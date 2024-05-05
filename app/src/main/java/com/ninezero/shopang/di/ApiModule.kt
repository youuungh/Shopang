package com.ninezero.shopang.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ninezero.shopang.BuildConfig
import com.ninezero.shopang.data.network.FirebaseFunctionsApiService
import com.ninezero.shopang.util.FUNCTIONS_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        return builder.build()
    }

    @Singleton
    @Provides
    @Named("FirebaseFunctionsRetrofit")
    fun provideFirebaseFunctionsRetrofit(httpClient: OkHttpClient) =
        Retrofit.Builder()
            .baseUrl(FUNCTIONS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(httpClient)
            .build()

    @Singleton
    @Provides
    fun provideFirebaseFunctionsApiService(@Named("FirebaseFunctionsRetrofit") retrofit: Retrofit): FirebaseFunctionsApiService =
        retrofit.create(FirebaseFunctionsApiService::class.java)

}