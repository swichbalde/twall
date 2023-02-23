package com.example.routes

import com.example.models.orderStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.orderRouting() {
    route("/order") {
        get {
            if (orderStorage.isNotEmpty()) {
                call.respond(orderStorage)
            } else {
                call.respondText(
                    "No orders",
                    status = HttpStatusCode.NoContent
                )
            }
        }
        get("{number?}") {
            val number = call.parameters["number"] ?: call.respondText(
                "No number param",
                status = HttpStatusCode.BadRequest
            )
            val order = orderStorage.find { it.number == number } ?: call.respondText(
                "Not found",
                status = HttpStatusCode.NotFound
            )
            call.respond(order)
        }
    }
}

fun Route.totalizeOrderRoute() {
    get("/order/{id?}/total") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Bad Request",
            status = HttpStatusCode.BadRequest
        )
        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        val total = order.products.sumOf { it.price * it.amount }
        call.respond(total)
    }
}