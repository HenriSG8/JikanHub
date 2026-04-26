package com.jikanhub.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val description: String = "",
    val dateTime: String,            // ISO-8601 format
    val priority: String,
    val status: String,
    val reminderEnabled: Boolean = false,
    val reminderMessage: String = "",
    val reminderOffsets: List<Int> = emptyList(),
    val subtasks: List<SubtaskDto> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
    val isDeleted: Boolean = false
)

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String = "",
    val dateTime: String,
    val priority: String,
    val reminderEnabled: Boolean = false,
    val reminderMessage: String = "",
    val reminderOffsets: List<Int> = emptyList(),
    val subtasks: List<SubtaskDto> = emptyList()
)

@Serializable
data class SubtaskDto(
    val id: String,
    val title: String,
    val isCompleted: Boolean
)

@Serializable
data class UpdateStatusRequest(
    val status: String
)

@Serializable
data class SyncRequest(
    val tasks: List<TaskDto>
)

@Serializable
data class SyncResponse(
    val tasks: List<TaskDto>,
    val serverTimestamp: String
)

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
    val name: String? = null
)

@Serializable
data class GoogleAuthRequest(
    val idToken: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String
)

@Serializable
data class AiSuggestRequest(
    val title: String,
    val description: String = ""
)

@Serializable
data class AiSuggestResponse(
    val subtasks: List<String>
)
