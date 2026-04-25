package com.jikanhub.app.domain.usecase

import com.jikanhub.app.domain.model.Task
import com.jikanhub.app.domain.repository.TaskRepository
import com.jikanhub.app.notification.AlarmScheduler
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(task: Task): Task {
        val created = repository.createTask(task)

        // Schedule alarms for each reminder offset
        if (created.reminder.enabled) {
            created.reminder.offsets.forEach { offset ->
                val triggerTime = created.dateTime.minusMinutes(offset.minutes.toLong())
                if (triggerTime.isAfter(LocalDateTime.now())) {
                    val message = created.reminder.message.ifBlank {
                        generateDefaultMessage(created, offset.minutes)
                    }
                    alarmScheduler.schedule(
                        taskId = created.id,
                        triggerAtMillis = triggerTime.atZone(ZoneId.systemDefault())
                            .toInstant().toEpochMilli(),
                        title = created.title,
                        message = message,
                        offsetMinutes = offset.minutes
                    )
                }
            }
        }

        return created
    }

    private fun generateDefaultMessage(task: Task, offsetMinutes: Int): String {
        val timeStr = "%02d:%02d".format(task.dateTime.hour, task.dateTime.minute)
        return when (offsetMinutes) {
            1440 -> "Amanhã: ${task.title} às $timeStr"
            720 -> "Não esqueça: ${task.title} às $timeStr"
            60 -> "Em 1 hora: ${task.title}"
            15 -> "Em 15 minutos: ${task.title}"
            else -> "${task.title} — Agora!"
        }
    }
}
