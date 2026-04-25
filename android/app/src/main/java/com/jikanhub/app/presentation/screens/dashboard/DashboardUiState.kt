package com.jikanhub.app.presentation.screens.dashboard

import com.jikanhub.app.domain.model.Task
import java.time.LocalDate

data class DashboardUiState(
    val greeting: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val tasks: List<Task> = emptyList(),
    val userName: String = "Usuário",
    val currentTab: DashboardTab = DashboardTab.TASKS_OF_DAY,
    val isLoading: Boolean = false,
    val weekDays: List<DayItem> = emptyList(),
    val showCreateSheet: Boolean = false,
    val selectedTask: Task? = null,
    val isDarkMode: Boolean = true
)

data class DayItem(
    val date: LocalDate,
    val dayOfWeek: String,
    val dayNumber: Int,
    val isSelected: Boolean,
    val hasTasksToday: Boolean = false
)
