package com.ninezero.shopang.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val orderId: String,
    val userUid: String,
    val orderPlacedTime: Long,
    val orderAddress: String,
    val orderStatus: OrderEnums,
    val totalPrice: Int,
    val cartList: List<Product>
) : Parcelable {

    fun toMap(): Map<String, Any> = mapOf(
        "orderId" to orderId,
        "userUid" to userUid,
        "orderPlacedTime" to orderPlacedTime,
        "orderAddress" to orderAddress,
        "orderStatus" to orderStatus,
        "totalPrice" to totalPrice,
        "cartList" to cartList.map { it.toMap() }
    )

    companion object {
        fun fromMap(map: Map<String, Any>): Order {
            return Order(
                orderId = map["orderId"].toString(),
                userUid = map["userUid"].toString(),
                orderPlacedTime = map["orderPlacedTime"] as Long,
                orderAddress = map["orderAddress"].toString(),
                orderStatus = OrderEnums.valueOf(map["orderStatus"].toString()),
                totalPrice = map["totalPrice"].toString().toInt(),
                cartList = convertArrayMapToProduct(map["cartList"] as ArrayList<Map<String, Any>>)
            )
        }

        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): Order {
            return fromMap(snapshot.data!!)
        }
    }
}

fun convertDocumentToOrder(documents: List<DocumentSnapshot>): List<Order> {
    return documents.map { Order.fromDocumentSnapshot(it) }
}

enum class OrderEnums {
    PLACED, CONFIRMED, SHIPPED, DELIVERED
}
