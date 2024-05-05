package com.ninezero.shopang.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FirebaseFunctionsApiService {

    @Headers("Accept: application/json; charset=utf-8")
    @POST("naverCustomAuth")
    suspend fun getFirebaseNaverCustomToken(@Body body: TokenRequest): retrofit2.Response<TokenResponse>

}

data class TokenRequest(
    @SerializedName("token")
    val token: String
)

data class TokenResponse(
    @SerializedName("firebase_token")
    val firebaseToken: String
)