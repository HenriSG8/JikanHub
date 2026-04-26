package com.jikanhub.app.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import com.jikanhub.app.BuildConfig
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)
    private val TAG = "GoogleAuthManager"

    suspend fun signIn(): String? {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            Log.d(TAG, "Iniciando solicitação de credenciais...")
            
            // CredentialManager precisa da Activity para mostrar a UI
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )

            val credential = result.credential
            if (credential is GoogleIdTokenCredential) {
                // Token obtido com sucesso
                return credential.idToken
            }
            Log.w(TAG, "Tipo de credencial inesperado: ${credential.type}")
            null
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Erro do CredentialManager: ${e.message}", e)

        } catch (e: Exception) {
            Log.e(TAG, "Erro inesperado no login com Google: ${e.message}", e)
        }
        return null
    }
}
