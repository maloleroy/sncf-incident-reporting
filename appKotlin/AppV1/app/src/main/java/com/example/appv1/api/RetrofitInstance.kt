package com.example.appv1.api
import android.content.Context
import com.example.appv1.BuildConfig
import com.example.appv1.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object RetrofitInstance {
    // Function to create the OkHttpClient with a Context
    private fun createSecureClient(context: Context): OkHttpClient {
        // Load the certificate from res/raw
        val certificate: X509Certificate = loadCertificate(context)

        // Create a KeyStore and TrustManager
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setCertificateEntry("ca", certificate)
        }

        val trustManagerFactory = TrustManagerFactory
            .getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
                init(keyStore)
            }

        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, trustManagerFactory.trustManagers, null)
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer ${BuildConfig.BACKEND_PASSWORD}")
                    .build()
                chain.proceed(request)
            }
            .sslSocketFactory(
                sslContext.socketFactory,
                trustManagerFactory.trustManagers[0] as X509TrustManager
            )
            .build()
    }

    // Load the certificate using Android's Resources
    private fun loadCertificate(context: Context): X509Certificate {
        val inputStream = context.resources.openRawResource(R.raw.cert)  // Use R.raw.cert
        val certificate = CertificateFactory.getInstance("X.509")
            .generateCertificate(inputStream) as X509Certificate
        inputStream.close()
        return certificate
    }

    fun getIncidentApiService(context: Context): IncidentAnalysisApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL)
            .client(createSecureClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IncidentAnalysisApiService::class.java)
    }
}