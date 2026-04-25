package com.jikanhub.app.presentation.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jikanhub.app.R
import com.jikanhub.app.presentation.components.HorizontalDateSelector
import com.jikanhub.app.presentation.components.TaskCard
import com.jikanhub.app.presentation.screens.createtask.CreateTaskSheet
import com.jikanhub.app.presentation.screens.dashboard.components.TaskDetailSheet
import com.jikanhub.app.presentation.theme.*
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.getDefault())
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                containerColor = SurfaceVariant,
                drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Drawer Header
                Column(modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = Accent.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = uiState.userName.firstOrNull()?.uppercase() ?: "U",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Accent
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.userName,
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "JikanHub Premium",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = OnSurfaceVariant.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Menu Items
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_tasks_day)) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Today, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Accent.copy(alpha = 0.15f),
                        selectedIconColor = Accent,
                        selectedTextColor = OnSurface
                    )
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_calendar)) },
                    selected = false,
                    onClick = { /* Navegar para calendário */ },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedIconColor = OnSurfaceVariant)
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_settings)) },
                    selected = false,
                    onClick = { /* Navegar para config */ },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedIconColor = OnSurfaceVariant)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_logout)) },
                    selected = false,
                    onClick = { /* Logout */ },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedIconColor = PriorityHigh, 
                        unselectedTextColor = PriorityHigh
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    ) {
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
                // ── Custom Top Bar ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = OnSurface,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    IconButton(onClick = { viewModel.testNotification() }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificações",
                            tint = OnSurfaceVariant
                        )
                    }
                }

                // ── Header ──
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    val greeting = when (uiState.greeting) {
                        "greeting_morning" -> stringResource(R.string.greeting_morning)
                        "greeting_afternoon" -> stringResource(R.string.greeting_afternoon)
                        else -> stringResource(R.string.greeting_evening)
                    }
                    
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = uiState.userName,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Data com fundo estilizado
                    Surface(
                        color = SurfaceBright.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = uiState.selectedDate.format(dateFormatter)
                                .replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Accent,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Seletor de Data ──
                HorizontalDateSelector(
                    days = uiState.weekDays,
                    onDateSelected = { viewModel.selectDate(it) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Lista de Tarefas ──
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Accent)
                    }
                } else if (uiState.tasks.isEmpty()) {
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
                            bottom = 88.dp
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
    }

    // ── Detalhes da Tarefa ──
    uiState.selectedTask?.let { task ->
        TaskDetailSheet(
            task = task,
            onDismiss = { viewModel.selectTask(null) },
            onDelete = { viewModel.deleteTask(it) }
        )
    }

    // ── Criar Tarefa ──
    if (uiState.showCreateSheet) {
        CreateTaskSheet(
            initialDate = uiState.selectedDate,
            onDismiss = { viewModel.hideCreateSheet() },
            onTaskCreated = { viewModel.onTaskCreated() }
        )
    }
}
