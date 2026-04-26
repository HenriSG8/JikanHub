package com.jikanhub.app.presentation.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jikanhub.app.R
import com.jikanhub.app.presentation.components.TaskCard
import com.jikanhub.app.presentation.screens.createtask.CreateTaskSheet
import com.jikanhub.app.presentation.screens.stats.StatsContent
import com.jikanhub.app.presentation.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Safety: Reset drawer state on entry to prevent transition glitches
    LaunchedEffect(Unit) {
        if (drawerState.isOpen) {
            drawerState.snapTo(DrawerValue.Closed)
        }
    }

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.getDefault())
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = JikanSurfaceVariant,
                drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JikanAccent.copy(alpha = 0.05f))
                        .padding(horizontal = 28.dp, vertical = 32.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = JikanAccent,
                        tonalElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = uiState.userName.firstOrNull()?.uppercase() ?: "U",
                                style = MaterialTheme.typography.displaySmall,
                                color = JikanSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.userName,
                        style = MaterialTheme.typography.titleLarge,
                        color = JikanOnSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "JikanHub Premium",
                        style = MaterialTheme.typography.labelMedium,
                        color = JikanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_tasks_day)) },
                    selected = uiState.currentTab == DashboardTab.TASKS_OF_DAY,
                    onClick = { 
                        viewModel.changeTab(DashboardTab.TASKS_OF_DAY)
                        scope.launch { drawerState.close() } 
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = JikanAccent.copy(alpha = 0.15f),
                        selectedIconColor = JikanAccent,
                        selectedTextColor = JikanOnSurface,
                        unselectedIconColor = JikanOnSurfaceVariant
                    )
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_calendar)) },
                    selected = uiState.currentTab == DashboardTab.CALENDAR,
                    onClick = { 
                        viewModel.changeTab(DashboardTab.CALENDAR)
                        scope.launch { drawerState.close() } 
                    },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = JikanAccent.copy(alpha = 0.15f),
                        selectedIconColor = JikanAccent,
                        selectedTextColor = JikanOnSurface,
                        unselectedIconColor = JikanOnSurfaceVariant
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Estatísticas") },
                    selected = uiState.currentTab == DashboardTab.STATS,
                    onClick = { 
                        viewModel.changeTab(DashboardTab.STATS)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = JikanAccent.copy(alpha = 0.15f),
                        selectedIconColor = JikanAccent,
                        selectedTextColor = JikanOnSurface,
                        unselectedIconColor = JikanOnSurfaceVariant
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Sobre") },
                    selected = uiState.currentTab == DashboardTab.ABOUT,
                    onClick = { 
                        viewModel.changeTab(DashboardTab.ABOUT)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = JikanAccent.copy(alpha = 0.15f),
                        selectedIconColor = JikanAccent,
                        selectedTextColor = JikanOnSurface,
                        unselectedIconColor = JikanOnSurfaceVariant
                    )
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_settings)) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToSettings()
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedIconColor = JikanOnSurfaceVariant)
                )

                Spacer(modifier = Modifier.weight(1f))
                
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_logout)) },
                    selected = false,
                    onClick = { 
                        viewModel.logout()
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedIconColor = JikanPriorityHigh, 
                        unselectedTextColor = JikanPriorityHigh
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                if (uiState.currentTab != DashboardTab.STATS) {
                    FloatingActionButton(
                        onClick = { viewModel.showCreateSheet() },
                        containerColor = JikanAccent,
                        contentColor = JikanSurface,
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.fab_new_task))
                    }
                }
            },
            containerColor = JikanSurface
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { 
                            if (!drawerState.isAnimationRunning) {
                                scope.launch { drawerState.open() } 
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = JikanOnSurface,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        if (uiState.currentTab == DashboardTab.STATS) {
                            Text(
                                text = "Estatísticas",
                                style = MaterialTheme.typography.titleLarge,
                                color = JikanOnSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        if (uiState.currentTab == DashboardTab.ABOUT) {
                            Text(
                                text = "Sobre",
                                style = MaterialTheme.typography.titleLarge,
                                color = JikanOnSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (uiState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Alternar Tema",
                            tint = JikanOnSurfaceVariant
                        )
                    }
                }

                // ── Show content based on current tab ──
                when (uiState.currentTab) {
                    DashboardTab.TASKS_OF_DAY -> TasksOfDayContent(
                        uiState = uiState,
                        dateFormatter = dateFormatter,
                        viewModel = viewModel
                    )
                    DashboardTab.CALENDAR -> CalendarContent(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    DashboardTab.STATS -> StatsContent()
                    DashboardTab.ABOUT -> com.jikanhub.app.presentation.screens.about.AboutContent()
                }
            }
        }
    }

    // ── Detalhes da Tarefa ──
    uiState.selectedTask?.let { task ->
        TaskDetailSheet(
            task = task,
            onDismiss = { viewModel.selectTask(null) },
            onDelete = { viewModel.deleteTask(it) },
            onEdit = { viewModel.editTask(it) },
            onReschedule = { t, dt -> viewModel.rescheduleTask(t, dt) },
            onToggleSubtask = { viewModel.toggleSubtask(task, it) }
        )
    }

    // ── Criar/Editar Tarefa ──
    if (uiState.showCreateSheet) {
        CreateTaskSheet(
            taskToEdit = uiState.taskToEdit,
            onDismiss = { viewModel.hideCreateSheet() },
            onTaskCreated = { viewModel.onTaskCreated() }
        )
    }

    if (uiState.showTutorial) {
        TutorialOverlay(
            step = uiState.tutorialStep,
            onNext = { viewModel.nextTutorialStep() },
            onSkip = { viewModel.skipTutorial() }
        )
    }
}

@Composable
private fun TasksOfDayContent(
    uiState: DashboardUiState,
    dateFormatter: DateTimeFormatter,
    viewModel: DashboardViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Date Header
        Text(
            text = uiState.selectedDate.format(dateFormatter),
            style = MaterialTheme.typography.titleLarge,
            color = JikanOnSurface,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        
        Text(
            text = stringResource(uiState.greeting.let { 
                when(it) {
                    "greeting_morning" -> R.string.greeting_morning
                    "greeting_afternoon" -> R.string.greeting_afternoon
                    else -> R.string.greeting_evening
                }
            }),
            style = MaterialTheme.typography.bodyLarge,
            color = JikanOnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.height(24.dp))


        // Tasks List
        if (uiState.tasks.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = JikanOnSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.empty_tasks),
                        style = MaterialTheme.typography.bodyLarge,
                        color = JikanOnSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.tasks) { task ->
                    TaskCard(
                        task = task,
                        onClick = { viewModel.selectTask(task) },
                        onToggleComplete = { viewModel.toggleTaskComplete(task.id, task.status) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun CalendarContent(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    val currentMonth = remember(uiState.selectedDate) { YearMonth.from(uiState.selectedDate) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + currentMonth.year,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = JikanOnSurface
            )
            Row {
                IconButton(onClick = { viewModel.selectDate(uiState.selectedDate.minusMonths(1)) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior", tint = JikanOnSurface)
                }
                IconButton(onClick = { viewModel.selectDate(uiState.selectedDate.plusMonths(1)) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Próximo", tint = JikanOnSurface)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekday labels
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = JikanOnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.weight(1f)
        ) {
            // Empty spaces for first week
            items(firstDayOfMonth) { Spacer(modifier = Modifier.aspectRatio(1f)) }
            
            items(daysInMonth) { dayIndex ->
                val date = currentMonth.atDay(dayIndex + 1)
                val isSelected = date == uiState.selectedDate
                val isToday = date == LocalDate.now()
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isSelected -> JikanAccent
                                isToday -> JikanAccent.copy(alpha = 0.1f)
                                else -> Color.Transparent
                            }
                        )
                        .clickable { viewModel.selectDate(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (dayIndex + 1).toString(),
                        color = if (isSelected) JikanSurface else JikanOnSurface,
                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Mini list for selected day in calendar
        if (uiState.tasks.isNotEmpty()) {
            Text(
                text = "Tarefas de " + uiState.selectedDate.dayOfMonth,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = JikanOnSurface,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                items(uiState.tasks) { task ->
                    TaskCard(
                        task = task,
                        onClick = { viewModel.selectTask(task) },
                        onToggleComplete = { viewModel.toggleTaskComplete(task.id, task.status) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayItem(
    dayItem: DayItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (dayItem.isSelected) JikanAccent else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .width(44.dp)
    ) {
        Text(
            text = dayItem.dayOfWeek,
            style = MaterialTheme.typography.labelMedium,
            color = if (dayItem.isSelected) JikanSurface else JikanOnSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = dayItem.dayNumber.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = if (dayItem.isSelected) JikanSurface else JikanOnSurface,
            fontWeight = FontWeight.Bold
        )
    }
}
