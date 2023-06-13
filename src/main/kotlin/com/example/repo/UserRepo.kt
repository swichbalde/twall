package com.example.repo

import com.example.models.UserDto
import com.example.plugins.DatabaseFactory.getConnection
import io.r2dbc.spi.Connection
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import reactor.kotlin.core.publisher.toFlux
import java.math.BigInteger

const val SELECT_ALL = "SELECT * FROM user_info"
const val SELECT_BY_ID = "SELECT * FROM user_info u WHERE u.id = \$1"
const val SELECT_BY_USERNAME = "SELECT * FROM user_info WHERE username = \$1"
const val INSERT = "INSERT INTO user_info (username, private_key) VALUES (\$1, \$2)"
const val UPDATE = "UPDATE user_info SET username = \$1, private_key = \$2 WHERE id = \$3"
const val DELETE_BY_ID = "DELETE FROM user_info WHERE id = \$1"

object UserRepo {
    suspend fun findAll(): List<UserDto> = updateOnConnection {
        it.createStatement(SELECT_ALL).execute()
            .toFlux()
            .collectList()
            .awaitFirst()
            .flatMap { result ->
                result.map { row, _ ->
                    UserDto(
                        username = row.get("username", String::class.java),
                        privateKey = row.get("private_key", String::class.java),
                    )
                }
                    .toFlux()
                    .collectList()
                    .awaitFirst()
            }
    }

    suspend fun findById(id: Long): UserDto = updateOnConnection {
        it.createStatement(SELECT_BY_ID)
            .bind("\$1", id)
            .execute()
            .awaitFirst()
            .map { row, _ ->
                UserDto(
                    username = row.get("username", String::class.java),
                    privateKey = row.get("private_key", String::class.java),
                )
            }
            .awaitSingle()
    }

    suspend fun findByUsername(username: String): UserDto = updateOnConnection {
        it.createStatement(SELECT_BY_USERNAME)
            .bind("\$1", username)
            .execute()
            .awaitFirst()
            .map { row, _ ->
                UserDto(
                    username = row.get("username", String::class.java),
                    privateKey = row.get("private_key", String::class.java),
                )
            }
            .awaitSingle()
    }

    suspend fun saveUser(user: UserDto): Long? = updateOnConnection {
        it.createStatement(INSERT)
            .bind("\$1", user.username!!)
            .bind("\$2", user.privateKey!!)
            .returnGeneratedValues("id")
            .execute()
            .awaitFirst()
            .map { row, _ -> row.get("id", BigInteger::class.java)?.toLong() }
            .awaitSingle()
    }

    suspend fun updateUser(id: Long, user: UserDto): Long? = updateOnConnection {
        it.createStatement(UPDATE)
            .bind("\$1", user.username!!)
            .bind("\$2", user.privateKey!!)
            .bind("\$3", id)
            .returnGeneratedValues("id")
            .execute()
            .awaitFirst()
            .map { row, _ -> row.get("id", BigInteger::class.java)?.toLong() }
            .awaitSingle()
    }

    suspend fun deleteUser(id: Long): Long? = updateOnConnection {
        it.createStatement(DELETE_BY_ID)
            .bind("\$1", id)
            .returnGeneratedValues("id")
            .execute()
            .awaitFirst()
            .map { row, _ -> row.get("id", BigInteger::class.java)?.toLong() }
            .awaitSingle()
    }

    private suspend fun <T> updateOnConnection(statement: suspend (Connection) -> T): T =
        getConnection().let { connection ->
            try {
                statement(connection)
            } finally {
                connection.close()
                    .awaitFirstOrNull()
            }
        }
}