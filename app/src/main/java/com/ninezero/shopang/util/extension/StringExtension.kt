package com.ninezero.shopang.util.extension


fun String.generateRandomNickname(): String {
    val nickname = listOf("사과", "바나나", "딸기", "복숭아", "망고")
    val random = nickname.random()
    return "$random $this"
}