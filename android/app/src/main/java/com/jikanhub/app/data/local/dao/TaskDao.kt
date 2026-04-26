package com.jikanhub.app.data.local.dao

import androidx.room.*
import com.jikanhub.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY dateTime DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks 
        WHERE dateTime BETWEEN :startOfDay AND :endOfDay 
        AND isDeleted = 0
        ORDER BY dateTime ASC
    """)
    fun getTasksByDate(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id AND isDeleted = 0")
    fun getTaskById(id: String): Flow<TaskEntity?>

    @Query("""
        SELECT * FROM tasks 
        WHERE reminderEnabled = 1 AND status != 'COMPLETED' AND isDeleted = 0
    """)
    fun getTasksWithActiveReminders(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isSynced = 0")
    suspend fun getUnsyncedTasks(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("""
        UPDATE tasks 
        SET status = :status, updatedAt = :updatedAt, isSynced = 0 
        WHERE id = :id
    """)
    suspend fun updateTaskStatus(id: String, status: String, updatedAt: Long)

    @Query("""
        UPDATE tasks 
        SET isDeleted = 1, updatedAt = :updatedAt, isSynced = 0 
        WHERE id = :id
    """)
    suspend fun softDeleteTask(id: String, updatedAt: Long)

    @Query("UPDATE tasks SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<String>)

    @Query("DELETE FROM tasks WHERE isDeleted = 1 AND isSynced = 1")
    suspend fun cleanupSyncedDeleted()
}
