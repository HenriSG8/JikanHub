package com.jikanhub.app.presentation.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jikanhub.app.domain.model.Task
import com.jikanhub.app.domain.model.TaskStatus
import com.jikanhub.app.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class StatsUiState(
    val completedCount: Int = 0,
    val pendingCount: Int = 0,
    val totalCount: Int = 0,
    val completionRate: Float = 0f,
    val mostProductiveDay: String = "-",
    val priorityStats: Map<com.jikanhub.app.domain.model.Priority, Int> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            repository.getAllTasks().collect { tasks ->
                val now = LocalDate.now()
                val currentMonthTasks = tasks.filter { 
                    it.dateTime.year == now.year && it.dateTime.month == now.month 
                }

                val completed = currentMonthTasks.count { it.status == TaskStatus.COMPLETED }
                val total = currentMonthTasks.size
                val pending = total - completed
                
                val rate = if (total > 0) (completed.toFloat() / total * 100) else 0f

                // Find most productive day
                val dayFrequencies = currentMonthTasks
                    .filter { it.status == TaskStatus.COMPLETED }
                    .groupBy { it.dateTime.dayOfWeek }
                    .mapValues { it.value.size }
                
                val productiveDay = dayFrequencies.maxByOrNull { it.value }?.key?.name ?: "-"

                // Priority stats
                val prioStats = currentMonthTasks
                    .groupBy { it.priority }
                    .mapValues { it.value.size }

                _uiState.update { 
                    it.copy(
                        completedCount = completed,
                        pendingCount = pending,
                        totalCount = total,
                        completionRate = rate,
                        mostProductiveDay = productiveDay,
                        priorityStats = prioStats,
                        isLoading = false
                    )
                }
            }
        }
    }
}
