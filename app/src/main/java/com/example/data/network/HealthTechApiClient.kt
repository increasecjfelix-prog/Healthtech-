package com.example.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object HealthTechApiClient {

    // Default backend URL (matching cloud deployment or local development environment)
    private var baseUrl: String = "https://ais-dev-edqzvzclkfjisjwypslyxx-389052448695.europe-west2.run.app/"
    private var authToken: String? = null

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")

        authToken?.let { token ->
            builder.header("Authorization", "Bearer $token")
        }

        chain.proceed(builder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private var retrofitInstance: Retrofit? = null

    private fun getRetrofit(): Retrofit {
        return retrofitInstance ?: synchronized(this) {
            retrofitInstance ?: Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build().also { retrofitInstance = it }
        }
    }

    val restService: HealthTechRestService
        get() = getRetrofit().create(HealthTechRestService::class.java)

    val graphQLClient: HealthTechGraphQLClient
        get() = HealthTechGraphQLClient(
            endpointUrl = baseUrl.trimEnd('/') + "/graphql",
            okHttpClient = okHttpClient
        )

    fun setAuthToken(token: String?) {
        authToken = token
    }

    fun setBaseUrl(newUrl: String) {
        val formatted = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        if (formatted != baseUrl) {
            baseUrl = formatted
            retrofitInstance = null
        }
    }

    fun getBaseUrl(): String = baseUrl
}
