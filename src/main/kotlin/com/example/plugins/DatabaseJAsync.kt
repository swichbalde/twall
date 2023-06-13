package com.example.plugins

import com.github.jasync.sql.db.Configuration
import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.ConnectionPoolConfiguration
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.general.ArrayRowData
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.postgresql.pool.PostgreSQLConnectionFactory
import io.ktor.server.config.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

object DatabaseFactoryJAsync {

    fun init(config: ApplicationConfig) {
//        val poolConfiguration = PoolConfiguration(
//            100,                            // maxObjects
//            TimeUnit.MINUTES.toMillis(15),  // maxIdle
//            10_000,                         // maxQueueSize
//            TimeUnit.SECONDS.toMillis(30)   // validationInterval
//        )
        val configuration = Configuration(
            host = config.property("host").getString(),
            port = config.property("port").getString().toInt(),
            username = config.property("username").getString(),
            password = config.property("password").getString(),
            database = config.property("database").getString(),
        )
        val connection: Connection = ConnectionPool(
            PostgreSQLConnectionFactory(
                configuration
            ), ConnectionPoolConfiguration(
                host = configuration.host,
                port = configuration.port,
                username = configuration.username,
                password = configuration.password,
                database = configuration.database,
                maxActiveConnections = 100,
                maxIdleTime = TimeUnit.MINUTES.toMillis(15),
                maximumMessageSize = 10_000
            )
        )
        connection.connect().get()

        runBlocking {
            launch {
                val queryResult = connection.sendPreparedStatementAwait("select * from user_info limit 2")
                println((queryResult.rows[0] as ArrayRowData).columns.toList())
                println((queryResult.rows[1] as ArrayRowData).columns.toList())
            }

            launch {
                val future = connection.sendPreparedStatement("select * from user_info limit 2")
                val queryResult = future.await()
                println((queryResult.rows[0] as ArrayRowData).columns.toList())
                println((queryResult.rows[1] as ArrayRowData).columns.toList())
            }
        }


        connection.disconnect().get()
    }

    suspend fun Connection.sendPreparedStatementAwait(query: String, values: List<Any> = emptyList()): QueryResult =
        this.sendPreparedStatement(query, values).await()
}
