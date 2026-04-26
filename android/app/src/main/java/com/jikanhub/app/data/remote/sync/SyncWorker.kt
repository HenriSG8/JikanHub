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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: JikanHubApi,
    private val taskDao: TaskDao,
    private val tokenManager: com.jikanhub.app.data.local.TokenManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 1. Get last sync time
            val lastSync = tokenManager.lastSyncTime.first()
            
            // 2. Get unsynced tasks from local DB
            val unsyncedTasks = taskDao.getUnsyncedTasks()
            
            // 3. Push to server
            val pushRequest = SyncRequest(tasks = unsyncedTasks.map { it.toDto() })
            val pushResponse = api.pushSync(pushRequest)
            
            // 4. Mark pushed tasks as synced
            if (unsyncedTasks.isNotEmpty()) {
                taskDao.markAsSynced(unsyncedTasks.map { it.id })
            }
            
            // 5. Pull from server (to get everything else)
            val lastSyncStr = lastSync ?: "1970-01-01T00:00:00Z"
            val pullResponse = api.pullSync(since = lastSyncStr)
            
            // 6. Save all server tasks to local DB
            val serverEntities = (pushResponse.tasks + pullResponse.tasks)
                .distinctBy { it.id }
                .map { it.toEntity() }
            
            if (serverEntities.isNotEmpty()) {
                taskDao.insertAll(serverEntities)
            }
            
            // 7. Save server timestamp for next sync
            tokenManager.setLastSyncTime(pullResponse.serverTimestamp)
            
            // 8. Cleanup
            taskDao.cleanupSyncedDeleted()
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
