package com.jikanhub.app.domain.usecase

import com.jikanhub.app.domain.model.TaskStatus
import com.jikanhub.app.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskStatusUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: String, status: TaskStatus) {
        repository.updateTaskStatus(taskId, status)
    }
}
