package com.example.plugins

import com.example.routes.customerRouting
import com.example.routes.orderRouting
import com.example.routes.totalizeOrderRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        customerRouting()
        orderRouting()
        totalizeOrderRoute()
    }
}
