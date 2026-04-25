package com.jikanhub.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jikanhub.app.data.local.dao.TaskDao
import com.jikanhub.app.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 2,
    exportSchema = true
)
abstract class JikanHubDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
