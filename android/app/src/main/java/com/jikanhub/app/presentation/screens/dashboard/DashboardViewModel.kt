package com.jikanhub.app.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikanhub.app.domain.model.TaskStatus
import com.jikanhub.app.domain.usecase.GetTasksByDateUseCase
import com.jikanhub.app.domain.usecase.UpdateTaskStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getTasksByDate: GetTasksByDateUseCase,
    private val updateTaskStatus: UpdateTaskStatusUseCase,
    private val deleteTask: com.jikanhub.app.domain.usecase.DeleteTaskUseCase,
    private val alarmScheduler: com.jikanhub.app.notification.AlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun selectTask(task: com.jikanhub.app.domain.model.Task?) {
        _uiState.update { it.copy(selectedTask = task) }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            deleteTask.invoke(taskId)
            selectTask(null)
        }
    }

    fun testNotification() {
        viewModelScope.launch {
            val testTask = com.jikanhub.app.domain.model.Task(
                id = "test_id",
                title = "Teste de Notificação",
                description = "Lembrete proativo funcionando!",
                dateTime = java.time.LocalDateTime.now().plusSeconds(10),
                priority = com.jikanhub.app.domain.model.Priority.HIGH,
                reminder = com.jikanhub.app.domain.model.ReminderConfig(
                    enabled = true,
                    message = "Lembrete JikanHub",
                    offsets = listOf(com.jikanhub.app.domain.model.ReminderOffset.AT_TIME)
                )
            )
            
            // Dispara o alarme diretamente para o teste
            alarmScheduler.schedule(
                taskId = testTask.id,
                triggerAtMillis = System.currentTimeMillis() + 5000, // 5 segundos
                title = testTask.title,
                message = testTask.reminder.message,
                offsetMinutes = 0
            )
        }
    }

    init {
        selectDate(LocalDate.now())
        updateGreeting()
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, isLoading = true) }
        generateWeekDays(date)
        loadTasks(date)
    }

    fun toggleTaskComplete(taskId: String, currentStatus: TaskStatus) {
        viewModelScope.launch {
            val newStatus = if (currentStatus == TaskStatus.COMPLETED) {
                TaskStatus.PENDING
            } else {
                TaskStatus.COMPLETED
            }
            updateTaskStatus(taskId, newStatus)
        }
    }

    fun showCreateSheet() {
        _uiState.update { it.copy(showCreateSheet = true) }
    }

    fun hideCreateSheet() {
        _uiState.update { it.copy(showCreateSheet = false) }
    }

    fun onTaskCreated() {
        hideCreateSheet()
        loadTasks(_uiState.value.selectedDate)
    }

    private fun loadTasks(date: LocalDate) {
        viewModelScope.launch {
            getTasksByDate(date).collect { tasks ->
                _uiState.update {
                    it.copy(tasks = tasks, isLoading = false)
                }
            }
        }
    }

    private fun generateWeekDays(selectedDate: LocalDate) {
        val startOfWeek = selectedDate.with(DayOfWeek.MONDAY)
        val days = (0L..13L).map { offset ->
            val date = startOfWeek.plusDays(offset)
            DayItem(
                date = date,
                dayOfWeek = date.dayOfWeek.getDisplayName(
                    TextStyle.SHORT, Locale.getDefault()
                ),
                dayNumber = date.dayOfMonth,
                isSelected = date == selectedDate
            )
        }
        _uiState.update { it.copy(weekDays = days) }
    }

    private fun updateGreeting() {
        val hour = LocalTime.now().hour
        val greeting = when {
            hour < 12 -> "greeting_morning"
            hour < 18 -> "greeting_afternoon"
            else -> "greeting_evening"
        }
        _uiState.update { it.copy(greeting = greeting) }
    }
}
