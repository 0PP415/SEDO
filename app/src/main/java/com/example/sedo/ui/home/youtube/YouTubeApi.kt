package com.example.sedo.ui.home.youtube

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 1. 받아올 데이터 구조 (DTO)
data class YouTubeResponse(val items: List<VideoItem>)
data class VideoItem(val id: VideoId, val snippet: VideoSnippet)
data class VideoId(val videoId: String?)
data class VideoSnippet(val title: String, val thumbnails: Thumbnails)
data class Thumbnails(val medium: Thumbnail)
data class Thumbnail(val url: String)

// 2. 통신 규격 (Interface)
interface YouTubeService {
    @GET("youtube/v3/search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 1, // ⭐️ 키워드당 무조건 1개만 가져오기!
        @Query("type") type: String = "video",
        @Query("key") apiKey: String
    ): YouTubeResponse
}

// 3. 통신 객체 (Client)
object YouTubeClient {
    val service: YouTubeService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeService::class.java)
    }
}