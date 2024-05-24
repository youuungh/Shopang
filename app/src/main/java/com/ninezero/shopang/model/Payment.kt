package com.ninezero.shopang.model

data class Payment(
    val userAddress: String,
    val paymentMethod: String,
    val totalPrice: Int
)