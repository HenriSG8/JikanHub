package com.jikanhub.app.notification

/**
 * Interface for scheduling and canceling exact alarms.
 */
interface AlarmScheduler {
    fun schedule(
        taskId: String,
        triggerAtMillis: Long,
        title: String,
        message: String,
        offsetMinutes: Int
    )

    fun cancel(taskId: String, offsetMinutes: Int)
    fun cancelAllForTask(taskId: String)
}
