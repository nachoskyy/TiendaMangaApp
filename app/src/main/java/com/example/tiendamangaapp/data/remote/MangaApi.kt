package com.example.tiendamangaapp.data.remote

import com.google.gson.Gson
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class ApiManga(
    val mal_id: Long,
    val title: String
)

data class ApiResponse(
    val data: List<ApiManga>
)

interface MangaApiService {
    @GET("manga")
    suspend fun getMangas(
        @Query("q") query: String = "one piece",
        @Query("limit") limit: Int = 5
    ): ApiResponse
}

object MangaApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.jikan.moe/v4/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: MangaApiService = retrofit.create(MangaApiService::class.java)
}