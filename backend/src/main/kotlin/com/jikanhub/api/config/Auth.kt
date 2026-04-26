package com.jikanhub.api.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.util.*

object JwtConfig {
    val secret: String = System.getenv("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET environment variable is not set")
    val issuer: String = System.getenv("JWT_ISSUER") ?: "jikanhub-api"
    val audience: String = System.getenv("JWT_AUDIENCE") ?: "jikanhub-app"
    private val algorithm = Algorithm.HMAC256(secret)
    private val expirationMs = 7 * 24 * 60 * 60 * 1000L // 7 days

    fun generateToken(userId: String, email: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("user_id", userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + expirationMs))
            .sign(algorithm)
    }

    fun getAlgorithm(): Algorithm = algorithm
}

fun Application.configureAuth() {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(JwtConfig.getAlgorithm())
                    .withIssuer(JwtConfig.issuer)
                    .withAudience(JwtConfig.audience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("user_id").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token is invalid or expired"))
            }
        }
    }
}

fun ApplicationCall.userId(): String {
    return principal<JWTPrincipal>()!!
        .payload.getClaim("user_id").asString()
}
