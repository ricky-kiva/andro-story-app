package com.rickyslash.storyapp.api

import com.rickyslash.storyapp.api.response.AddStoryResponse
import com.rickyslash.storyapp.api.response.AllStoriesResponse
import com.rickyslash.storyapp.api.response.LoginResponse
import com.rickyslash.storyapp.api.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("/v1//register")
    fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("/v1//login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("/v1/stories")
    fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("location") location: Int = 0
    ): Call<AllStoriesResponse>

    @Multipart
    @POST("/v1/stories")
    fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddStoryResponse>

    @GET("/v1/stories")
    suspend fun getStoriesForPaging(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("location") location: Int = 0
    ): AllStoriesResponse

}