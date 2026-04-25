package com.jikanhub.app.ai

import com.jikanhub.app.domain.ai.AiAssistant
import com.jikanhub.app.domain.ai.AiTip
import com.jikanhub.app.domain.model.Task
import java.time.Duration
import javax.inject.Inject

/**
 * 🤖 Stub implementation of AiAssistant.
 * Returns empty results until a real AI provider (Gemini API) is integrated.
 *
 * To activate AI features:
 * 1. Replace this with a real implementation (e.g., GeminiAiAssistant)
 * 2. Update RepositoryModule binding
 * 3. Add API key to BuildConfig
 */
class AiAssistantStub @Inject constructor() : AiAssistant {

    override suspend fun getTaskTips(task: Task): List<AiTip> = emptyList()

    override suspend fun suggestSubtasks(task: Task): List<String> = emptyList()

    override suspend fun estimateEffort(task: Task): Duration = Duration.ZERO
}
