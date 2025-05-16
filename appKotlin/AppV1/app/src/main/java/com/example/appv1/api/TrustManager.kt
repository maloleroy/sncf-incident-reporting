package com.example.appv1.api

import android.content.Context
import com.example.appv1.BuildConfig
import com.example.appv1.R
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

// Load the certificate from raw resources
fun getCertificate(context: Context): Certificate {
    val cf = CertificateFactory.getInstance("X.509")
    val certInput: InputStream = context.resources.openRawResource(R.raw.cert)
    return cf.generateCertificate(certInput).also { certInput.close() }
}

// Configure OkHttp to trust your certificate
fun createSecureClient(context: Context): OkHttpClient {
    // Create a KeyStore with your certificate
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null, null)
        setCertificateEntry("ca", getCertificate(context))
    }

    // Create a TrustManager that trusts the KeyStore
    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
        init(keyStore)
    }

    // Create an SSLContext with the TrustManager
    val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, trustManagerFactory.trustManagers, null)
    }

    return OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustManagerFactory.trustManagers[0] as X509TrustManager)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", "Bearer ${BuildConfig.BACKEND_PASSWORD}")
                .build()
            chain.proceed(request)
        }
        .build()
}