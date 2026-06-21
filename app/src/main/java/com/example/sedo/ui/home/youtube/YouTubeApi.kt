package com.example.sedo.ui.home.youtube

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class YouTubeResponse(val items: List<VideoItem>)
data class VideoItem(val id: VideoId, val snippet: VideoSnippet)
data class VideoId(val videoId: String?)
data class VideoSnippet(val title: String, val thumbnails: Thumbnails)
data class Thumbnails(val medium: Thumbnail)
data class Thumbnail(val url: String)

interface YouTubeService {
    @GET("youtube/v3/search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 1,
        @Query("type") type: String = "video",
        @Query("key") apiKey: String
    ): YouTubeResponse
}

object YouTubeClient {
    val service: YouTubeService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeService::class.java)
    }
}