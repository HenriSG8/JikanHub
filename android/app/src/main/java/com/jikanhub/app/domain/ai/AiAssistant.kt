package com.jikanhub.app.domain.ai

import com.jikanhub.app.domain.model.Task
import java.time.Duration

/**
 * 🤖 AI Assistant interface — prepared for future integration.
 * When implemented, this will connect to Gemini API or similar
 * to provide intelligent tips on how to execute tasks.
 */
interface AiAssistant {
    suspend fun getTaskTips(task: Task): List<AiTip>
    suspend fun suggestSubtasks(task: Task): List<String>
    suspend fun estimateEffort(task: Task): Duration
}

data class AiTip(
    val title: String,
    val description: String,
    val category: TipCategory
)

enum class TipCategory {
    PREPARATION, FOCUS, BREAK, RESOURCE
}
