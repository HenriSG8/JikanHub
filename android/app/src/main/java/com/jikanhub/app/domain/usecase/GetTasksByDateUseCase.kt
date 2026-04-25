package com.jikanhub.app.domain.usecase

import com.jikanhub.app.domain.model.Task
import com.jikanhub.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetTasksByDateUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<Task>> {
        return repository.getTasksByDate(date)
    }
}
