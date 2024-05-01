package com.ninezero.shopang.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    val userUid: String = "",
    val userName: String,
    val profileImageUrl: String? = null,
): Parcelable {

    fun toMap(): Map<String, Any?> = mapOf(
            "userUid" to userUid,
            "userName" to userName,
            "profileImageUrl" to profileImageUrl
    )
}

fun mapToUserInfo(map: Map<String, Any>): UserInfo = UserInfo(
        userUid = map["userUid"].toString(),
        userName = map["userName"].toString(),
        profileImageUrl = map["profileImageUrl"] as? String
)
