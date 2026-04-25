package com.jikanhub.app.domain.model

import java.time.LocalDateTime

/**
 * Domain model representing a Task.
 * This is framework-independent — no Room or Retrofit annotations.
 */
data class Task(
    val id: String = "",
    val title: String,
    val description: String = "",
    val dateTime: LocalDateTime,
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val reminder: ReminderConfig = ReminderConfig(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val isSynced: Boolean = false
)
