package com.jikanhub.app.presentation.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jikanhub.app.R
import com.jikanhub.app.presentation.components.HorizontalDateSelector
import com.jikanhub.app.presentation.components.TaskCard
import com.jikanhub.app.presentation.screens.createtask.CreateTaskSheet
import com.jikanhub.app.presentation.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.getDefault())
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateSheet() },
                containerColor = Accent,
                contentColor = OnSurface,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.fab_new_task))
            }
        },
        containerColor = Surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Header ──
            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Greeting & Test Button Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (uiState.greeting) {
                            "greeting_morning" -> stringResource(R.string.greeting_morning)
                            "greeting_afternoon" -> stringResource(R.string.greeting_afternoon)
                            else -> stringResource(R.string.greeting_evening)
                        },
                        style = MaterialTheme.typography.headlineLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    IconButton(onClick = { viewModel.testNotification() }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Teste Notificação",
                            tint = Accent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Date
                Text(
                    text = uiState.selectedDate.format(dateFormatter)
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Date Selector ──
            HorizontalDateSelector(
                days = uiState.weekDays,
                onDateSelected = { viewModel.selectDate(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Task List ──
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Accent)
                }
            } else if (uiState.tasks.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "時",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = MaterialTheme.typography.headlineLarge.fontSize * 3
                            ),
                            color = OnSurfaceVariant.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.empty_tasks),
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        bottom = 88.dp // Space for FAB
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.tasks,
                        key = { it.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            onToggleComplete = {
                                viewModel.toggleTaskComplete(task.id, task.status)
                            },
                            onClick = { viewModel.selectTask(task) }
                        )
                    }
                }
            }
        }
    }

    // ── Task Detail Bottom Sheet ──
    uiState.selectedTask?.let { task ->
        TaskDetailSheet(
            task = task,
            onDismiss = { viewModel.selectTask(null) },
            onDelete = { viewModel.deleteTask(it) }
        )
    }

    // ── Create Task Bottom Sheet ──
    if (uiState.showCreateSheet) {
        CreateTaskSheet(
            initialDate = uiState.selectedDate,
            onDismiss = { viewModel.hideCreateSheet() },
            onTaskCreated = { viewModel.onTaskCreated() }
        )
    }
}
