package io.cloudx.demo.demoapp.dynamic

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ConfigService {

    @GET
    suspend fun getAppConfig(@Url appKey: String): Response<AppConfig>
}