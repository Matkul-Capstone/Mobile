package com.example.speaktoo.api.route

import com.example.speaktoo.api.model.sentences.SentencesResponse
import com.example.speaktoo.api.model.transcribe.TranscribeResponse
import com.example.speaktoo.api.model.user.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface SpeaktooAPI {

    @POST("/users/register")
    suspend fun registerUser(
        @Body user: RegisterRequest
    ): Response<RegisterResponse>

    @POST("/users/login")
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @PUT("/users/{uid}/type")
    suspend fun changeUserType(
        @Path("uid") userId: String,
        @Body userTypeRequest: ChangeUserTypeRequest
    ): Response<ChangeUserTypeResponse>

    @PUT("/users/{uid}/username")
    suspend fun changeUsername(
        @Path("uid") userId: String,
        @Body changeUsernameRequest: ChangeUsernameRequest
    ): Response<ChangeUsernameResponse>

    @PUT("/users/password")
    suspend fun newPassword(
        @Body newPasswordRequest: NewPasswordRequest
    ): Response<NewPasswordResponse>

    @PUT("/users/{uid}/score/{type}")
    suspend fun changeScore(
        @Path("uid") userId: String,
        @Path("type") type: String,
        @Body changeScoreRequest: ChangeScoreRequest
    ): Response<ChangeScoreResponse>

    @Multipart
    @POST("/transcribe/{sid}")
    suspend fun postAudio(
        @Path("sid") sentenceId: Int,
        @Part("chapter") chapter: RequestBody,
        @Part("uid") uid: RequestBody,
        @Part("sentence") sentence: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part audio: MultipartBody.Part
    ): Response<TranscribeResponse>

    @GET("sentences/{type}/{uid}")
    suspend fun getSentencesByType(
        @Path("type") sentenceType: String,
        @Path("uid") userId: String
    ): Response<SentencesResponse>
}