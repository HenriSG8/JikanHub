package com.jikanhub.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jikanhub.app.data.local.dao.TaskDao
import com.jikanhub.app.data.mapper.toDomain
import com.jikanhub.app.domain.model.ReminderOffset
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var taskDao: TaskDao
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tasks = taskDao.getTasksWithActiveReminders().first()
                tasks.forEach { entity ->
                    val task = entity.toDomain()
                    task.reminder.offsets.forEach { offset ->
                        val triggerTime = task.dateTime.minusMinutes(offset.minutes.toLong())
                        if (triggerTime.isAfter(LocalDateTime.now())) {
                            val message = task.reminder.message.ifBlank {
                                task.title
                            }
                            alarmScheduler.schedule(
                                taskId = task.id,
                                triggerAtMillis = triggerTime.atZone(ZoneId.systemDefault())
                                    .toInstant().toEpochMilli(),
                                title = task.title,
                                message = message,
                                offsetMinutes = offset.minutes
                            )
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
