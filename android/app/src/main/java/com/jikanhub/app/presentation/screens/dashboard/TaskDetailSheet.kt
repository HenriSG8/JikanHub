package com.jikanhub.app.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextDecoration
import com.jikanhub.app.R
import com.jikanhub.app.domain.model.Task
import com.jikanhub.app.presentation.theme.*
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailSheet(
    task: Task,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit,
    onEdit: (Task) -> Unit,
    onToggleSubtask: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = JikanSurfaceContainer,
        contentColor = JikanOnSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Priority Tag
            Surface(
                color = when (task.priority.name) {
                    "HIGH" -> JikanPriorityHigh.copy(alpha = 0.2f)
                    "MEDIUM" -> JikanPriorityMedium.copy(alpha = 0.2f)
                    else -> JikanPriorityLow.copy(alpha = 0.2f)
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = task.priority.name,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = when (task.priority.name) {
                        "HIGH" -> JikanPriorityHigh
                        "MEDIUM" -> JikanPriorityMedium
                        else -> JikanPriorityLow
                    }
                )
            }

            // Title and Edit button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = JikanOnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = { onEdit(task) }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = JikanAccent
                    )
                }
            }

            // Date and Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.dateTime.format(dateFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = JikanOnSurfaceVariant
                )

                // Priority Badge
                val priorityLabel = when (task.priority) {
                    com.jikanhub.app.domain.model.Priority.HIGH -> stringResource(R.string.priority_high)
                    com.jikanhub.app.domain.model.Priority.MEDIUM -> stringResource(R.string.priority_medium)
                    com.jikanhub.app.domain.model.Priority.LOW -> stringResource(R.string.priority_low)
                }
                val priorityColor = when (task.priority) {
                    com.jikanhub.app.domain.model.Priority.HIGH -> JikanPriorityHigh
                    com.jikanhub.app.domain.model.Priority.MEDIUM -> JikanPriorityMedium
                    com.jikanhub.app.domain.model.Priority.LOW -> JikanPriorityLow
                }

                Surface(
                    color = priorityColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, priorityColor.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = priorityLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = priorityColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = JikanOnSurfaceVariant.copy(alpha = 0.1f))

            // Description
            Text(
                text = if (task.description.isBlank()) "Sem descrição" else task.description,
                style = MaterialTheme.typography.bodyLarge,
                color = JikanOnSurface
            )

            if (task.subtasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Checklist",
                    style = MaterialTheme.typography.titleMedium,
                    color = JikanOnSurface,
                    fontWeight = FontWeight.Bold
                )
                
                task.subtasks.forEach { subtask ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = subtask.isCompleted,
                            onCheckedChange = { onToggleSubtask(subtask.id) },
                            colors = CheckboxDefaults.colors(checkedColor = JikanAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = subtask.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (subtask.isCompleted) JikanOnSurfaceVariant else JikanOnSurface,
                            textDecoration = if (subtask.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Delete Button
            OutlinedButton(
                onClick = { 
                    onDelete(task.id)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = JikanPriorityHigh),
                border = androidx.compose.foundation.BorderStroke(1.dp, JikanPriorityHigh.copy(alpha = 0.3f))
            ) {
                Text("Excluir Tarefa")
            }
        }
    }
}
