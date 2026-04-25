package com.jikanhub.api

import com.jikanhub.api.config.configureAuth
import com.jikanhub.api.config.configureCORS
import com.jikanhub.api.config.configureDatabase
import com.jikanhub.api.config.configureSerialization
import com.jikanhub.api.config.configureStatusPages
import com.jikanhub.api.routes.authRoutes
import com.jikanhub.api.routes.syncRoutes
import com.jikanhub.api.routes.taskRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(CallLogging)

    configureSerialization()
    configureStatusPages()
    configureCORS()
    configureDatabase()
    configureAuth()

    routing {
        authRoutes()
        taskRoutes()
        syncRoutes()
    }
}
