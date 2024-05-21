package com.ninezero.shopang.di

import android.content.Context
import androidx.room.Room
import com.ninezero.shopang.data.local.WishDatabase
import com.ninezero.shopang.util.WISH_DB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomDatabase {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            WishDatabase::class.java,
            WISH_DB
        ).build()

    @Singleton
    @Provides
    fun provideWishDao(db: WishDatabase) = db.getWishDao()

}