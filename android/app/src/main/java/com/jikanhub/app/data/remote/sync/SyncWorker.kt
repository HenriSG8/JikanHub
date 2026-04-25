package com.jikanhub.app.data.remote.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jikanhub.app.data.local.dao.TaskDao
import com.jikanhub.app.data.mapper.toDto
import com.jikanhub.app.data.mapper.toEntity
import com.jikanhub.app.data.remote.api.JikanHubApi
import com.jikanhub.app.data.remote.dto.SyncRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: JikanHubApi,
    private val taskDao: TaskDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 1. Get unsynced tasks from local DB
            val unsyncedTasks = taskDao.getUnsyncedTasks()
            
            // 2. Push to server
            val syncRequest = SyncRequest(tasks = unsyncedTasks.map { it.toDto() })
            val response = api.pushSync(syncRequest)
            
            // 3. Mark pushed tasks as synced
            if (unsyncedTasks.isNotEmpty()) {
                taskDao.markAsSynced(unsyncedTasks.map { it.id })
            }
            
            // 4. Save server tasks to local DB (Pull/Sync)
            val serverEntities = response.tasks.map { it.toEntity() }
            taskDao.insertAll(serverEntities)
            
            // 5. Cleanup (optional: could delete local tasks that were soft-deleted on server)
            taskDao.cleanupSyncedDeleted()
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
