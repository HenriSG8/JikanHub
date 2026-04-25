package com.jikanhub.app.domain.model

/**
 * Reminder offset configuration.
 * Each offset represents how many minutes before the task
 * the reminder should fire.
 */
enum class ReminderOffset(val minutes: Int) {
    AT_TIME(0),
    MINUTES_15(15),
    MINUTES_30(30),
    MINUTES_45(45),
    HOUR_1(60),
    HOURS_12(720),
    HOURS_24(1440),
    HOURS_48(2880)
}

data class ReminderConfig(
    val enabled: Boolean = false,
    val message: String = "",
    val offsets: List<ReminderOffset> = emptyList()
)
