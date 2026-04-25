package com.jikanhub.app.presentation.screens.createtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikanhub.app.data.remote.api.JikanHubApi
import com.jikanhub.app.data.remote.dto.AiSuggestRequest
import com.jikanhub.app.domain.model.*
import com.jikanhub.app.domain.usecase.CreateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase,
    private val api: JikanHubApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    private val _taskCreated = MutableSharedFlow<Unit>()
    val taskCreated: SharedFlow<Unit> = _taskCreated.asSharedFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateDate(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
    }

    fun updateTime(time: LocalTime) {
        _uiState.update { it.copy(time = time) }
    }

    fun updatePriority(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun toggleReminder(enabled: Boolean) {
        _uiState.update { it.copy(reminderEnabled = enabled) }
    }

    fun toggleOffset(offset: ReminderOffset) {
        _uiState.update { state ->
            val newOffsets = state.selectedOffsets.toMutableSet()
            if (offset in newOffsets) newOffsets.remove(offset)
            else newOffsets.add(offset)
            state.copy(selectedOffsets = newOffsets)
        }
    }

    fun updateReminderMessage(message: String) {
        _uiState.update { it.copy(reminderMessage = message) }
    }

    fun addSubtask() {
        _uiState.update { state ->
            state.copy(subtasks = state.subtasks + SubtaskDraft())
        }
    }

    fun removeSubtask(id: String) {
        _uiState.update { state ->
            state.copy(subtasks = state.subtasks.filter { it.id != id })
        }
    }

    fun updateSubtaskTitle(id: String, title: String) {
        _uiState.update { state ->
            state.copy(
                subtasks = state.subtasks.map { 
                    if (it.id == id) it.copy(title = title) else it 
                }
            )
        }
    }

    fun requestAiSuggestions() {
        val state = _uiState.value
        if (state.title.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isAiLoading = true, aiError = null) }
            try {
                val response = api.suggestSubtasks(
                    AiSuggestRequest(
                        title = state.title.trim(),
                        description = state.description.trim()
                    )
                )
                val newSubtasks = response.subtasks.map { suggestion ->
                    SubtaskDraft(title = suggestion)
                }
                _uiState.update { current ->
                    current.copy(
                        isAiLoading = false,
                        subtasks = current.subtasks + newSubtasks
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAiLoading = false,
                        aiError = "Não foi possível gerar sugestões"
                    )
                }
            }
        }
    }


    fun saveTask() {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "title_required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val task = Task(
                    title = state.title.trim(),
                    description = state.description.trim(),
                    dateTime = LocalDateTime.of(state.date, state.time),
                    priority = state.priority,
                    reminder = ReminderConfig(
                        enabled = state.reminderEnabled,
                        message = state.reminderMessage.trim(),
                        offsets = if (state.reminderEnabled) {
                            state.selectedOffsets.toList()
                        } else emptyList()
                    ),
                    subtasks = state.subtasks
                        .filter { it.title.isNotBlank() }
                        .map { Subtask(id = it.id, title = it.title.trim(), isCompleted = false) }
                )
                createTaskUseCase(task)
                _uiState.value = CreateTaskUiState() // Reseta o estado (limpa os campos)
                _taskCreated.emit(Unit)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = e.message)
                }
            }
        }
    }
}
