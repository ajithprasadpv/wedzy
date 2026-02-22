package io.example.wedzy.data.auth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSession @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val ANONYMOUS_USER_ID_KEY = "anonymous_user_id"
    
    fun getCurrentUserId(): String {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (firebaseUser != null) {
            firebaseUser.uid
        } else {
            getOrCreateAnonymousUserId()
        }
    }
    
    fun isUserSignedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
    
    fun isAnonymousUser(): Boolean {
        return FirebaseAuth.getInstance().currentUser == null
    }
    
    private fun getOrCreateAnonymousUserId(): String {
        var anonymousId = prefs.getString(ANONYMOUS_USER_ID_KEY, null)
        if (anonymousId == null) {
            anonymousId = "anon_${UUID.randomUUID()}"
            prefs.edit().putString(ANONYMOUS_USER_ID_KEY, anonymousId).apply()
        }
        return anonymousId
    }
    
    fun clearAnonymousSession() {
        prefs.edit().remove(ANONYMOUS_USER_ID_KEY).apply()
    }
    
    fun getFirebaseUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}
