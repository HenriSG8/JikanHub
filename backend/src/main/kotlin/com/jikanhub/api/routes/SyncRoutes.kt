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
import java.time.LocalDateTime

fun Route.syncRoutes() {
    authenticate("auth-jwt") {
        route("/api/sync") {

            // POST /api/sync — Push local changes to server
            post {
                val userId = call.userId()
                val request = call.receive<SyncRequest>()

                transaction {
                    request.tasks.forEach { dto ->
                        val existing = Tasks.selectAll().where {
                            (Tasks.id eq dto.id) and (Tasks.userId eq userId)
                        }.firstOrNull()

                        if (existing != null) {
                            // Update if client version is newer
                            val clientUpdated = LocalDateTime.parse(dto.updatedAt)
                            val serverUpdated = existing[Tasks.updatedAt]
                            if (clientUpdated.isAfter(serverUpdated)) {
                                Tasks.update({ Tasks.id eq dto.id }) {
                                    it[title] = dto.title
                                    it[description] = dto.description
                                    it[dateTime] = LocalDateTime.parse(dto.dateTime)
                                    it[priority] = dto.priority
                                    it[status] = dto.status
                                    it[reminderEnabled] = dto.reminderEnabled
                                    it[reminderMessage] = dto.reminderMessage
                                    it[reminderOffsets] = dto.reminderOffsets.joinToString(",")
                                    it[updatedAt] = clientUpdated
                                    it[isDeleted] = dto.isDeleted
                                }

                                // Update subtasks
                                Subtasks.deleteWhere { Subtasks.taskId eq dto.id }
                                dto.subtasks.forEach { sub ->
                                    Subtasks.insert { sit ->
                                        sit[Subtasks.id] = sub.id
                                        sit[Subtasks.taskId] = dto.id
                                        sit[Subtasks.title] = sub.title
                                        sit[Subtasks.isCompleted] = sub.isCompleted
                                    }
                                }
                            }
                        } else {
                            // Insert new
                            Tasks.insert {
                                it[id] = dto.id
                                it[this.userId] = userId
                                it[title] = dto.title
                                it[description] = dto.description
                                it[dateTime] = LocalDateTime.parse(dto.dateTime)
                                it[priority] = dto.priority
                                it[status] = dto.status
                                it[reminderEnabled] = dto.reminderEnabled
                                it[reminderMessage] = dto.reminderMessage
                                it[reminderOffsets] = dto.reminderOffsets.joinToString(",")
                                it[createdAt] = LocalDateTime.now()
                                it[updatedAt] = LocalDateTime.parse(dto.updatedAt)
                                it[isDeleted] = dto.isDeleted
                            }

                            // Insert subtasks
                            dto.subtasks.forEach { sub ->
                                Subtasks.insert { sit ->
                                    sit[Subtasks.id] = sub.id
                                    sit[Subtasks.taskId] = dto.id
                                    sit[Subtasks.title] = sub.title
                                    sit[Subtasks.isCompleted] = sub.isCompleted
                                }
                            }
                        }
                    }
                }

                // Return all server tasks for this user
                val serverTasks = transaction {
                    Tasks.selectAll().where {
                        (Tasks.userId eq userId) and (Tasks.isDeleted eq false)
                    }.map { it.toTaskDto() }
                }

                call.respond(SyncResponse(
                    tasks = serverTasks,
                    serverTimestamp = LocalDateTime.now().toString()
                ))
            }

            // GET /api/sync?since=2026-04-24T10:00:00
            get {
                val userId = call.userId()
                val sinceStr = call.request.queryParameters["since"]
                val since = sinceStr?.let { LocalDateTime.parse(it) }
                    ?: LocalDateTime.now().minusDays(30)

                val tasks = transaction {
                    Tasks.selectAll().where {
                        (Tasks.userId eq userId) and
                        (Tasks.updatedAt greaterEq since) and
                        (Tasks.isDeleted eq false)
                    }.map { it.toTaskDto() }
                }

                call.respond(SyncResponse(
                    tasks = tasks,
                    serverTimestamp = LocalDateTime.now().toString()
                ))
            }
        }
    }
}
