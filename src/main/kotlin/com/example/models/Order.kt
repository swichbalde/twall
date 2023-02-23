package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val number: String,
    val products: List<Product>,
)

@Serializable
data class Product(
    val item: String,
    val amount: Int,
    val price: Double,
)

val orderStorage = listOf(Order(
    "2020-04-06-01", listOf(
        Product("Ham Sandwich", 2, 5.50),
        Product("Water", 1, 1.50),
        Product("Beer", 3, 2.30),
        Product("Cheesecake", 1, 3.75)
    )),
    Order("2020-04-03-01", listOf(
        Product("Cheeseburger", 1, 8.50),
        Product("Water", 2, 1.50),
        Product("Coke", 2, 1.76),
        Product("Ice Cream", 1, 2.35)
    ))
)