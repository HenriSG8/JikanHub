package com.jikanhub.app.domain.repository

import com.jikanhub.app.data.remote.dto.AuthRequest
import com.jikanhub.app.data.remote.dto.AuthResponse
import com.jikanhub.app.data.remote.dto.GoogleAuthRequest

interface AuthRepository {
    suspend fun login(request: AuthRequest): Result<AuthResponse>
    suspend fun register(request: AuthRequest): Result<AuthResponse>
    suspend fun loginWithGoogle(request: GoogleAuthRequest): Result<AuthResponse>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun deleteAccount(): Result<Unit>
}
