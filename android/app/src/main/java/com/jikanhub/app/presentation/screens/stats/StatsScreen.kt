package com.jikanhub.app.presentation.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.jikanhub.app.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jikanhub.app.presentation.theme.*

@Composable
fun StatsContent(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = JikanAccent)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Progress Card
            MainStatsCard(uiState)

            // Detailed Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatSmallCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.stats_completed),
                    value = uiState.completedCount.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = Completed
                )
                StatSmallCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.stats_pending),
                    value = uiState.pendingCount.toString(),
                    icon = Icons.Default.PendingActions,
                    color = JikanPriorityMedium
                )
            }

            // Insights Card
            InsightsCard(uiState)

            // Priority Distribution
            PriorityDistributionCard(uiState)
        }
    }
}

@Composable
private fun MainStatsCard(state: StatsUiState) {
    Surface(
        color = JikanSurfaceVariant,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.stats_completion_rate),
                style = MaterialTheme.typography.titleMedium,
                color = JikanOnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { state.completionRate / 100f },
                    modifier = Modifier.size(140.dp),
                    color = JikanAccent,
                    strokeWidth = 12.dp,
                    trackColor = JikanAccent.copy(alpha = 0.1f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.completionRate.toInt()}%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = JikanOnSurface
                    )
                    Text(
                        text = stringResource(R.string.stats_efficiency),
                        style = MaterialTheme.typography.labelMedium,
                        color = JikanOnSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.stats_tasks_created, state.totalCount),
                style = MaterialTheme.typography.bodyMedium,
                color = JikanOnSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatSmallCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        color = JikanSurfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = JikanOnSurface)
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = JikanOnSurfaceVariant)
        }
    }
}

@Composable
private fun InsightsCard(state: StatsUiState) {
    Surface(
        color = JikanAccent.copy(alpha = 0.05f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, JikanAccent.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = JikanPriorityMedium, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.stats_monthly_highlight),
                    style = MaterialTheme.typography.titleSmall,
                    color = JikanAccent,
                    fontWeight = FontWeight.Bold
                )
                val dayTranslated = when (state.mostProductiveDay) {
                    "MONDAY" -> stringResource(R.string.day_monday)
                    "TUESDAY" -> stringResource(R.string.day_tuesday)
                    "WEDNESDAY" -> stringResource(R.string.day_wednesday)
                    "THURSDAY" -> stringResource(R.string.day_thursday)
                    "FRIDAY" -> stringResource(R.string.day_friday)
                    "SATURDAY" -> stringResource(R.string.day_saturday)
                    "SUNDAY" -> stringResource(R.string.day_sunday)
                    else -> state.mostProductiveDay
                }
                Text(
                    text = stringResource(R.string.stats_most_productive_day, dayTranslated),
                    style = MaterialTheme.typography.bodyMedium,
                    color = JikanOnSurface
                )
            }
        }
    }
}

@Composable
private fun PriorityDistributionCard(state: StatsUiState) {
    Surface(
        color = JikanSurfaceVariant,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.stats_priority_distribution),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = JikanOnSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            state.priorityStats.forEach { (priority, count) ->
                val label = when(priority) {
                    com.jikanhub.app.domain.model.Priority.HIGH -> stringResource(R.string.priority_high)
                    com.jikanhub.app.domain.model.Priority.MEDIUM -> stringResource(R.string.priority_medium)
                    com.jikanhub.app.domain.model.Priority.LOW -> stringResource(R.string.priority_low)
                }
                val color = when(priority) {
                    com.jikanhub.app.domain.model.Priority.HIGH -> JikanPriorityHigh
                    com.jikanhub.app.domain.model.Priority.MEDIUM -> JikanPriorityMedium
                    com.jikanhub.app.domain.model.Priority.LOW -> JikanPriorityLow
                }
                
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = JikanOnSurface)
                        Text(text = count.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = JikanOnSurface)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { if (state.totalCount > 0) count.toFloat() / state.totalCount else 0f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = color,
                        trackColor = color.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}
