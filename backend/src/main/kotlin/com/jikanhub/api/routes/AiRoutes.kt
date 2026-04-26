package com.jikanhub.api.routes

import com.jikanhub.api.models.AiSuggestRequest
import com.jikanhub.api.models.AiSuggestResponse
import com.jikanhub.api.models.ErrorResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Groq API response models (OpenAI compatible)
@Serializable
data class GroqRequest(
    val model: String = "llama3-70b-8192",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 512
)

@Serializable
data class GroqMessage(
    val role: String,
    val content: String
)

@Serializable
data class GroqResponse(
    val choices: List<GroqChoice>? = null
)

@Serializable
data class GroqChoice(
    val message: GroqMessage? = null
)

private val aiClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        })
    }
}

fun Route.aiRoutes() {
    authenticate("auth-jwt") {
        route("/api/ai") {

            // POST /api/ai/suggest-subtasks
            post("/suggest-subtasks") {
                val apiKey = System.getenv("GROQ_API_KEY")
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

                    val aiResponse = aiClient.post("https://api.groq.com/openai/v1/chat/completions") {
                        contentType(ContentType.Application.Json)
                        header("Authorization", "Bearer $apiKey")
                        setBody(GroqRequest(
                            model = "llama3-70b-8192",
                            messages = listOf(
                                GroqMessage(role = "user", content = prompt)
                            ),
                            temperature = 0.7,
                            max_tokens = 512
                        ))
                    }

                    if (!aiResponse.status.isSuccess()) {
                        val errorBody = aiResponse.bodyAsText()
                        throw Exception("Groq Error ${aiResponse.status.value}: $errorBody")
                    }

                    val body = aiResponse.body<GroqResponse>()
                    val rawText = body.choices
                        ?.firstOrNull()
                        ?.message
                        ?.content ?: ""

                    val subtasks = parseSubtasks(rawText)

                    call.respond(AiSuggestResponse(subtasks = subtasks))

                } catch (e: Exception) {
                    System.err.println("Groq API error: ${e.message}")
                    e.printStackTrace()
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
