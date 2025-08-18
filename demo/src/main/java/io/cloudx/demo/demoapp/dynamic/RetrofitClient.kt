package io.cloudx.demo.demoapp.dynamic

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://cloudfront-dev.cloudx.io/demoapp/"

    val configService: ConfigService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConfigService::class.java)
    }
}