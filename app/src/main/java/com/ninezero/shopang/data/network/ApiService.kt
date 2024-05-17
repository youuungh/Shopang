package com.ninezero.shopang.data.network

import com.ninezero.shopang.model.Product
import retrofit2.http.GET

interface ApiService {

    @GET("/")
    suspend fun getProducts(): List<Product>

}