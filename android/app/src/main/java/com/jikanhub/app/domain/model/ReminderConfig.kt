package com.jikanhub.app.domain.model

/**
 * Reminder offset configuration.
 * Each offset represents how many minutes before the task
 * the reminder should fire.
 */
enum class ReminderOffset(val minutes: Int) {
    AT_TIME(0),
    MINUTES_15(15),
    HOUR_1(60),
    HOURS_12(720),   // Aviso Prévio — 12h before
    HOURS_24(1440)   // Aviso Prévio — 24h before
}

data class ReminderConfig(
    val enabled: Boolean = false,
    val message: String = "",
    val offsets: List<ReminderOffset> = emptyList()
)
