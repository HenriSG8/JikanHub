package com.jikanhub.app.presentation.screens.dashboard

import com.jikanhub.app.domain.model.Task
import com.jikanhub.app.R
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
    val isDarkMode: Boolean = true,
    val taskToEdit: Task? = null,
    val showTutorial: Boolean = false,
    val tutorialStep: Int = 0
)

enum class TutorialStep(val titleRes: Int, val descriptionRes: Int) {
    WELCOME(R.string.tutorial_welcome_title, R.string.tutorial_welcome_desc),
    TASKS(R.string.tutorial_tasks_title, R.string.tutorial_tasks_desc),
    ADD_TASK(R.string.tutorial_add_task_title, R.string.tutorial_add_task_desc),
    MENU(R.string.tutorial_menu_title, R.string.tutorial_menu_desc),
    THEME(R.string.tutorial_theme_title, R.string.tutorial_theme_desc)
}

data class DayItem(
    val date: LocalDate,
    val dayOfWeek: String,
    val dayNumber: Int,
    val isSelected: Boolean,
    val hasTasksToday: Boolean = false
)
