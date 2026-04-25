package com.jikanhub.app.presentation.screens.createtask

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jikanhub.app.R
import com.jikanhub.app.domain.model.Priority
import com.jikanhub.app.domain.model.ReminderOffset
import com.jikanhub.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateTaskSheet(
    initialDate: java.time.LocalDate = java.time.LocalDate.now(),
    onDismiss: () -> Unit,
    onTaskCreated: () -> Unit,
    viewModel: CreateTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(initialDate) {
        viewModel.updateDate(initialDate)
    }

    LaunchedEffect(Unit) {
        viewModel.taskCreated.collect { onTaskCreated() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceContainer,
        contentColor = OnSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = OnSurfaceVariant) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = stringResource(R.string.fab_new_task),
                style = MaterialTheme.typography.headlineMedium,
                color = OnSurface
            )

            // Task title field
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text(stringResource(R.string.field_title)) },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.error == "title_required",
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = OnSurfaceVariant.copy(alpha = 0.3f),
                    cursorColor = Accent
                )
            )

            // Description field
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text(stringResource(R.string.field_description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = OnSurfaceVariant.copy(alpha = 0.3f),
                    cursorColor = Accent
                )
            )

            // Date & Time row
            val context = androidx.compose.ui.platform.LocalContext.current
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                OutlinedTextField(
                    value = uiState.date.format(dateFormatter),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.field_date)) },
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val picker = android.app.DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    viewModel.updateDate(java.time.LocalDate.of(year, month + 1, dayOfMonth))
                                },
                                uiState.date.year,
                                uiState.date.monthValue - 1,
                                uiState.date.dayOfMonth
                            )
                            picker.show()
                        },
                    enabled = false, // Use enabled=false + clickable modifier for text fields to act as buttons
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = OnSurfaceVariant.copy(alpha = 0.3f),
                        disabledTextColor = OnSurface,
                        disabledLabelColor = OnSurfaceVariant
                    )
                )
                OutlinedTextField(
                    value = "%02d:%02d".format(uiState.time.hour, uiState.time.minute),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.field_time)) },
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val picker = android.app.TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    viewModel.updateTime(java.time.LocalTime.of(hourOfDay, minute))
                                },
                                uiState.time.hour,
                                uiState.time.minute,
                                true // 24h format
                            )
                            picker.show()
                        },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = OnSurfaceVariant.copy(alpha = 0.3f),
                        disabledTextColor = OnSurface,
                        disabledLabelColor = OnSurfaceVariant
                    )
                )
            }

            // Priority selector
            Text(
                text = stringResource(R.string.field_priority),
                style = MaterialTheme.typography.labelLarge,
                color = OnSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityChip(
                    label = stringResource(R.string.priority_low),
                    color = PriorityLow,
                    selected = uiState.priority == Priority.LOW,
                    onClick = { viewModel.updatePriority(Priority.LOW) },
                    modifier = Modifier.weight(1f)
                )
                PriorityChip(
                    label = stringResource(R.string.priority_medium),
                    color = PriorityMedium,
                    selected = uiState.priority == Priority.MEDIUM,
                    onClick = { viewModel.updatePriority(Priority.MEDIUM) },
                    modifier = Modifier.weight(1f)
                )
                PriorityChip(
                    label = stringResource(R.string.priority_high),
                    color = PriorityHigh,
                    selected = uiState.priority == Priority.HIGH,
                    onClick = { viewModel.updatePriority(Priority.HIGH) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Reminder toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.field_reminder),
                    style = MaterialTheme.typography.labelLarge,
                    color = OnSurfaceVariant
                )
                Switch(
                    checked = uiState.reminderEnabled,
                    onCheckedChange = { viewModel.toggleReminder(it) },
                    colors = SwitchDefaults.colors(checkedTrackColor = Accent)
                )
            }

            // Reminder offsets (animated visibility)
            AnimatedVisibility(visible = uiState.reminderEnabled) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReminderOffset.entries.forEach { offset ->
                            val label = when (offset) {
                                ReminderOffset.AT_TIME -> stringResource(R.string.reminder_at_time)
                                ReminderOffset.MINUTES_15 -> stringResource(R.string.reminder_15min)
                                ReminderOffset.MINUTES_30 -> stringResource(R.string.reminder_30min)
                                ReminderOffset.MINUTES_45 -> stringResource(R.string.reminder_45min)
                                ReminderOffset.HOUR_1 -> stringResource(R.string.reminder_1h)
                                ReminderOffset.HOURS_12 -> stringResource(R.string.reminder_12h)
                                ReminderOffset.HOURS_24 -> stringResource(R.string.reminder_24h)
                                ReminderOffset.HOURS_48 -> stringResource(R.string.reminder_48h)
                            }
                            FilterChip(
                                selected = offset in uiState.selectedOffsets,
                                onClick = { viewModel.toggleOffset(offset) },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Accent.copy(alpha = 0.2f),
                                    selectedLabelColor = Accent
                                )
                            )
                        }
                    }

                    // Custom reminder message
                    OutlinedTextField(
                        value = uiState.reminderMessage,
                        onValueChange = { viewModel.updateReminderMessage(it) },
                        label = { Text(stringResource(R.string.field_reminder_message)) },
                        placeholder = { Text(stringResource(R.string.field_reminder_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent,
                            unfocusedBorderColor = OnSurfaceVariant.copy(alpha = 0.3f),
                            cursorColor = Accent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save button
            Button(
                onClick = { viewModel.saveTask() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor = OnSurface
                ),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color = OnSurface,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.btn_save),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun PriorityChip(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.2f),
            selectedLabelColor = color,
            labelColor = OnSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = color.copy(alpha = 0.3f),
            selectedBorderColor = color,
            enabled = true,
            selected = selected
        )
    )
}
