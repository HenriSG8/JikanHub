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
    onNavigateToStats: () -> Unit = {},
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

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = JikanOnSurfaceVariant.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Menu Items
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.drawer_tasks_day)) },
                    selected = uiState.currentTab == DashboardTab.TASKS_OF_DAY,
                    onClick = { 
                        viewModel.changeTab(DashboardTab.TASKS_OF_DAY)
                        scope.launch { drawerState.close() } 
                    },
                    icon = { Icon(Icons.Default.Today, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = JikanAccent.copy(alpha = 0.15f),
                        selectedIconColor = JikanAccent,
                        selectedTextColor = JikanOnSurface
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
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToStats()
                    },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedIconColor = JikanOnSurfaceVariant)
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
                FloatingActionButton(
                    onClick = { viewModel.showCreateSheet() },
                    containerColor = JikanAccent,
                    contentColor = JikanOnSurface,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.fab_new_task))
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
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = JikanOnSurface,
                            modifier = Modifier.size(28.dp)
                        )
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
            onReschedule = { t, newDate -> viewModel.rescheduleTask(t, newDate) },
            onToggleSubtask = { subtaskId ->
                viewModel.toggleSubtask(task, subtaskId)
            }
        )
    }

    // ── Criar ou Editar Tarefa ──
    if (uiState.showCreateSheet) {
        CreateTaskSheet(
            initialDate = uiState.selectedDate,
            taskToEdit = uiState.taskToEdit,
            onDismiss = { viewModel.hideCreateSheet() },
            onTaskCreated = { viewModel.onTaskCreated() }
        )
    }
}

// ═══════════════════════════════════════════
// ── Aba: Tarefas do Dia ──
// ═══════════════════════════════════════════
@Composable
private fun TasksOfDayContent(
    uiState: DashboardUiState,
    dateFormatter: DateTimeFormatter,
    viewModel: DashboardViewModel
) {
    // ── Header com saudação ──
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        val greeting = when (uiState.greeting) {
            "greeting_morning" -> stringResource(R.string.greeting_morning)
            "greeting_afternoon" -> stringResource(R.string.greeting_afternoon)
            else -> stringResource(R.string.greeting_evening)
        }

        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineSmall,
            color = JikanOnSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = uiState.userName,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            ),
            color = JikanOnSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Today Date Badge
        Surface(
            color = JikanAccent.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                width = 1.dp,
                color = JikanAccent.copy(alpha = 0.2f)
            )
        ) {
            Text(
                text = LocalDate.now().format(dateFormatter)
                    .replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge,
                color = JikanAccent,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // ── Lista de Tarefas de Hoje ──
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = JikanAccent)
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
                    color = JikanOnSurfaceVariant.copy(alpha = 0.3f)
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

// ═══════════════════════════════════════════
// ── Aba: Calendário com Grid Mensal ──
// ═══════════════════════════════════════════
@Composable
private fun CalendarContent(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()

    Column(modifier = Modifier.fillMaxSize()) {
        // ── Seletor de Mês ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Mês anterior",
                    tint = JikanOnSurface
                )
            }
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                color = JikanOnSurface,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Próximo mês",
                    tint = JikanOnSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Cabeçalho dos dias da semana ──
        val weekDayNames = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDayNames.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    color = JikanOnSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Grid do Calendário ──
        val firstDayOfMonth = currentMonth.atDay(1)
        // Monday = 1, ..., Sunday = 7
        val startDayOfWeek = firstDayOfMonth.dayOfWeek.value
        val daysInMonth = currentMonth.lengthOfMonth()
        
        // Build cells: empty cells for padding + actual day cells
        val emptyCells = startDayOfWeek - 1 // Monday-based
        val totalCells = emptyCells + daysInMonth

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Empty cells for alignment
            items(emptyCells) {
                Box(modifier = Modifier.aspectRatio(1f))
            }
            // Day cells
            items(daysInMonth) { index ->
                val day = index + 1
                val date = currentMonth.atDay(day)
                val isToday = date == today
                val isSelected = date == uiState.selectedDate

                val bgColor = when {
                    isSelected -> JikanAccent
                    isToday -> JikanAccent.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
                val borderColor = when {
                    isSelected -> JikanAccent
                    isToday -> JikanAccent.copy(alpha = 0.3f)
                    else -> if (LocalIsDarkTheme.current) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                }
                val textColor = when {
                    isSelected -> JikanSurface
                    isToday -> JikanAccent
                    else -> JikanOnSurface
                }

                Surface(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.selectDate(date)
                        },
                    color = bgColor,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                    tonalElevation = 0.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = day.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor,
                            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // ── Tarefas do dia selecionado (abaixo do calendário) ──
        if (uiState.tasks.isNotEmpty()) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                color = JikanOnSurfaceVariant.copy(alpha = 0.1f)
            )
            Text(
                text = "Tarefas em ${uiState.selectedDate.dayOfMonth}/${uiState.selectedDate.monthValue}",
                style = MaterialTheme.typography.titleSmall,
                color = JikanOnSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp, bottom = 88.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
