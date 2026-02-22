package io.example.wedzy.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import io.example.wedzy.data.model.CollaboratorRole
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class WeddingInvite(
    val weddingId: String,
    val code: String,
    val role: String,
    val brideName: String = "",
    val groomName: String = ""
)

data class FirebaseWedding(
    val id: String = "",
    val brideName: String = "",
    val groomName: String = "",
    val weddingDate: Long = 0,
    val ownerId: String = "",
    val collaboratorIds: List<String> = emptyList()
)

@Singleton
class FirebaseCollaborationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    private val weddingsCollection = firestore.collection("weddings")
    private val usersCollection = firestore.collection("users")
    
    val currentUserId: String?
        get() = auth.currentUser?.uid
    
    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null
    
    suspend fun createWedding(
        brideName: String,
        groomName: String,
        weddingDate: Long
    ): String {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        
        val weddingData = hashMapOf(
            "brideName" to brideName,
            "groomName" to groomName,
            "weddingDate" to weddingDate,
            "ownerId" to userId,
            "collaboratorIds" to listOf(userId),
            "createdAt" to FieldValue.serverTimestamp()
        )
        
        val docRef = weddingsCollection.add(weddingData).await()
        
        // Update user's owned weddings
        usersCollection.document(userId).set(
            hashMapOf(
                "ownedWeddings" to FieldValue.arrayUnion(docRef.id),
                "joinedWeddings" to FieldValue.arrayUnion(docRef.id)
            ),
            com.google.firebase.firestore.SetOptions.merge()
        ).await()
        
        return docRef.id
    }
    
    suspend fun createInviteCode(
        weddingId: String,
        invitedName: String,
        invitedPhone: String,
        role: CollaboratorRole
    ): String {
        val code = generateInviteCode()
        
        val inviteData = hashMapOf(
            "code" to code,
            "invitedName" to invitedName,
            "invitedPhone" to invitedPhone,
            "role" to role.name,
            "isUsed" to false,
            "createdAt" to FieldValue.serverTimestamp(),
            "createdBy" to (auth.currentUser?.uid ?: "")
        )
        
        weddingsCollection
            .document(weddingId)
            .collection("inviteCodes")
            .document(code)
            .set(inviteData)
            .await()
        
        return code
    }
    
    suspend fun validateInviteCode(code: String): WeddingInvite? {
        // Query all weddings for this invite code
        val snapshot = firestore.collectionGroup("inviteCodes")
            .whereEqualTo("code", code)
            .whereEqualTo("isUsed", false)
            .get()
            .await()
        
        val inviteDoc = snapshot.documents.firstOrNull() ?: return null
        
        val weddingId = inviteDoc.reference.parent.parent?.id ?: return null
        
        // Get wedding details
        val weddingDoc = weddingsCollection.document(weddingId).get().await()
        
        return WeddingInvite(
            weddingId = weddingId,
            code = code,
            role = inviteDoc.getString("role") ?: "FRIEND",
            brideName = weddingDoc.getString("brideName") ?: "",
            groomName = weddingDoc.getString("groomName") ?: ""
        )
    }
    
    suspend fun joinWedding(weddingId: String, inviteCode: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        
        try {
            firestore.runBatch { batch ->
                // Mark code as used
                val codeRef = weddingsCollection
                    .document(weddingId)
                    .collection("inviteCodes")
                    .document(inviteCode)
                batch.update(codeRef, mapOf(
                    "isUsed" to true,
                    "usedBy" to userId,
                    "usedAt" to FieldValue.serverTimestamp()
                ))
                
                // Add user to wedding collaborators
                val weddingRef = weddingsCollection.document(weddingId)
                batch.update(weddingRef, mapOf(
                    "collaboratorIds" to FieldValue.arrayUnion(userId)
                ))
                
                // Update user's joined weddings
                val userRef = usersCollection.document(userId)
                batch.set(userRef, mapOf(
                    "joinedWeddings" to FieldValue.arrayUnion(weddingId)
                ), com.google.firebase.firestore.SetOptions.merge())
            }.await()
            
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    suspend fun getUserWeddings(): List<FirebaseWedding> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        
        val snapshot = weddingsCollection
            .whereArrayContains("collaboratorIds", userId)
            .get()
            .await()
        
        return snapshot.documents.map { doc ->
            FirebaseWedding(
                id = doc.id,
                brideName = doc.getString("brideName") ?: "",
                groomName = doc.getString("groomName") ?: "",
                weddingDate = doc.getLong("weddingDate") ?: 0,
                ownerId = doc.getString("ownerId") ?: "",
                collaboratorIds = (doc.get("collaboratorIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            )
        }
    }
    
    suspend fun syncWeddingToFirebase(
        localWeddingId: Long,
        brideName: String,
        groomName: String,
        weddingDate: Long
    ): String? {
        return try {
            createWedding(brideName, groomName, weddingDate)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..10)
            .map { chars.random() }
            .joinToString("")
    }
}
