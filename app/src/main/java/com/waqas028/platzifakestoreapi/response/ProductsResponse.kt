package com.waqas028.platzifakestoreapi.response

data class Product(
    val id: Int,
    val title: String,
    val price: String,
    val description: String,
    val images: List<String>,
    val creationAt: String,
    val updatedAt: String,
    val category: Category
)

data class  Category(
    val id: Int,
    val name: String,
    val image: String,
    val creationAt: String,
    val updatedAt: String
)
