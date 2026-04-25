package com.jikanhub.app.data.remote.api

import com.jikanhub.app.data.remote.dto.*
import retrofit2.http.*

interface JikanHubApi {

    // === Auth ===
    @POST("api/auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("api/auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): AuthResponse

    // === Tasks ===
    @GET("api/tasks")
    suspend fun getTasksByDate(@Query("date") date: String): List<TaskDto>

    @POST("api/tasks")
    suspend fun createTask(@Body request: CreateTaskRequest): TaskDto

    @PUT("api/tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body task: TaskDto): TaskDto

    @PATCH("api/tasks/{id}/status")
    suspend fun updateTaskStatus(
        @Path("id") id: String,
        @Body request: UpdateStatusRequest
    ): TaskDto

    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String)

    // === Sync ===
    @POST("api/sync")
    suspend fun pushSync(@Body request: SyncRequest): SyncResponse

    @GET("api/sync")
    suspend fun pullSync(@Query("since") since: String): SyncResponse
}
