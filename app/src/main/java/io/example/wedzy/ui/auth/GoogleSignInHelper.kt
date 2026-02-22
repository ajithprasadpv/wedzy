package io.example.wedzy.ui.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.security.MessageDigest
import java.util.UUID

class GoogleSignInHelper(private val context: Context) {
    
    private val credentialManager = CredentialManager.create(context)
    
    suspend fun signIn(webClientId: String): Result<String> {
        return try {
            Log.d(TAG, "Initiating Google Sign-In")
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .setNonce(generateNonce())
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            Log.d(TAG, "Requesting credentials from Credential Manager")
            val result = credentialManager.getCredential(
                request = request,
                context = context as android.app.Activity
            )
            
            Log.d(TAG, "Credentials received, processing result")
            handleSignInResult(result)
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "User cancelled Google Sign-In")
            Result.failure(GoogleSignInCancelledException("Sign-in was cancelled"))
        } catch (e: NoCredentialException) {
            Log.e(TAG, "No Google credentials available", e)
            Result.failure(GoogleSignInNoCredentialsException("No Google account found. Please add a Google account to your device."))
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential retrieval failed: ${e.message}", e)
            Result.failure(GoogleSignInCredentialException("Failed to retrieve Google credentials. Please try again."))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Google Sign-In: ${e.message}", e)
            Result.failure(GoogleSignInUnexpectedException("An unexpected error occurred. Please try again."))
        }
    }
    
    private fun handleSignInResult(result: GetCredentialResponse): Result<String> {
        val credential = result.credential
        
        return when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        Result.success(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Result.failure(e)
                    }
                } else {
                    Result.failure(Exception("Unexpected credential type"))
                }
            }
            else -> Result.failure(Exception("Unexpected credential type"))
        }
    }
    
    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    
    companion object {
        private const val TAG = "GoogleSignInHelper"
    }
}

// Custom exceptions for better error handling
class GoogleSignInCancelledException(message: String) : Exception(message)
class GoogleSignInNoCredentialsException(message: String) : Exception(message)
class GoogleSignInCredentialException(message: String) : Exception(message)
class GoogleSignInUnexpectedException(message: String) : Exception(message)
