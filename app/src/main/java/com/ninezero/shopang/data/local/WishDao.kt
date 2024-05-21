package com.ninezero.shopang.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.ninezero.shopang.model.Product

@Dao
interface WishDao {

    @Insert(onConflict = REPLACE)
    fun saveProduct(productModel: Product)

    @Query("SELECT * FROM Product")
    fun getAllWishes(): LiveData<List<Product>>

    @Query("SELECT * FROM Product WHERE id =:id")
    suspend fun getWishProductById(id: String): Product?

    @Query("SELECT * FROM Product WHERE id =:id")
    fun getWishProductByIdLiveData(id: String): LiveData<Product?>

    @Delete
    suspend fun deleteWish(productModel: Product)

    @Query("DELETE FROM Product")
    suspend fun deleteAllWishes()
}