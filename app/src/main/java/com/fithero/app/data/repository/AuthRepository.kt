package com.fithero.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.fithero.app.data.*
import com.fithero.app.data.network.RetrofitClient

class AuthRepository(context: Context) {
    private val api = RetrofitClient.instance
    private val prefs: SharedPreferences = context.getSharedPreferences("fithero_prefs", Context.MODE_PRIVATE)

    fun saveSession(token: String) = prefs.edit().putString("auth_token", token).apply()
    fun clearSession() = prefs.edit().remove("auth_token").apply()
    fun isLoggedIn() = prefs.getString("auth_token", null) != null
    fun hasSavedToken(): Boolean = !prefs.getString("auth_token", null).isNullOrEmpty()
    fun loadTokenToManager() {
        val rawToken = prefs.getString("auth_token", "") ?: ""
        com.fithero.app.data.network.TokenManager.token = rawToken
    }
    fun getToken() = "Bearer ${prefs.getString("auth_token", "")}"

    suspend fun loginUser(username: String, password: String): LoginResponse {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.success && response.token != null) saveSession(response.token)
            response
        } catch (e: Exception) { LoginResponse(null, "Error: ${e.message}", false) }
    }

    suspend fun registerUser(username: String, email: String, password: String): String? {
        return try {
            val response = RetrofitClient.instance.register(RegisterRequest(username, email, password))
            if (response.isSuccessful) {
                null
            } else {
                // Leemos el error con cuidado
                response.errorBody()?.string() ?: "Error desconocido"
            }
        } catch (e: Exception) {
            // Si hay una excepción de red, devolvemos el error técnico
            e.message ?: "Error de conexión"
        }
    }

    suspend fun getMe(): BackendUser? {
        return try {
            val r = api.getMe(getToken())
            if (r.isSuccessful) r.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun registrarLogin(): BackendUser? {
        return try {
            val r = api.registrarLogin(getToken())
            if (r.isSuccessful) r.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun updateProfile(name: String?, username: String?, email: String?, avatarUrl: String?): BackendUser? {
        return try {
            val r = api.updateProfile(getToken(), UpdateProfileRequest(name, username, email, avatarUrl))
            if (r.isSuccessful) r.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun completarMision(xpReward: Int, coinReward: Int, calories: Int): BackendUser? {
        return try {
            val r = api.completarMision(getToken(), CompleteMissionRequest(xpReward, coinReward, calories))
            if (r.isSuccessful) r.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun gastarMonedas(amount: Int): BackendUser? {
        return try {
            val r = api.gastarMonedas(getToken(), SpendCoinsRequest(amount))
            if (r.isSuccessful) r.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun getMisiones(): List<BackendMission> {
        return try {
            val r = api.getMisiones(getToken())
            if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun crearMision(mission: BackendMission): BackendMission? {
        return try {
            val r = api.crearMision(getToken(), mission)
            if (r.isSuccessful) r.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun actualizarProgreso(id: Long, increment: Int): BackendMission? {
        return try {
            val r = api.actualizarProgreso(getToken(), id, mapOf("increment" to increment))
            if (r.isSuccessful) r.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun reclamarRecompensa(id: Long): ClaimRewardResponse? {
        return try {
            val r = api.reclamarRecompensa(getToken(), id)
            if (r.isSuccessful) r.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun editarMision(id: Long, mission: BackendMission): BackendMission? {
        return try {
            val response = api.editarMision(getToken(), id, mission)
            if (response.isSuccessful) {
                response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("API_ERROR", "editarMision ${response.code()}: $errorBody")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("API_ERROR", "editarMision excepción: ${e.message}")
            null
        }
    }

    suspend fun eliminarMision(id: Long): Boolean {
        return try { api.eliminarMision(getToken(), id).isSuccessful } catch (e: Exception) { false }
    }

    suspend fun changePassword(newPassword: String): Boolean {
        return try {
            val r = api.changePassword(getToken(), mapOf("newPassword" to newPassword))
            r.isSuccessful
        } catch (e: Exception) { false }
    }

    fun saveAvatar(avatar: String) = prefs.edit().putString("user_avatar", avatar).apply()
    fun getAvatar() = prefs.getString("user_avatar", "🦸") ?: "🦸"
}