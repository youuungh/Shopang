package com.ninezero.shopang.data.repository

import android.content.Context
import android.util.Log
import com.ninezero.shopang.R
import com.ninezero.shopang.data.network.ApiService
import com.ninezero.shopang.model.Category
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.ResponseWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class HomeRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
){

    private val errorMsg by lazy { context.getString(R.string.error_msg) }

    suspend fun getProducts(): ResponseWrapper<List<Product>> {
        return try {
            val products = apiService.getProducts()
            ResponseWrapper.Success(products)
        } catch (e: Exception) {
            ResponseWrapper.Error(msg = errorMsg)
        }
    }

    suspend fun getCategories(): ResponseWrapper<List<Category>> {
        return try {
            val products = apiService.getProducts()
            val categories = groupProductsByCategory(products)
            ResponseWrapper.Success(categories)
        } catch (e: Exception) {
            ResponseWrapper.Error(msg = errorMsg)
        }
    }

    private fun groupProductsByCategory(products: List<Product>): List<Category> {
        val categoryList = listOf(
            Category("vegetable", emptyList(), "https://i.imgur.com/cbEnhhZ.png", "채소"),
            Category("fruit", emptyList(), "https://i.imgur.com/e6U6Y2y.png", "과일"),
            Category("beverage", emptyList(), "https://i.imgur.com/8FbSgvC.png", "음료"),
            Category("dairy", emptyList(),"https://i.imgur.com/G1ZY381.png", "유제품"),
            Category("seafood", emptyList(), "https://i.imgur.com/CTiCpKk.png", "수산물"),
            Category("livestock", emptyList(), "https://i.imgur.com/z6ZmJCm.png", "축산물")
        )

        return categoryList.map { category ->
            val categoryProducts = products.filter { it.category == category.name }
            category.copy(products = categoryProducts)
        }
    }
}