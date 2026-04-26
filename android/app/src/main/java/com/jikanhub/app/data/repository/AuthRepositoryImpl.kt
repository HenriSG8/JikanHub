package com.jikanhub.app.data.repository

import com.jikanhub.app.data.local.TokenManager
import com.jikanhub.app.data.remote.api.JikanHubApi
import com.jikanhub.app.data.remote.dto.AuthRequest
import com.jikanhub.app.data.remote.dto.AuthResponse
import com.jikanhub.app.data.remote.dto.GoogleAuthRequest
import com.jikanhub.app.domain.repository.AuthRepository
import com.jikanhub.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: JikanHubApi,
    private val tokenManager: TokenManager,
    private val taskRepository: TaskRepository
) : AuthRepository {

    override suspend fun login(request: AuthRequest): Result<AuthResponse> {
        return try {
            val response = api.login(request)
            tokenManager.saveAuthData(response.token, response.user.name)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(request: AuthRequest): Result<AuthResponse> {
        return try {
            val response = api.register(request)
            tokenManager.saveAuthData(response.token, response.user.name)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(request: GoogleAuthRequest): Result<AuthResponse> {
        return try {
            val response = api.googleAuth(request)
            tokenManager.saveAuthData(response.token, response.user.name)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        taskRepository.clearAllTasks()
        tokenManager.clearAuthData()
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.token.first() != null
    }
}
