package com.jikanhub.api.db

import com.jikanhub.api.models.SubtaskDto
import com.jikanhub.api.models.TaskDto
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll

fun ResultRow.toTaskDto(): TaskDto {
    val taskId = this[Tasks.id]
    
    // Fetch subtasks for this task
    val subtasks = Subtasks.selectAll().where { Subtasks.taskId eq taskId }
        .map { row ->
            SubtaskDto(
                id = row[Subtasks.id],
                title = row[Subtasks.title],
                isCompleted = row[Subtasks.isCompleted]
            )
        }

    val offsets = this[Tasks.reminderOffsets]
        .split(",")
        .filter { it.isNotBlank() }
        .map { it.trim().toInt() }

    return TaskDto(
        id = taskId,
        title = this[Tasks.title],
        description = this[Tasks.description],
        dateTime = this[Tasks.dateTime].toString(),
        priority = this[Tasks.priority],
        status = this[Tasks.status],
        reminderEnabled = this[Tasks.reminderEnabled],
        reminderMessage = this[Tasks.reminderMessage],
        reminderOffsets = offsets,
        subtasks = subtasks,
        createdAt = this[Tasks.createdAt].toString(),
        updatedAt = this[Tasks.updatedAt].toString()
    )
}
