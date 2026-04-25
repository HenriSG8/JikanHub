package com.jikanhub.app.domain.usecase

import com.jikanhub.app.domain.model.Task
import com.jikanhub.app.domain.repository.TaskRepository
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        val updatedTask = task.copy(updatedAt = LocalDateTime.now(), isSynced = false)
        repository.updateTask(updatedTask)
    }
}
