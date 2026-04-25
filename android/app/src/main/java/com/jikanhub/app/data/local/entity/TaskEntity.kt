package com.jikanhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String = "",
    val dateTime: Long,                // Epoch millis
    val priority: String,              // "LOW", "MEDIUM", "HIGH"
    val status: String,                // "PENDING", "COMPLETED", "POSTPONED"
    val reminderEnabled: Boolean = false,
    val reminderMessage: String = "",
    val reminderOffsets: String = "",   // Comma-separated minutes: "0,15,720"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false      // Soft delete for sync
)
