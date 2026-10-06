package com.fithero.app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fithero.app.data.*
import com.fithero.app.data.network.TokenManager
import com.fithero.app.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class AppViewModel(context: Context) : ViewModel() {

    private val repository = AuthRepository(context)
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()
    private val _missions = MutableStateFlow<List<Mission>>(emptyList())
    val missions: StateFlow<List<Mission>> = _missions.asStateFlow()
    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast.asStateFlow()
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        TokenManager.onUnauthorized = { logout() }
        autoLogin()
    }

    private fun autoLogin() {
        if (!repository.hasSavedToken()) return
        repository.loadTokenToManager()
        viewModelScope.launch {
            val ok = doCargar()
            if (ok) _isLoggedIn.value = true
            else { repository.clearSession(); TokenManager.token = "" }
        }
    }

    fun login(username: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.loginUser(username, password)

                if (response.success) {
                    response.token?.let { TokenManager.token = it }
                    repository.registrarLogin()
                    doCargar()
                    _isLoggedIn.value = true
                    onSuccess()
                } else {
                    // Si falla pero el servidor no da un mensaje, evitamos el NullPointerException
                    onError(response.message ?: "Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                // Si el servidor está caído, no hay internet o lanza una excepción HTTP
                onError("Error de conexión: ${e.localizedMessage ?: "Desconocido"}")
            }
        }
    }

    fun logout() {
        repository.clearSession()
        TokenManager.token = ""
        _isLoggedIn.value = false
        _user.value = User()
        _missions.value = emptyList()
    }

    private suspend fun doCargar(): Boolean {
        val backendUser = repository.getMe() ?: return false
        val safeUsername = backendUser.username ?: ""
        _user.value = User(
            name = (backendUser.name ?: "").ifEmpty { safeUsername },
            username = safeUsername,
            email = backendUser.email ?: "",
            level = backendUser.level, xp = backendUser.xp, xpNext = backendUser.xpNext,
            coins = backendUser.coins, logins = backendUser.logins,
            avatar = repository.getAvatar(),
            weeklyActivity = backendUser.weeklyActivity ?: List(7) { 0 },
            totalCalories = backendUser.totalCalories, totalTrainings = backendUser.totalTrainings,
            memberSince = backendUser.createdAt?.substring(0, 4) ?: "2024",
        )
        val backendMissions = repository.getMisiones()
        if (backendMissions.isNotEmpty()) {
            _missions.value = backendMissions.map { m ->
                Mission(
                    id = m.id.toInt(), title = m.title, desc = m.description,
                    icon = m.icon, colorHex = m.colorHex, status = m.status,
                    progress = m.progress, goal = m.goal, unit = m.unit,
                    xpReward = m.xpReward, coinReward = m.coinReward, category = m.category,
                    deadline = m.deadline, predefined = m.predefined, claimed = m.claimed
                )
            }
        } else loadMissionsForToday()
        return true
    }

    fun cargarDatos() {
        viewModelScope.launch { doCargar() }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            doCargar()
            _isRefreshing.value = false
        }
    }

    private fun loadMissionsForToday() {
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val todayMissions = DAILY_MISSIONS[dayOfWeek] ?: emptyList()
        _missions.value = todayMissions
        viewModelScope.launch {
            todayMissions.forEach { mission ->
                repository.crearMision(BackendMission(
                    title = mission.title, description = mission.desc, icon = mission.icon,
                    colorHex = mission.colorHex, goal = mission.goal, unit = mission.unit,
                    xpReward = mission.xpReward, coinReward = mission.coinReward,
                    category = mission.category, predefined = true
                ))
            }
        }
    }

    fun updateMission(id: Int, delta: Int) {
        viewModelScope.launch {
            val m = repository.actualizarProgreso(id.toLong(), delta)
            if (m != null) {
                _missions.value = _missions.value.map { if (it.id == id) it.copy(progress = m.progress, status = m.status) else it }
            }
        }
    }

    fun claimReward(id: Int) {
        viewModelScope.launch {
            val result = repository.reclamarRecompensa(id.toLong())
            if (result != null) {
                _missions.value = _missions.value.map { if (it.id == id) it.copy(claimed = true) else it }
                cargarDatos()
                showToast("🎁 +${result.xpReward} XP y +${result.coinReward} 🪙 recogidos!")
            } else {
                showToast("❌ Error al recoger recompensa")
            }
        }
    }

    fun editMission(id: Int, mission: BackendMission) {
        viewModelScope.launch {
            // Llamada a la API que devuelve un BackendMission (con 'description')
            val result = repository.editarMision(id.toLong(), mission)

            if (result != null) {
                _missions.value = _missions.value.map { m ->
                    if (m.id == id) {
                        // Usamos la estructura de la clase 'Mission' (la de tu lista)
                        m.copy(
                            title = result.title,
                            desc = result.description,
                            icon = result.icon,
                            colorHex = result.colorHex,
                            status = result.status,
                            progress = result.progress,
                            goal = result.goal,
                            unit = result.unit,
                            xpReward = result.xpReward,
                            coinReward = result.coinReward,
                            category = result.category,
                            deadline = result.deadline,
                            predefined = result.predefined,
                            claimed = result.claimed
                        )
                    } else {
                        m
                    }
                }
                showToast("✏️ Misión actualizada")
            } else {
                showToast("❌ Error al actualizar")
            }
        }
    }

    fun deleteMission(id: Int) {
        viewModelScope.launch {
            if (repository.eliminarMision(id.toLong())) {
                _missions.value = _missions.value.filter { it.id != id }.toList()
                showToast("🗑️ Misión eliminada")
            }
        }
    }

    fun saveProfile(name: String, username: String, email: String, avatar: String) {
        viewModelScope.launch {
            repository.saveAvatar(avatar) // guardar localmente
            val updated = repository.updateProfile(name, username, email, null)
            if (updated != null) {
                _user.value = _user.value.copy(name = name, username = username, email = email, avatar = avatar)
                showToast("✅ Perfil actualizado")
            }
        }
    }

    fun buyItem(item: ShopItem) {
        if (_user.value.coins < item.price) { showToast("❌ No tienes suficientes monedas"); return }
        viewModelScope.launch {
            val updated = repository.gastarMonedas(item.price)
            if (updated != null) {
                _user.value = _user.value.let { u ->
                    val baseUser = u.copy(coins = updated.coins, inventory = u.inventory + item.id)
                    when (item.effect) {
                        "double_xp"          -> { showToast("🧪 ¡Doble XP activado!"); baseUser.copy(doubleXpActive = true) }
                        "extra_coins"        -> { showToast("🧲 ¡Más monedas activado!"); baseUser.copy(extraCoinsActive = true) }
                        "strength_boost"     -> { showToast("🥤 Bonus Fuerza activado"); baseUser.copy(strengthBoost = true) }
                        "cardio_boost"       -> { showToast("⚡ Bonus Cardio activado"); baseUser.copy(cardioBoost = true) }
                        "mind_boost"         -> { showToast("🧘 Bonus Mente activado"); baseUser }
                        "health_boost"       -> { showToast("💧 Bonus Salud activado"); baseUser }
                        "triple_xp"          -> { showToast("💜 ¡Triple XP activado!"); baseUser.copy(doubleXpActive = true) }
                        "streak_shield",
                        "streak_guard"       -> { showToast("🛡️ Racha protegida mañana"); baseUser }
                        "instant_xp" -> {
                            showToast("🎁 +300 XP Recibidos")
                            var xp = baseUser.xp + 300; var level = baseUser.level; var xpNext = baseUser.xpNext
                            while (xp >= xpNext) { xp -= xpNext; level++; xpNext = (xpNext * 1.4).toInt() }
                            baseUser.copy(xp = xp, level = level, xpNext = xpNext)
                        }
                        "instant_xp_mega" -> {
                            showToast("🚀 +1000 XP Recibidos!")
                            var xp = baseUser.xp + 1000; var level = baseUser.level; var xpNext = baseUser.xpNext
                            while (xp >= xpNext) { xp -= xpNext; level++; xpNext = (xpNext * 1.4).toInt() }
                            baseUser.copy(xp = xp, level = level, xpNext = xpNext)
                        }
                        "mystery_chest" -> {
                            val rand = (1..3).random()
                            when (rand) {
                                1 -> { showToast("📦 ¡Cofre: +500 XP!"); baseUser.copy(xp = baseUser.xp + 500) }
                                2 -> { showToast("📦 ¡Cofre: +200 Monedas!"); baseUser.copy(coins = baseUser.coins + 200) }
                                else -> { showToast("📦 ¡Cofre: Doble XP activado!"); baseUser.copy(doubleXpActive = true) }
                            }
                        }
                        "role_titan"         -> { showToast("🏋️ ¡Ahora eres Titán Legendario!"); baseUser.copy(role = "Titán Legendario") }
                        "title_warrior"      -> { showToast("⚔️ Título: Guerrero Imparable"); baseUser.copy(role = "Guerrero Imparable") }
                        "frame_legend"       -> { showToast("👑 Marco Legendario equipado"); baseUser }
                        "skin_gold"          -> { showToast("✨ Piel de Oro activada"); baseUser }
                        else                 -> { showToast("✅ ¡Comprado: ${item.name}!"); baseUser }
                    }

                }
            } else showToast("❌ Error al comprar")
        }
    }

    fun sincronizarCalorias(calorias: Int) {
        viewModelScope.launch {
            val updated = repository.completarMision(0, 0, calorias)
            if (updated != null) _user.value = _user.value.copy(totalCalories = updated.totalCalories)
        }
    }

    fun register(username: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val errorMessage = repository.registerUser(username, email, password)

            if (errorMessage == null) {
                onSuccess()
            } else {
                // Esto pintará el mensaje real del servidor en tu UI
                onError(errorMessage)
            }
        }
    }

    fun cambiarPassword(newPassword: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(repository.changePassword(newPassword)) }
    }

    fun crearMision(mission: BackendMission) {
        viewModelScope.launch {
            val nueva = repository.crearMision(mission)
            if (nueva != null) { cargarDatos(); showToast("✅ Misión creada") }
            else showToast("❌ Límite de 5 misiones diarias alcanzado")
        }
    }

    private fun showToast(msg: String) {
        _toast.value = msg
        viewModelScope.launch { delay(2200); _toast.value = null }
    }
}