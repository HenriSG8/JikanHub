package com.jikanhub.app.data.mapper

import com.jikanhub.app.data.local.entity.TaskEntity
import com.jikanhub.app.data.remote.dto.TaskDto
import com.jikanhub.app.domain.model.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Maps between Room TaskEntity and Domain Task model.
 */
fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(dateTime), ZoneId.systemDefault()
        ),
        priority = Priority.valueOf(priority),
        status = TaskStatus.valueOf(status),
        reminder = ReminderConfig(
            enabled = reminderEnabled,
            message = reminderMessage,
            offsets = parseOffsets(reminderOffsets)
        ),
        createdAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()
        ),
        updatedAt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault()
        ),
        isSynced = isSynced
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        dateTime = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        priority = priority.name,
        status = status.name,
        reminderEnabled = reminder.enabled,
        reminderMessage = reminder.message,
        reminderOffsets = reminder.offsets.joinToString(",") { it.minutes.toString() },
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = updatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isSynced = isSynced
    )
}

fun TaskEntity.toDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        dateTime = java.time.Instant.ofEpochMilli(dateTime)
            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().toString(),
        priority = priority,
        status = status,
        reminderEnabled = reminderEnabled,
        reminderMessage = reminderMessage,
        reminderOffsets = reminderOffsets.split(",")
            .filter { it.isNotBlank() }
            .map { it.toInt() },
        createdAt = java.time.Instant.ofEpochMilli(createdAt)
            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().toString(),
        updatedAt = java.time.Instant.ofEpochMilli(updatedAt)
            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().toString()
    )
}

fun TaskDto.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        dateTime = java.time.LocalDateTime.parse(dateTime)
            .atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
        priority = priority,
        status = status,
        reminderEnabled = reminderEnabled,
        reminderMessage = reminderMessage,
        reminderOffsets = reminderOffsets.joinToString(","),
        createdAt = java.time.LocalDateTime.parse(createdAt)
            .atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
        updatedAt = java.time.LocalDateTime.parse(updatedAt)
            .atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
        isSynced = true
    )
}


private fun parseOffsets(raw: String): List<ReminderOffset> {
    if (raw.isBlank()) return emptyList()
    return raw.split(",").mapNotNull { minuteStr ->
        val minutes = minuteStr.trim().toIntOrNull() ?: return@mapNotNull null
        ReminderOffset.entries.find { it.minutes == minutes }
    }
}
