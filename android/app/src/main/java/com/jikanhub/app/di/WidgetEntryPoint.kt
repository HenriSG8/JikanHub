package com.jikanhub.app.di

import com.jikanhub.app.data.local.dao.TaskDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun taskDao(): TaskDao
}
