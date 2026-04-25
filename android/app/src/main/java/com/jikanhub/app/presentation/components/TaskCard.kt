package com.jikanhub.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jikanhub.app.domain.model.Priority
import com.jikanhub.app.domain.model.Task
import com.jikanhub.app.domain.model.TaskStatus
import com.jikanhub.app.presentation.theme.*

@Composable
fun TaskCard(
    task: Task,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    val cardAlpha = if (isCompleted) 0.6f else 1f

    val priorityColor = when (task.priority) {
        Priority.HIGH -> PriorityHigh
        Priority.MEDIUM -> PriorityMedium
        Priority.LOW -> PriorityLow
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariant.copy(alpha = cardAlpha)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Completed,
                    uncheckedColor = OnSurfaceVariant,
                    checkmarkColor = Surface
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Task content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSurface.copy(alpha = cardAlpha),
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant.copy(alpha = cardAlpha),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right side: time badge + priority dot + reminder icon
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Time badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceBright,
                    tonalElevation = 0.dp
                ) {
                    Text(
                        text = "%02d:%02d".format(
                            task.dateTime.hour,
                            task.dateTime.minute
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = OnSurface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Reminder indicator
                    Icon(
                        imageVector = if (task.reminder.enabled)
                            Icons.Outlined.Notifications
                        else
                            Icons.Outlined.NotificationsOff,
                        contentDescription = null,
                        tint = if (task.reminder.enabled) Accent else OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )

                    // Priority dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(priorityColor)
                    )
                }
            }
        }
    }
}
