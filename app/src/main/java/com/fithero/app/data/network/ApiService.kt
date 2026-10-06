package com.fithero.app.data.network

import com.fithero.app.data.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ResponseBody>

    @GET("/api/users/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<BackendUser>

    @PUT("/api/users/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: UpdateProfileRequest
    ): Response<BackendUser>

    @POST("/api/users/me/login")
    suspend fun registrarLogin(@Header("Authorization") token: String): Response<BackendUser>

    @POST("/api/users/me/complete-mission")
    suspend fun completarMision(
        @Header("Authorization") token: String,
        @Body body: CompleteMissionRequest
    ): Response<BackendUser>

    @POST("/api/users/me/spend-coins")
    suspend fun gastarMonedas(
        @Header("Authorization") token: String,
        @Body body: SpendCoinsRequest
    ): Response<BackendUser>

    @GET("/api/missions")
    suspend fun getMisiones(@Header("Authorization") token: String): Response<List<BackendMission>>

    @POST("/api/missions")
    suspend fun crearMision(
        @Header("Authorization") token: String,
        @Body mission: BackendMission
    ): Response<BackendMission>

    @PATCH("/api/missions/{id}/progress")
    suspend fun actualizarProgreso(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body body: Map<String, Int>
    ): Response<BackendMission>

    @POST("/api/missions/{id}/claim")
    suspend fun reclamarRecompensa(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<ClaimRewardResponse>

    @POST("/api/missions/{id}")
    suspend fun editarMision(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body mission: BackendMission
    ): Response<BackendMission>

    @DELETE("/api/missions/{id}")
    suspend fun eliminarMision(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Void>

    @PATCH("/api/users/me/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<Void>
}