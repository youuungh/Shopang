package com.ninezero.shopang.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentSnapshot
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
): Parcelable {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "image" to image,
            "capacity" to capacity,
            "calories" to calories,
            "carb" to carb,
            "fat" to fat,
            "protein" to protein,
            "price" to price,
            "quantity" to quantity,
            "quantityType" to quantityType,
            "category" to category,
            "rate" to rate,
            "detail" to detail
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Product {
            return Product(
                map["id"].toString(),
                map["name"].toString(),
                map["image"].toString(),
                map["capacity"].toString(),
                map["calories"].toString().toDouble(),
                map["carb"].toString().toDouble(),
                map["fat"].toString().toDouble(),
                map["protein"].toString().toDouble(),
                map["price"].toString().toDouble(),
                map["quantity"].toString().toInt(),
                map["quantityType"].toString(),
                map["category"].toString(),
                map["rate"].toString().toDouble(),
                map["detail"].toString()
            )
        }

        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): Product {
            return fromMap(snapshot.data!!)
        }
    }
}

fun convertDocumentToProductList(documents: List<DocumentSnapshot>): List<Product> {
    return documents.map { Product.fromDocumentSnapshot(it) }
}
