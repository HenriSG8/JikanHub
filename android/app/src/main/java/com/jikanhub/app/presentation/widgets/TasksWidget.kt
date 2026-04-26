package com.jikanhub.app.presentation.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.jikanhub.app.MainActivity
import com.jikanhub.app.data.local.entity.TaskEntity
import com.jikanhub.app.di.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId

class TasksWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val tasks = try {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                WidgetEntryPoint::class.java
            )
            val taskDao = entryPoint.taskDao()

            val today = LocalDate.now()
            val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            taskDao.getTasksByDate(startOfDay, endOfDay).first()
        } catch (e: Exception) {
            emptyList<TaskEntity>()
        }

        provideContent {
            androidx.glance.GlanceTheme {
                TasksWidgetContent(tasks)
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun TasksWidgetContent(tasks: List<TaskEntity>) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp)
                .background(androidx.glance.unit.ColorProvider(androidx.compose.ui.graphics.Color(0xFF121212)))
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tarefas de Hoje",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Spacer(modifier = GlanceModifier.defaultWeight())
                
                Text(
                    text = "Ver tudo",
                    modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>()),
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF4F46E5)),
                        fontSize = 12.sp
                    )
                )
            }

            if (tasks.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma tarefa",
                        style = TextStyle(color = ColorProvider(Color.Gray))
                    )
                }
            } else {
                LazyColumn(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    items(tasks) { task ->
                        TaskWidgetItem(task)
                    }
                }
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun TaskWidgetItem(task: TaskEntity) {
        val priorityColor = when (task.priority) {
            "HIGH" -> "#EF4444"
            "MEDIUM" -> "#F59E0B"
            else -> "#10B981"
        }

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp)
                .background(ColorProvider(Color(0xFF1E1E1E)))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = GlanceModifier
                    .size(4.dp, 24.dp)
                    .background(ColorProvider(Color(android.graphics.Color.parseColor(priorityColor))))
            ) {}
            
            Spacer(modifier = GlanceModifier.width(8.dp))
            
            Text(
                text = task.title,
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 14.sp
                ),
                maxLines = 1
            )
        }
    }
}

class TasksWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TasksWidget()
}
