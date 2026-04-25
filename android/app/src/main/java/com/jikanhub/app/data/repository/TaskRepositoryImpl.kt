package com.jikanhub.app.data.repository

import androidx.work.*
import com.jikanhub.app.data.local.dao.TaskDao
import com.jikanhub.app.data.mapper.toDomain
import com.jikanhub.app.data.mapper.toEntity
import com.jikanhub.app.data.remote.sync.SyncWorker
import com.jikanhub.app.domain.model.Task
import com.jikanhub.app.domain.model.TaskStatus
import com.jikanhub.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

/**
 * Offline-first repository implementation.
 * All writes go to Room first, then sync to server via WorkManager.
 */
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val workManager: WorkManager
) : TaskRepository {

    override fun getTasksByDate(date: LocalDate): Flow<List<Task>> {
        val startOfDay = date.atStartOfDay()
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.atTime(LocalTime.MAX)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return taskDao.getTasksByDate(startOfDay, endOfDay)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTaskById(id: String): Flow<Task?> {
        return taskDao.getTaskById(id).map { it?.toDomain() }
    }

    override fun getTasksWithActiveReminders(): Flow<List<Task>> {
        return taskDao.getTasksWithActiveReminders()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun createTask(task: Task): Task {
        val newTask = task.copy(
            id = if (task.id.isBlank()) UUID.randomUUID().toString() else task.id,
            isSynced = false
        )
        taskDao.insertTask(newTask.toEntity())
        syncWithServer()
        return newTask
    }

    override suspend fun updateTask(task: Task) {
        val updated = task.copy(
            updatedAt = java.time.LocalDateTime.now(),
            isSynced = false
        )
        taskDao.updateTask(updated.toEntity())
        syncWithServer()
    }

    override suspend fun updateTaskStatus(id: String, status: TaskStatus) {
        taskDao.updateTaskStatus(
            id = id,
            status = status.name,
            updatedAt = System.currentTimeMillis()
        )
        syncWithServer()
    }

    override suspend fun deleteTask(id: String) {
        taskDao.softDeleteTask(id, System.currentTimeMillis())
        syncWithServer()
    }

    override suspend fun syncWithServer() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, java.util.concurrent.TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniqueWork(
            "jikanhub_sync",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
}
