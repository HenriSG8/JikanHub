package com.jikanhub.app.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jikanhub.app.R
import com.jikanhub.app.domain.model.Task
import com.jikanhub.app.presentation.theme.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailSheet(
    task: Task,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceContainer,
        contentColor = OnSurface
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
                    "HIGH" -> PriorityHigh.copy(alpha = 0.2f)
                    "MEDIUM" -> PriorityMedium.copy(alpha = 0.2f)
                    else -> PriorityLow.copy(alpha = 0.2f)
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = task.priority.name,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = when (task.priority.name) {
                        "HIGH" -> PriorityHigh
                        "MEDIUM" -> PriorityMedium
                        else -> PriorityLow
                    }
                )
            }

            // Title
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineMedium,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )

            // Date
            Text(
                text = task.dateTime.format(dateFormatter),
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )

            HorizontalDivider(color = OnSurfaceVariant.copy(alpha = 0.1f))

            // Description
            Text(
                text = if (task.description.isBlank()) "Sem descrição" else task.description,
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Delete Button
            OutlinedButton(
                onClick = { 
                    onDelete(task.id)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PriorityHigh),
                border = androidx.compose.foundation.BorderStroke(1.dp, PriorityHigh.copy(alpha = 0.3f))
            ) {
                Text("Excluir Tarefa")
            }
        }
    }
}
