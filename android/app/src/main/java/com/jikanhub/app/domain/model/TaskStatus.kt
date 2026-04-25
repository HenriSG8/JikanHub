package com.jikanhub.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TaskStatus {
    PENDING, COMPLETED, POSTPONED
}
