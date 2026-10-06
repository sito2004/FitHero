package com.fithero.app.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // <--- IMPORTANTE
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Mantén esto tal cual, es correcto
    private const val BASE_URL = "https://proyectotfg-production-2338.up.railway.app/"

    // El logger automático
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // El cliente que usa el logger automáticamente
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging) // ESTO IMPRIME LA URL POR TI
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

object TokenManager {
    var token: String = ""
    var onUnauthorized: (() -> Unit)? = null
}

private val authInterceptor = Interceptor { chain ->
    val request = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer ${TokenManager.token}")
        .addHeader("Accept", "application/json")
        .addHeader("Origin", "https://proyectotfg-production-2338.up.railway.app/")
        .build()
    val response = chain.proceed(request)
    if (response.code == 401) {
        TokenManager.token = ""
        TokenManager.onUnauthorized?.invoke()
    }
    response
}

