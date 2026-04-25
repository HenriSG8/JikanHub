package com.jikanhub.app.data.mapper

import com.jikanhub.app.data.local.entity.TaskEntity
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

private fun parseOffsets(raw: String): List<ReminderOffset> {
    if (raw.isBlank()) return emptyList()
    return raw.split(",").mapNotNull { minuteStr ->
        val minutes = minuteStr.trim().toIntOrNull() ?: return@mapNotNull null
        ReminderOffset.entries.find { it.minutes == minutes }
    }
}
