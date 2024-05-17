package com.ninezero.shopang.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Product(
    @PrimaryKey
    val id: String,
    val name: String,
    val image: String,
    val capacity: String,
    val calories: Double,
    val carb: Double,
    val fat: Double,
    val protein: Double,
    val price: Double,
    var quantity: Int,
    val quantityType: String,
    val category: String,
    val rate: Double,
    val detail: String
): Parcelable
