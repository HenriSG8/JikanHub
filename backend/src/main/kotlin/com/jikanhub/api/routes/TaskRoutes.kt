package com.jikanhub.api.routes

import com.jikanhub.api.config.userId
import com.jikanhub.api.db.Tasks
import com.jikanhub.api.db.Subtasks
import com.jikanhub.api.db.toTaskDto
import com.jikanhub.api.models.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

fun Route.taskRoutes() {
    authenticate("auth-jwt") {
        route("/api/tasks") {

            // GET /api/tasks?date=2026-04-25
            get {
                val userId = call.userId()
                val dateStr = call.request.queryParameters["date"]
                val date = dateStr?.let { LocalDate.parse(it) } ?: LocalDate.now()

                val startOfDay = date.atStartOfDay()
                val endOfDay = date.atTime(23, 59, 59)

                val tasks = transaction {
                    Tasks.selectAll().where {
                        (Tasks.userId eq userId) and
                        (Tasks.dateTime greaterEq startOfDay) and
                        (Tasks.dateTime lessEq endOfDay) and
                        (Tasks.isDeleted eq false)
                    }.orderBy(Tasks.dateTime to SortOrder.ASC)
                        .map { it.toTaskDto() }
                }

                call.respond(tasks)
            }

            // POST /api/tasks
            post {
                val userId = call.userId()
                val request = call.receive<CreateTaskRequest>()

                val taskId = UUID.randomUUID().toString()
                val now = LocalDateTime.now()

                transaction {
                    Tasks.insert {
                        it[id] = taskId
                        it[Tasks.userId] = userId
                        it[title] = request.title
                        it[description] = request.description
                        it[dateTime] = LocalDateTime.parse(request.dateTime)
                        it[priority] = request.priority
                        it[status] = "PENDING"
                        it[reminderEnabled] = request.reminderEnabled
                        it[reminderMessage] = request.reminderMessage
                        it[reminderOffsets] = request.reminderOffsets.joinToString(",")
                        it[createdAt] = now
                        it[updatedAt] = now
                    }

                    // Insert subtasks
                    request.subtasks.forEach { sub ->
                        Subtasks.insert { sit ->
                            sit[Subtasks.id] = sub.id
                            sit[Subtasks.taskId] = taskId
                            sit[Subtasks.title] = sub.title
                            sit[Subtasks.isCompleted] = sub.isCompleted
                        }
                    }
                }

                val task = transaction {
                    Tasks.selectAll().where { Tasks.id eq taskId }.first().toTaskDto()
                }
                call.respond(HttpStatusCode.Created, task)
            }

            // PUT /api/tasks/{id}
            put("/{id}") {
                val userId = call.userId()
                val taskId = call.parameters["id"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse("Task ID required")
                )
                val request = call.receive<TaskDto>()

                val updated = transaction {
                    Tasks.update({
                        (Tasks.id eq taskId) and (Tasks.userId eq userId)
                    }) {
                        it[title] = request.title
                        it[description] = request.description
                        it[dateTime] = LocalDateTime.parse(request.dateTime)
                        it[priority] = request.priority
                        it[status] = request.status
                        it[reminderEnabled] = request.reminderEnabled
                        it[reminderMessage] = request.reminderMessage
                        it[reminderOffsets] = request.reminderOffsets.joinToString(",")
                        it[updatedAt] = LocalDateTime.now()
                    }
                }

                if (updated == 0) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Task not found"))
                } else {
                    val task = transaction {
                        Tasks.selectAll().where { Tasks.id eq taskId }.first().toTaskDto()
                    }
                    call.respond(task)
                }
            }

            // PATCH /api/tasks/{id}/status
            patch("/{id}/status") {
                val userId = call.userId()
                val taskId = call.parameters["id"] ?: return@patch call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse("Task ID required")
                )
                val request = call.receive<UpdateStatusRequest>()

                val updated = transaction {
                    Tasks.update({
                        (Tasks.id eq taskId) and (Tasks.userId eq userId)
                    }) {
                        it[status] = request.status
                        it[updatedAt] = LocalDateTime.now()
                    }
                }

                if (updated == 0) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Task not found"))
                } else {
                    val task = transaction {
                        Tasks.selectAll().where { Tasks.id eq taskId }.first().toTaskDto()
                    }
                    call.respond(task)
                }
            }

            // DELETE /api/tasks/{id}
            delete("/{id}") {
                val userId = call.userId()
                val taskId = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse("Task ID required")
                )

                val updated = transaction {
                    Tasks.update({
                        (Tasks.id eq taskId) and (Tasks.userId eq userId)
                    }) {
                        it[isDeleted] = true
                        it[updatedAt] = LocalDateTime.now()
                    }
                }

                if (updated == 0) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Task not found"))
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}
