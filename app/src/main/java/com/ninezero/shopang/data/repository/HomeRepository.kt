package com.ninezero.shopang.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ninezero.shopang.R
import com.ninezero.shopang.data.local.WishDao
import com.ninezero.shopang.data.network.ApiService
import com.ninezero.shopang.model.Category
import com.ninezero.shopang.model.Order
import com.ninezero.shopang.model.OrderEnums
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.model.convertDocumentToOrder
import com.ninezero.shopang.util.CART_COLLECTION
import com.ninezero.shopang.util.PENDING_ORDERS
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.USER_COLLECTION
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class HomeRepository @Inject constructor(
    private val apiService: ApiService,
    private val wishDao: WishDao,
    private val fAuth: FirebaseAuth,
    private val fStore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {

    private val userUid: String
        get() = fAuth.uid!!

    private val userCart by lazy {
        fStore.collection(USER_COLLECTION).document(userUid).collection(CART_COLLECTION)
    }

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
            Category("dairy", emptyList(), "https://i.imgur.com/G1ZY381.png", "유제품"),
            Category("seafood", emptyList(), "https://i.imgur.com/CTiCpKk.png", "수산물"),
            Category("livestock", emptyList(), "https://i.imgur.com/z6ZmJCm.png", "축산물")
        )

        return categoryList.map { category ->
            val categoryProducts = products.filter { it.category == category.name }
            category.copy(products = categoryProducts)
        }
    }

    private suspend fun getProductFromWish(id: String): Boolean {
        return wishDao.getWishProductById(id) != null
    }

    fun getProductFromWishLiveData(id: String): LiveData<Product?> =
        wishDao.getWishProductByIdLiveData(id)

    fun getAllWishesLiveData(): LiveData<List<Product>> =
        wishDao.getAllWishes()

    suspend fun toggleProductInWish(product: Product) {
        if (getProductFromWish(product.id)) {
            wishDao.deleteWish(product)
        } else {
            wishDao.saveProduct(product)
        }
    }

    suspend fun deleteFromUserCarts(productId: String) {
        userCart.document(productId).delete().await()
    }

    suspend fun getUserCarts(): ResponseWrapper<List<Product>> {
        return try {
            val result = userCart.get().await()
            val products = result.documents.map { Product.fromDocumentSnapshot(it) }
            ResponseWrapper.Success(products)
        } catch (e: Exception) {
            ResponseWrapper.Error(errorMsg)
        }
    }

    suspend fun addProductToCartFromWish(product: Product): ResponseWrapper<Any> {
        return try {
            val cartProductsList = getUserCarts().data.orEmpty()
            cartProductsList.find { it.id == product.id }?.let {
                product.quantity += it.quantity
            }
            userCart.document(product.id).set(product).await()
            wishDao.deleteWish(product)
            ResponseWrapper.Success(Any())
        } catch (e: Exception) {
            ResponseWrapper.Error(errorMsg)
        }
    }

    suspend fun addProductsToCart(
        list: List<Product>,
        deleteWishlistProducts: Boolean
    ): ResponseWrapper<Any> {
        return try {
            val cartProductsList = getUserCarts().data.orEmpty()
            list.forEach { product ->
                cartProductsList.find { it.id == product.id }?.let {
                    product.quantity += it.quantity
                }
                userCart.document(product.id).set(product).await()
            }
            if (deleteWishlistProducts) {
                wishDao.deleteAllWishes()
            }
            ResponseWrapper.Success(Any())
        } catch (e: Exception) {
            ResponseWrapper.Error(errorMsg)
        }
    }

    suspend fun submitUserOrder(
        cartList: Array<Product>,
        userAddress: String,
        totalPrice: Int
    ): ResponseWrapper<Order> {
        return try {
            val orderCollection = fStore.collection(PENDING_ORDERS)
            val orderId = orderCollection.document().id
            val order = Order(
                orderId,
                userUid,
                System.currentTimeMillis(),
                userAddress,
                OrderEnums.PLACED,
                totalPrice,
                cartList.toList()
            )
            orderCollection.document(orderId).set(order.toMap()).await()
            clearUserCart()
            ResponseWrapper.Success(order)
        } catch (e: Exception) {
            ResponseWrapper.Error(errorMsg)
        }
    }

    private suspend fun clearUserCart() {
        userCart.get().await().let {
            it.forEach { doc ->
                userCart.document(doc.id).delete().await()
            }
        }
    }

    suspend fun getUserOrders(): ResponseWrapper<List<Order>> {
        return try {
            val result = fStore.collection(PENDING_ORDERS)
                .whereEqualTo("userUid", userUid)
                .get()
                .await()
            val orders = convertDocumentToOrder(result.documents)
            ResponseWrapper.Success(orders)
        } catch (e: Exception) {
            ResponseWrapper.Error(errorMsg)
        }
    }
}