package com.jikanhub.app.domain.repository

import com.jikanhub.app.domain.model.Task
import com.jikanhub.app.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository contract for Task operations.
 * Implementation handles offline-first strategy.
 */
interface TaskRepository {
    fun getTasksByDate(date: LocalDate): Flow<List<Task>>
    fun getTaskById(id: String): Flow<Task?>
    fun getTasksWithActiveReminders(): Flow<List<Task>>
    suspend fun createTask(task: Task): Task
    suspend fun updateTask(task: Task)
    suspend fun updateTaskStatus(id: String, status: TaskStatus)
    suspend fun deleteTask(id: String)
    suspend fun syncWithServer()
}
