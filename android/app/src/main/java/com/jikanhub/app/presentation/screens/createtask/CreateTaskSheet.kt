package com.jikanhub.app.presentation.screens.createtask

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jikanhub.app.R
import com.jikanhub.app.domain.model.Priority
import com.jikanhub.app.domain.model.ReminderOffset
import com.jikanhub.app.presentation.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.RemoveCircle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateTaskSheet(
    initialDate: java.time.LocalDate = java.time.LocalDate.now(),
    taskToEdit: com.jikanhub.app.domain.model.Task? = null,
    onDismiss: () -> Unit,
    onTaskCreated: () -> Unit,
    viewModel: CreateTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(initialDate, taskToEdit) {
        if (taskToEdit != null) {
            viewModel.loadTaskForEdit(taskToEdit)
        } else {
            viewModel.updateDate(initialDate)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.taskCreated.collect { onTaskCreated() }
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(uiState.aiError) {
        if (uiState.aiError != null) {
            android.widget.Toast.makeText(context, uiState.aiError, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = JikanSurfaceContainer,
        contentColor = JikanOnSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = JikanOnSurfaceVariant) }
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
                text = if (taskToEdit != null) "Editar Tarefa" else stringResource(R.string.fab_new_task),
                style = MaterialTheme.typography.headlineMedium,
                color = JikanOnSurface
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
                    focusedBorderColor = JikanAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    cursorColor = JikanAccent,
                    focusedLabelColor = JikanAccent,
                    unfocusedLabelColor = JikanOnSurfaceVariant
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
                    focusedBorderColor = JikanAccent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    cursorColor = JikanAccent,
                    focusedLabelColor = JikanAccent,
                    unfocusedLabelColor = JikanOnSurfaceVariant
                )
            )

            // Subtasks section header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "Subtarefas",
                    style = MaterialTheme.typography.labelLarge,
                    color = JikanOnSurfaceVariant
                )
                TextButton(onClick = { viewModel.addSubtask() }) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar")
                }
            }

            // AI Suggest button — prominent, full-width
            OutlinedButton(
                onClick = { viewModel.requestAiSuggestions() },
                enabled = uiState.title.isNotBlank() && !uiState.isAiLoading,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (uiState.title.isNotBlank()) JikanAccent.copy(alpha = 0.5f)
                    else JikanOnSurfaceVariant.copy(alpha = 0.2f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = JikanAccent
                )
            ) {
                if (uiState.isAiLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = JikanAccent,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gerando sugestões...")
                } else {
                    Text("✨  Sugerir com IA", style = MaterialTheme.typography.labelLarge)
                }
            }

            // AI Error message
            if (uiState.aiError != null) {
                Text(
                    text = uiState.aiError!!,
                    color = JikanPriorityHigh,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Subtask list
            uiState.subtasks.forEach { subtask ->
                OutlinedTextField(
                    value = subtask.title,
                    onValueChange = { viewModel.updateSubtaskTitle(subtask.id, it) },
                    placeholder = { Text("Ex: Comprar leite") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.removeSubtask(subtask.id) }) {
                            Icon(
                                imageVector = Icons.Outlined.RemoveCircle,
                                contentDescription = "Remover subtarefa",
                                tint = JikanPriorityHigh
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JikanAccent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        cursorColor = JikanAccent
                    ),
                    singleLine = true
                )
            }

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
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        disabledTextColor = JikanOnSurface,
                        disabledLabelColor = JikanOnSurfaceVariant
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
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        disabledTextColor = JikanOnSurface,
                        disabledLabelColor = JikanOnSurfaceVariant
                    )
                )
            }

            // Priority selector
            Text(
                text = stringResource(R.string.field_priority),
                style = MaterialTheme.typography.labelLarge,
                color = JikanOnSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityChip(
                    label = stringResource(R.string.priority_low),
                    color = JikanPriorityLow,
                    selected = uiState.priority == Priority.LOW,
                    onClick = { viewModel.updatePriority(Priority.LOW) },
                    modifier = Modifier.weight(1f)
                )
                PriorityChip(
                    label = stringResource(R.string.priority_medium),
                    color = JikanPriorityMedium,
                    selected = uiState.priority == Priority.MEDIUM,
                    onClick = { viewModel.updatePriority(Priority.MEDIUM) },
                    modifier = Modifier.weight(1f)
                )
                PriorityChip(
                    label = stringResource(R.string.priority_high),
                    color = JikanPriorityHigh,
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
                    color = JikanOnSurfaceVariant
                )
                Switch(
                    checked = uiState.reminderEnabled,
                    onCheckedChange = { viewModel.toggleReminder(it) },
                    colors = SwitchDefaults.colors(checkedTrackColor = JikanAccent)
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
                                    selectedContainerColor = JikanAccent.copy(alpha = 0.2f),
                                    selectedLabelColor = JikanAccent
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
                            focusedBorderColor = JikanAccent,
                            unfocusedBorderColor = JikanOnSurfaceVariant.copy(alpha = 0.3f),
                            cursorColor = JikanAccent
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
                    containerColor = JikanAccent,
                    contentColor = JikanOnSurface
                ),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color = JikanOnSurface,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (taskToEdit != null) "Salvar Alterações" else stringResource(R.string.btn_save),
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
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) color.copy(alpha = 0.15f) else JikanSurfaceVariant.copy(alpha = 0.5f),
        label = "bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) color else JikanOnSurfaceVariant,
        label = "content"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) color.copy(alpha = 0.5f) else Color.Transparent,
        label = "border"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
