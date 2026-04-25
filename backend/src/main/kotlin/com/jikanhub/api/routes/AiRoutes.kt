package com.jikanhub.api.routes

import com.jikanhub.api.models.AiSuggestRequest
import com.jikanhub.api.models.AiSuggestResponse
import com.jikanhub.api.models.ErrorResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Gemini API response models
@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig()
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 512
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiCandidateContent? = null
)

@Serializable
data class GeminiCandidateContent(
    val parts: List<GeminiPart>? = null
)

private val geminiClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
}

fun Route.aiRoutes() {
    authenticate("auth-jwt") {
        route("/api/ai") {

            // POST /api/ai/suggest-subtasks
            post("/suggest-subtasks") {
                val apiKey = System.getenv("GEMINI_API_KEY")
                if (apiKey.isNullOrBlank()) {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        ErrorResponse("AI service not configured")
                    )
                    return@post
                }

                val request = call.receive<AiSuggestRequest>()

                if (request.title.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("Title is required")
                    )
                    return@post
                }

                try {
                    val prompt = buildPrompt(request.title, request.description)

                    val geminiResponse = geminiClient.post(
                        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"
                    ) {
                        contentType(ContentType.Application.Json)
                        setBody(GeminiRequest(
                            contents = listOf(
                                GeminiContent(
                                    parts = listOf(GeminiPart(text = prompt))
                                )
                            )
                        ))
                    }

                    val body = geminiResponse.body<GeminiResponse>()
                    val rawText = body.candidates
                        ?.firstOrNull()
                        ?.content
                        ?.parts
                        ?.firstOrNull()
                        ?.text ?: ""

                    val subtasks = parseSubtasks(rawText)

                    call.respond(AiSuggestResponse(subtasks = subtasks))

                } catch (e: Exception) {
                    call.application.log.error("Gemini API error", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("AI suggestion failed: ${e.message}")
                    )
                }
            }
        }
    }
}

private fun buildPrompt(title: String, description: String): String {
    val descPart = if (description.isNotBlank()) {
        "\nDescrição adicional: $description"
    } else ""

    return """Você é um assistente de produtividade. O usuário está criando uma tarefa e precisa de sugestões práticas de subtarefas.

Tarefa: $title$descPart

Gere uma lista de 4 a 8 subtarefas práticas e objetivas para essa tarefa. 
Cada subtarefa deve ser curta (máximo 5 palavras).
Responda APENAS com a lista, uma subtarefa por linha, sem numeração, sem marcadores, sem explicações extras.

Exemplo para "Ir ao mercado comprar produtos de limpeza":
Detergente
Desinfetante
Sabão em pó
Água sanitária
Esponja de cozinha
Luvas de limpeza"""
}

private fun parseSubtasks(rawText: String): List<String> {
    return rawText
        .lines()
        .map { it.trim() }
        .filter { it.isNotBlank() && it.length <= 60 }
        .take(10)
}
