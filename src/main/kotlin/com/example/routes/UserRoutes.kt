package com.example.routes

import com.example.models.UserDto
import com.example.service.deleteUser
import com.example.service.findAll
import com.example.service.findById
import com.example.service.findByUsername
import com.example.service.saveUser
import com.example.service.updateUser
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRouting() {
    route("/user") {
        get {
            val username = call.request.queryParameters["username"]
            if (username == null) {
                call.respond(findAll())
            } else {
                call.respond(findByUsername(username))
            }
        }
        get("/{id}") {
            call.respond(findById(call.parameters["id"]!!.toLong()))
        }
        post {
            val user = call.receive<UserDto>()
            call.respond(saveUser(user)!!)
        }
        put("/{id}") {
            val id = call.parameters["id"]!!.toLong()
            val user = call.receive<UserDto>()
            call.respond(updateUser(id, user)!!)
        }
        delete("/{id}") {
            val id = call.parameters["id"]!!.toLong()
            call.respond(deleteUser(id)!!)
        }
    }
}