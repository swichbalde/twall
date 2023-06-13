package com.example.models

import kotlinx.serialization.Serializable

data class User(
    val id: Int?,
    val username: String?,
    val privateKey: String?,
) {
    fun toDto() = UserDto(
        username = username,
        privateKey = privateKey,
    )
}

@Serializable
data class UserDto(
    val username: String?,
    val privateKey: String?,
)