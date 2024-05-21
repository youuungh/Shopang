package com.ninezero.shopang.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ninezero.shopang.model.Product

@Database(
    entities = [Product::class],
    version = 1,
    exportSchema = false
)
abstract class WishDatabase : RoomDatabase() {
    abstract fun getWishDao(): WishDao
}