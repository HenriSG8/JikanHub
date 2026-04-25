package com.jikanhub.api.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : Table("users") {
    val id = varchar("id", 36)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255).default("")
    val name = varchar("name", 100).default("")
    val googleId = varchar("google_id", 255).nullable().uniqueIndex()
    val language = varchar("language", 5).default("pt-BR")
    val createdAt = datetime("created_at").default(LocalDateTime.now())

    override val primaryKey = PrimaryKey(id)
}

object Tasks : Table("tasks") {
    val id = varchar("id", 36)
    val userId = varchar("user_id", 36).references(Users.id)
    val title = varchar("title", 255)
    val description = text("description").default("")
    val dateTime = datetime("date_time")
    val priority = varchar("priority", 10)  // LOW, MEDIUM, HIGH
    val status = varchar("status", 15).default("PENDING")
    val reminderEnabled = bool("reminder_enabled").default(false)
    val reminderMessage = text("reminder_message").default("")
    val reminderOffsets = varchar("reminder_offsets", 255).default("")  // "0,15,720,1440"
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
    val isDeleted = bool("is_deleted").default(false)

    override val primaryKey = PrimaryKey(id)
}
