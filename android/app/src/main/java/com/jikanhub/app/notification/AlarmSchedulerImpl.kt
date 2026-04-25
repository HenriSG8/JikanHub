package com.jikanhub.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.jikanhub.app.domain.model.ReminderOffset
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(
        taskId: String,
        triggerAtMillis: Long,
        title: String,
        message: String,
        offsetMinutes: Int
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TASK_ID, taskId)
            putExtra(AlarmReceiver.EXTRA_TITLE, title)
            putExtra(AlarmReceiver.EXTRA_MESSAGE, message)
            putExtra(AlarmReceiver.EXTRA_OFFSET, offsetMinutes)
        }

        val requestCode = generateRequestCode(taskId, offsetMinutes)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    override fun cancel(taskId: String, offsetMinutes: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = generateRequestCode(taskId, offsetMinutes)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }

    override fun cancelAllForTask(taskId: String) {
        ReminderOffset.entries.forEach { offset ->
            cancel(taskId, offset.minutes)
        }
    }

    private fun generateRequestCode(taskId: String, offsetMinutes: Int): Int {
        return (taskId.hashCode() * 31 + offsetMinutes)
    }
}
