package com.ninezero.shopang.util

import androidx.lifecycle.LiveData

sealed class ResponseWrapper<T>(
    val data: T? = null,
    val msg: String? = null
) {
    class Success<T>(data: T?): ResponseWrapper<T>(data)
    class Error<T>(msg: String, data: T? = null): ResponseWrapper<T>(data, msg)
    class Loading<T>: ResponseWrapper<T>()
    class Idle<T>: ResponseWrapper<T>()
}