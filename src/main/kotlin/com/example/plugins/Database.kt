package com.example.plugins

import io.ktor.server.config.*
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.Connection
import io.r2dbc.spi.Result
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import reactor.kotlin.core.publisher.toFlux
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object DatabaseFactory {

    fun init(config: ApplicationConfig) {
        initConnectionPool(
            PostgresqlConnectionConfiguration.builder()
                .host(config.property("host").getString())
                .port(config.property("port").getString().toInt())
                .username(config.property("username").getString())
                .password(config.property("password").getString())
                .database(config.property("database").getString())
                .build()
        )

        runBlocking { initDBStructure() }
    }

    suspend fun getConnection(): Connection = pool.create().awaitSingle()

    private suspend fun executeQuery(sql: String): List<Result> =
        getConnection().let {
            try {
                it
                    .createStatement(sql)
                    .execute()
                    .toFlux()
                    .collectList()
                    .awaitFirst()
            } finally {
                it
                    .close()
                    .awaitFirstOrNull()
            }
        }

    private fun initConnectionPool(
        connectionConfiguration: PostgresqlConnectionConfiguration,
    ) {
        val poolConfig = ConnectionPoolConfiguration
            .builder(
                PostgresqlConnectionFactory(connectionConfiguration)
            )
            .maxIdleTime(10.seconds.toJavaDuration())
            .maxSize(20)
            .build()

        pool = ConnectionPool(poolConfig)
    }

    private lateinit var pool: ConnectionPool

    private suspend fun initDBStructure() = executeQuery(
        this::class
            .java
            .classLoader
            .getResource("db/schema.sql")!!
            .readText()
    )
}
