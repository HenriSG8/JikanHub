package com.jikanhub.app.domain.usecase

import com.jikanhub.app.domain.repository.TaskRepository
import javax.inject.Inject

class ClearAllTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke() {
        repository.clearAllTasks()
    }
}
