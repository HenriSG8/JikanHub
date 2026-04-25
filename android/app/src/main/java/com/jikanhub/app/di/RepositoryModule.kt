package com.jikanhub.app.di

import com.jikanhub.app.data.repository.TaskRepositoryImpl
import com.jikanhub.app.domain.repository.TaskRepository
import com.jikanhub.app.notification.AlarmScheduler
import com.jikanhub.app.notification.AlarmSchedulerImpl
import com.jikanhub.app.domain.ai.AiAssistant
import com.jikanhub.app.ai.AiAssistantStub
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(impl: AlarmSchedulerImpl): AlarmScheduler

    @Binds
    @Singleton
    abstract fun bindAiAssistant(impl: AiAssistantStub): AiAssistant

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: com.jikanhub.app.data.repository.AuthRepositoryImpl): com.jikanhub.app.domain.repository.AuthRepository
}
