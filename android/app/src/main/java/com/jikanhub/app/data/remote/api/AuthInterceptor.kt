package com.jikanhub.app.data.remote.api

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: com.jikanhub.app.data.local.TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Skip auth for login/register endpoints
        if (request.url.encodedPath.contains("/auth/")) {
            return chain.proceed(request)
        }

        val token = runBlocking {
            tokenManager.token.first()
        }

        val authenticatedRequest = if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(authenticatedRequest)
    }
}
