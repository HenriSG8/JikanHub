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
    private val updateTask: com.jikanhub.app.domain.usecase.UpdateTaskUseCase,
    private val clearAllTasks: com.jikanhub.app.domain.usecase.ClearAllTasksUseCase,
    private val syncTasks: com.jikanhub.app.domain.usecase.SyncTasksUseCase,
    private val alarmScheduler: com.jikanhub.app.notification.AlarmScheduler,
    private val tokenManager: com.jikanhub.app.data.local.TokenManager,
    private val application: android.app.Application
) : ViewModel() {

    private fun updateWidgets() {
        viewModelScope.launch {
            com.jikanhub.app.presentation.widgets.WidgetUpdater.updateAllWidgets(application)
        }
    }


    private var tasksJob: kotlinx.coroutines.Job? = null

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun selectTask(task: com.jikanhub.app.domain.model.Task?) {
        _uiState.update { it.copy(selectedTask = task) }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            deleteTask.invoke(taskId)
            selectTask(null)
            updateWidgets()
        }
    }

    // testNotification removed
    init {
        updateGreeting()
        loadUserName()
        loadThemePreference()
        checkTutorialStatus()
        triggerInitialSync()
    }

    private fun triggerInitialSync() {
        viewModelScope.launch {
            // Wait for token to be available
            tokenManager.token.first { it != null }
            
            _uiState.update { it.copy(isLoading = true) }
            syncTasks(forceForeground = true)
            
            // After sync, reload tasks from Room for the selected date
            selectDate(_uiState.value.selectedDate)
        }
    }

    fun logout() {
        viewModelScope.launch {
            clearAllTasks()
            tokenManager.clearAuthData()
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val newTheme = !_uiState.value.isDarkMode
            tokenManager.setDarkMode(newTheme)
        }
    }

    fun changeTab(tab: DashboardTab) {
        _uiState.update { it.copy(currentTab = tab) }
        if (tab == DashboardTab.TASKS_OF_DAY) {
            selectDate(LocalDate.now())
        }
    }

    private fun loadUserName() {
        viewModelScope.launch {
            tokenManager.userName.collect { name ->
                _uiState.update { it.copy(userName = name ?: "Usuário") }
            }
        }
    }

    private fun loadThemePreference() {
        viewModelScope.launch {
            tokenManager.isDarkMode.collect { isDark ->
                _uiState.update { it.copy(isDarkMode = isDark) }
            }
        }
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
            updateWidgets()
        }
    }

    fun toggleSubtask(task: com.jikanhub.app.domain.model.Task, subtaskId: String) {
        viewModelScope.launch {
            val updatedSubtasks = task.subtasks.map { 
                if (it.id == subtaskId) it.copy(isCompleted = !it.isCompleted) else it 
            }
            val updatedTask = task.copy(subtasks = updatedSubtasks)
            updateTask(updatedTask)
            
            // Also update the selected task in state to reflect change immediately in UI
            _uiState.update { it.copy(selectedTask = updatedTask) }
        }
    }

    fun showCreateSheet() {
        _uiState.update { it.copy(showCreateSheet = true, taskToEdit = null) }
    }

    fun editTask(task: com.jikanhub.app.domain.model.Task) {
        _uiState.update { it.copy(taskToEdit = task, showCreateSheet = true, selectedTask = null) }
    }

    fun rescheduleTask(task: com.jikanhub.app.domain.model.Task, newDateTime: java.time.LocalDateTime) {
        viewModelScope.launch {
            val updatedTask = task.copy(dateTime = newDateTime)
            updateTask(updatedTask)
            selectTask(null)
        }
    }

    fun clearTaskToEdit() {
        _uiState.update { it.copy(taskToEdit = null) }
    }

    fun hideCreateSheet() {
        _uiState.update { it.copy(showCreateSheet = false, taskToEdit = null) }
    }

    fun onTaskCreated() {
        hideCreateSheet()
        loadTasks(_uiState.value.selectedDate)
    }

    private fun loadTasks(date: LocalDate) {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
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

    private fun checkTutorialStatus() {
        viewModelScope.launch {
            tokenManager.isTutorialCompleted.collect { completed ->
                if (!completed) {
                    _uiState.update { it.copy(showTutorial = true) }
                }
            }
        }
    }

    fun nextTutorialStep() {
        val currentStep = _uiState.value.tutorialStep
        if (currentStep < TutorialStep.entries.size - 1) {
            _uiState.update { it.copy(tutorialStep = currentStep + 1) }
        } else {
            finishTutorial()
        }
    }

    fun skipTutorial() {
        finishTutorial()
    }

    private fun finishTutorial() {
        viewModelScope.launch {
            tokenManager.setTutorialCompleted(true)
            _uiState.update { it.copy(showTutorial = false) }
        }
    }
}
