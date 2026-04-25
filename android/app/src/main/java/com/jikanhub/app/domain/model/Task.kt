package com.jikanhub.app.domain.model

import java.time.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * Domain model representing a Task.
 * This is framework-independent — no Room or Retrofit annotations.
 */
@Serializable
data class Subtask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
)

data class Task(
    val id: String = "",
    val title: String,
    val description: String = "",
    val dateTime: LocalDateTime,
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val reminder: ReminderConfig = ReminderConfig(),
    val subtasks: List<Subtask> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val isSynced: Boolean = false
)
