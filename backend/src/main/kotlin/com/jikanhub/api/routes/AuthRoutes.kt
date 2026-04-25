package com.jikanhub.api.routes

import com.jikanhub.api.config.JwtConfig
import com.jikanhub.api.db.Users
import com.jikanhub.api.models.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

fun Route.authRoutes() {
    route("/api/auth") {

        // Register
        post("/register") {
            val request = call.receive<AuthRequest>()

            if (request.email.isBlank() || request.password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Email and password are required"))
                return@post
            }

            val existingUser = transaction {
                Users.selectAll().where { Users.email eq request.email }.firstOrNull()
            }

            if (existingUser != null) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse("Email already registered"))
                return@post
            }

            val userId = UUID.randomUUID().toString()
            val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

            transaction {
                Users.insert {
                    it[id] = userId
                    it[email] = request.email
                    it[passwordHash] = hashedPassword
                    it[name] = request.name ?: ""
                }
            }

            val token = JwtConfig.generateToken(userId, request.email)
            call.respond(
                HttpStatusCode.Created,
                AuthResponse(
                    token = token,
                    user = UserDto(id = userId, email = request.email, name = request.name ?: "")
                )
            )
        }

        // Login
        post("/login") {
            val request = call.receive<AuthRequest>()

            val user = transaction {
                Users.selectAll().where { Users.email eq request.email }.firstOrNull()
            }

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid credentials"))
                return@post
            }

            val storedHash = user[Users.passwordHash]
            if (!BCrypt.checkpw(request.password, storedHash)) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid credentials"))
                return@post
            }

            val token = JwtConfig.generateToken(user[Users.id], user[Users.email])
            call.respond(
                AuthResponse(
                    token = token,
                    user = UserDto(
                        id = user[Users.id],
                        email = user[Users.email],
                        name = user[Users.name]
                    )
                )
            )
        }

        // Google OAuth
        post("/google") {
            val request = call.receive<GoogleAuthRequest>()

            // TODO: Verify Google ID token with Google's API
            // For now, this is a placeholder that should be implemented
            // using io.ktor.client to call https://oauth2.googleapis.com/tokeninfo
            call.respond(
                HttpStatusCode.NotImplemented,
                ErrorResponse("Google OAuth verification pending - implement token verification")
            )
        }
    }
}
