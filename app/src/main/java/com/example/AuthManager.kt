package com.example

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

object AuthManager {
    private const val TAG = "AuthManager"

    suspend fun signInWithGoogle(context: Context): Boolean {
        val webClientIdRes = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (webClientIdRes == 0) {
            Log.e(TAG, "default_web_client_id not found. Missing google-services.json?")
            return false
        }
        
        val webClientId = context.getString(webClientIdRes)
        val credentialManager = CredentialManager.create(context)

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.joinToString("") { "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setNonce(hashedNonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            
            if (credential is GoogleIdTokenCredential) {
                val idToken = credential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
                authResult.user != null
            } else {
                Log.w(TAG, "Unexpected credential type")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In failed", e)
            false
        }
    }
    
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
    
    fun isUserSignedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
    
    fun getUserEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email
    }
}
