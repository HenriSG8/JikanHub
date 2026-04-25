package com.jikanhub.app.domain.usecase

import com.jikanhub.app.domain.repository.TaskRepository
import com.jikanhub.app.notification.AlarmScheduler
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(taskId: String) {
        alarmScheduler.cancelAllForTask(taskId)
        repository.deleteTask(taskId)
    }
}
