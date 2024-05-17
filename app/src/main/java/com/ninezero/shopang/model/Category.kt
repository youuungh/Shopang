package com.ninezero.shopang.model

data class Category(
    val name: String,
    val products: List<Product>,
    val image: String,
    val title: String
)
