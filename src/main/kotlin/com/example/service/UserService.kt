package com.example.service

import com.example.models.UserDto
import com.example.repo.UserRepo

suspend fun findAll() = UserRepo.findAll()
suspend fun findById(id: Long) = UserRepo.findById(id)
suspend fun findByUsername(username: String) = UserRepo.findByUsername(username)
suspend fun saveUser(user: UserDto) = UserRepo.saveUser(user)
suspend fun updateUser(id: Long, user: UserDto) = UserRepo.updateUser(id, user)
suspend fun deleteUser(id: Long) = UserRepo.deleteUser(id)