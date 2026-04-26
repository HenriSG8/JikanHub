package com.jikanhub.app.domain.usecase

import com.jikanhub.app.domain.repository.TaskRepository
import javax.inject.Inject

class SyncTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(forceForeground: Boolean = false) {
        if (forceForeground) {
            repository.fetchEverything()
        } else {
            repository.syncWithServer()
        }
    }
}
