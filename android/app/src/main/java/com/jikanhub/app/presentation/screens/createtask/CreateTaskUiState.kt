package com.jikanhub.app.presentation.screens.createtask

import com.jikanhub.app.domain.model.Priority
import com.jikanhub.app.domain.model.ReminderOffset
import java.time.LocalDate
import java.time.LocalTime

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now().plusHours(1).withMinute(0),
    val priority: Priority = Priority.MEDIUM,
    val reminderEnabled: Boolean = false,
    val selectedOffsets: Set<ReminderOffset> = setOf(ReminderOffset.AT_TIME),
    val reminderMessage: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)
