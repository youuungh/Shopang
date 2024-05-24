package com.ninezero.shopang.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    val userUid: String = "",
    val platform: String,
    val userName: String,
    val userAddress: String? = null,
    val profileImageUrl: String? = null,
): Parcelable {

    fun toMap(): Map<String, Any?> = mapOf(
        "userUid" to userUid,
        "platform" to platform,
        "userName" to userName,
        "userAddress" to userAddress,
        "profileImageUrl" to profileImageUrl
    )
}

fun mapToUserInfo(map: Map<String, Any>): UserInfo = UserInfo(
    userUid = map["userUid"].toString(),
    platform = map["platform"].toString(),
    userName = map["userName"].toString(),
    userAddress = map["userAddress"] as? String,
    profileImageUrl = map["profileImageUrl"] as? String
)
