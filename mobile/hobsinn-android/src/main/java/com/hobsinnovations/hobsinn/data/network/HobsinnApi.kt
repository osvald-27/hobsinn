package com.hobsinnovations.hobsinn.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface HobsinnApi {
    @POST("/pickups/special-call")
    suspend fun createSpecialCall(@Body request: SpecialCallRequest): SpecialCallResponse
}

data class SpecialCallRequest(val lat: Double, val lng: Double, val bagCount: Int, val requestedDatetime: String)
data class SpecialCallResponse(val requestId: String, val estimatedCostXaf: Double, val status: String)

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: HobsinnApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HobsinnApi::class.java)
    }
}
