package ru.netology.learningandtrying.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.learningandtrying.dto.Media
import ru.netology.learningandtrying.dto.Post
import ru.netology.learningandtrying.dto.PushToken
import ru.netology.learningandtrying.model.Token

interface AuthApi {
    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun auth(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<Token>
}

interface PostApi {
    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post): Post

    @DELETE("posts/{id}")
    suspend fun delete(@Path("id") id: Long)

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Post

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Post

    @GET("posts/{id}/before")
    suspend fun getBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("posts/{id}/after")
    suspend fun getAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>
    
    @Multipart
    @POST("media")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Media

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken)
}
