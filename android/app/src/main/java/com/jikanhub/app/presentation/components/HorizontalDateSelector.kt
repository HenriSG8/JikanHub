package com.jikanhub.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jikanhub.app.presentation.screens.dashboard.DayItem
import com.jikanhub.app.presentation.theme.*
import java.time.LocalDate

@Composable
fun HorizontalDateSelector(
    days: List<DayItem>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(days, key = { it.date.toString() }) { day ->
            DateItem(
                day = day,
                onClick = { onDateSelected(day.date) }
            )
        }
    }
}

@Composable
private fun DateItem(
    day: DayItem,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (day.isSelected) JikanAccent else JikanSurfaceVariant,
        label = "dateItemBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (day.isSelected) JikanOnSurface else JikanOnSurfaceVariant,
        label = "dateItemText"
    )
    val isToday = day.date == LocalDate.now()

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = day.dayOfWeek.uppercase().take(3),
            style = MaterialTheme.typography.labelSmall,
            color = textColor.copy(alpha = 0.7f)
        )
        Text(
            text = day.dayNumber.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
        )
        // Tasks indicator dot
        if (day.hasTasksToday) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (day.isSelected) JikanOnSurface else JikanAccent)
            )
        } else {
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}
