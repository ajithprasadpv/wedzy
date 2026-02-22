package io.example.wedzy.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthState {
    object Loading : AuthState()
    object NotAuthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    val isLoggedIn: Boolean
        get() = auth.currentUser != null
    
    val authState: Flow<AuthState> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            trySend(
                if (user != null) AuthState.Authenticated(user)
                else AuthState.NotAuthenticated
            )
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
    
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User creation failed")
            
            // Update display name
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(profileUpdates).await()
            
            // Create user document in Firestore
            createUserDocument(user.uid, displayName, email)
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Sign in failed")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            Log.d(TAG, "Starting Google Sign-In with token")
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: throw GoogleSignInException("User object is null after authentication")
            
            Log.d(TAG, "Google Sign-In successful for user: ${user.uid}")
            
            // Create user document if new user
            if (result.additionalUserInfo?.isNewUser == true) {
                Log.d(TAG, "Creating user document for new user")
                createUserDocument(user.uid, user.displayName ?: "", user.email ?: "")
            }
            
            Result.success(user)
        } catch (e: FirebaseAuthException) {
            Log.e(TAG, "Firebase Auth error during Google Sign-In: ${e.errorCode}", e)
            Result.failure(GoogleSignInException(getFirebaseAuthErrorMessage(e.errorCode), e))
        } catch (e: IOException) {
            Log.e(TAG, "Network error during Google Sign-In", e)
            Result.failure(GoogleSignInException("Network error. Please check your internet connection and try again.", e))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Google Sign-In: ${e.message}", e)
            Result.failure(GoogleSignInException("An unexpected error occurred. Please try again.", e))
        }
    }
    
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun signOut() {
        auth.signOut()
    }
    
    private suspend fun createUserDocument(userId: String, displayName: String, email: String) {
        val userData = hashMapOf(
            "displayName" to displayName,
            "email" to email,
            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "ownedWeddings" to emptyList<String>(),
            "joinedWeddings" to emptyList<String>()
        )
        
        firestore.collection("users")
            .document(userId)
            .set(userData, com.google.firebase.firestore.SetOptions.merge())
            .await()
    }
    
    private fun getFirebaseAuthErrorMessage(errorCode: String): String {
        return when (errorCode) {
            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your internet connection."
            "ERROR_USER_DISABLED" -> "This account has been disabled."
            "ERROR_USER_TOKEN_EXPIRED" -> "Your session has expired. Please sign in again."
            "ERROR_INVALID_CREDENTIAL" -> "Invalid credentials. Please try again."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "An account already exists with the same email but different sign-in credentials."
            else -> "Authentication failed. Please try again."
        }
    }
    
    companion object {
        private const val TAG = "FirebaseAuthRepository"
    }
}

class GoogleSignInException(message: String, cause: Throwable? = null) : Exception(message, cause)
