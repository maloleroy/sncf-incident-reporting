package com.example.appv1.api
import com.example.appv1.BuildConfig // Import BuildConfig to access the API key
import okhttp3.Interceptor // Import Interceptor
import okhttp3.OkHttpClient // Import OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // Create an OkHttpClient with an Interceptor to add the Authorization header
    private val client = OkHttpClient.Builder() // OkHttpClient is now resolved
        .addInterceptor(Interceptor { chain -> // Interceptor lambda syntax
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                // Add the Authorization header using the key from BuildConfig
                .header("Authorization", "Bearer ${BuildConfig.BACKEND_PASSWORD}") // BuildConfig is now resolved
            val request = requestBuilder.build()
            chain.proceed(request) // proceed is now resolved
        })
        .build()

    val api: MistralApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL) // Use the backend URL from BuildConfig
            .client(client) // Use the client with the interceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MistralApi::class.java)
    }
}