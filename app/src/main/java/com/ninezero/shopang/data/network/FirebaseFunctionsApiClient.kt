package com.ninezero.shopang.data.network

import com.ninezero.shopang.util.ResponseWrapper
import javax.inject.Inject

class FirebaseFunctionsApiClient @Inject constructor(
    private val firebaseFunctionsApiService: FirebaseFunctionsApiService
) {

    suspend fun getFirebaseNaverCustomToken(token: String): ResponseWrapper<String> {
        val request = firebaseFunctionsApiService.getFirebaseNaverCustomToken(TokenRequest(token))
        return if (request.isSuccessful) {
            val responseData = request.body()
            val firebaseToken: String? = responseData?.firebaseToken
            ResponseWrapper.Success(firebaseToken!!)
        } else {
            ResponseWrapper.Error(request.toString())
        }
    }

}