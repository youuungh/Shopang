package com.ninezero.shopang.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    val userUid: String = "",
    val userName: String,
    val profileImageUrl: String,
): Parcelable {

    fun toMapWithoutProfileImage(): MutableMap<String, Any> {
        return mutableMapOf(
            "userUid" to userUid,
            "userName" to userName
        )
    }

}

fun UserInfo.toMap(): Map<String, Any> {
    return mapOf(
        "userUid" to userUid,
        "userName" to userName,
        "profileImageUrl" to profileImageUrl
    )
}

fun mapToUserInfo(map: Map<String, Any>): UserInfo {
    return UserInfo(
        userUid = map["userUid"].toString(),
        userName = map["userName"].toString(),
        profileImageUrl = map["profileImageUrl"].toString()
    )
}