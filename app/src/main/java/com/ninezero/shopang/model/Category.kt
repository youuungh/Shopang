package com.ninezero.shopang.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val name: String,
    val products: List<Product>,
    val image: String,
    val title: String
) : Parcelable
