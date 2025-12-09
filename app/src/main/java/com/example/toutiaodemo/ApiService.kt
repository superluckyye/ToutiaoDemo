package com.example.toutiaodemo

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("toutiao/index")
    suspend fun getNews(
        @Query("key") apiKey: String,
        @Query("type") type: String = "top",
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 30
    ): NewsResponse
}
