package com.jikanhub.api.config

import com.jikanhub.api.db.Tasks
import com.jikanhub.api.db.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val config = HikariConfig().apply {
        jdbcUrl = environment.config.propertyOrNull("database.url")?.getString()
            ?: System.getenv("DATABASE_URL")
            ?: "jdbc:postgresql://localhost:5432/jikanhub"
        driverClassName = "org.postgresql.Driver"
        username = environment.config.propertyOrNull("database.user")?.getString()
            ?: System.getenv("DATABASE_USER")
            ?: "postgres"
        password = environment.config.propertyOrNull("database.password")?.getString()
            ?: System.getenv("DATABASE_PASSWORD")
            ?: "postgres"
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }

    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    transaction {
        SchemaUtils.create(Users, Tasks)
    }

    log.info("Database connected and tables created")
}
