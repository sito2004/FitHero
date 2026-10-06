package com.fithero.app.data

import java.util.Calendar

data class User(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val avatar: String = "🦸",
    val level: Int = 1,
    val xp: Int = 0,
    val xpNext: Int = 500,
    val coins: Int = 500,
    val logins: Int = 0,
    val memberSince: String = "2024",
    val role: String = "Héroe Novato",
    val weeklyActivity: List<Int> = List(7) { 0 },
    val inventory: List<Int> = emptyList(),
    val totalCalories: Int = 0,
    val totalTrainings: Int = 0,
    val doubleXpActive: Boolean = false,
    val extraCoinsActive: Boolean = false,
    val strengthBoost: Boolean = false,
    val cardioBoost: Boolean = false
)

data class Mission(
    val id: Int,
    val title: String,
    val desc: String,
    val icon: String,
    val colorHex: String,
    val status: String = "active",
    val progress: Int = 0,
    val goal: Int,
    val unit: String,
    val xpReward: Int,
    val coinReward: Int,
    val category: String,
    val deadline: String? = null,
    val predefined: Boolean = true,
    val claimed: Boolean = false
)

data class ShopItem(
    val id: Int, val name: String, val brand: String, val price: Int,
    val tag: String?, val emoji: String, val category: String,
    val description: String = "", val effect: String = ""
)

data class AchievementDef(
    val id: String, val label: String, val icon: String, val colorHex: String,
    val condition: (User) -> Boolean
)

val DAILY_MISSIONS = mapOf(
    Calendar.MONDAY to listOf(
        Mission(101, "Press de Banca", "Completa 3 series de 10", "💪", "#7C3AED", goal = 3, unit = "series", xpReward = 400, coinReward = 40, category = "fuerza"),
        Mission(102, "Trote Ligero", "2km para calentar", "🏃", "#F59E0B", goal = 2, unit = "km", xpReward = 200, coinReward = 20, category = "cardio"),
        Mission(103, "Flexiones", "Realiza 20 rápidas", "⚡", "#7C3AED", goal = 20, unit = "reps", xpReward = 300, coinReward = 30, category = "fuerza"),
        Mission(104, "Agua Matutina", "Bebe 1L de agua", "💧", "#10B981", goal = 1, unit = "L", xpReward = 150, coinReward = 15, category = "salud"),
        Mission(105, "Sentadillas", "30 profundas", "🦵", "#7C3AED", goal = 30, unit = "reps", xpReward = 350, coinReward = 35, category = "fuerza")
    ),
    Calendar.TUESDAY to listOf(
        Mission(201, "Carrera 5km", "Corre sin parar", "🏃", "#F59E0B", goal = 5, unit = "km", xpReward = 600, coinReward = 60, category = "cardio"),
        Mission(202, "Abdominales", "Haz 50 crunches", "🤸", "#10B981", goal = 50, unit = "reps", xpReward = 300, coinReward = 30, category = "fuerza"),
        Mission(203, "Cuerda", "Salta 5 minutos", "🪢", "#F59E0B", goal = 5, unit = "min", xpReward = 400, coinReward = 40, category = "cardio"),
        Mission(204, "Burpees", "Completa 15", "🔥", "#EF4444", goal = 15, unit = "reps", xpReward = 500, coinReward = 50, category = "cardio")
    ),
    Calendar.WEDNESDAY to listOf(
        Mission(301, "Yoga Flow", "Estiramientos suaves", "🧘", "#06B6D4", goal = 15, unit = "min", xpReward = 350, coinReward = 35, category = "mente"),
        Mission(302, "Sentadillas Goblet", "3 series pesadas", "🦵", "#7C3AED", goal = 3, unit = "series", xpReward = 400, coinReward = 40, category = "fuerza"),
        Mission(303, "Meditación", "10 min de paz", "🎧", "#06B6D4", goal = 10, unit = "min", xpReward = 200, coinReward = 20, category = "mente")
    ),
    Calendar.THURSDAY to listOf(
        Mission(401, "Boxeo Sombra", "10 min de cardio", "🥊", "#EF4444", goal = 10, unit = "min", xpReward = 500, coinReward = 50, category = "cardio"),
        Mission(402, "Diamond Pushups", "20 cerradas", "💎", "#7C3AED", goal = 20, unit = "reps", xpReward = 450, coinReward = 45, category = "fuerza"),
        Mission(403, "Pull-ups", "Logra 10 dominadas", "💪", "#7C3AED", goal = 10, unit = "reps", xpReward = 600, coinReward = 60, category = "fuerza")
    ),
    Calendar.FRIDAY to listOf(
        Mission(501, "HIIT Intenso", "20 min intervalos", "⚡", "#F59E0B", goal = 20, unit = "min", xpReward = 700, coinReward = 70, category = "cardio"),
        Mission(502, "Zancadas", "30 por pierna", "🦵", "#10B981", goal = 60, unit = "pasos", xpReward = 400, coinReward = 40, category = "fuerza"),
        Mission(503, "Box Jumps", "15 saltos verticales", "📦", "#F59E0B", goal = 15, unit = "saltos", xpReward = 350, coinReward = 35, category = "cardio")
    ),
    Calendar.SATURDAY to listOf(
        Mission(601, "Gran Ruta", "10km campo", "🌲", "#10B981", goal = 10, unit = "km", xpReward = 1000, coinReward = 100, category = "cardio"),
        Mission(602, "Plancha Pro", "Aguanta 2 min", "⏳", "#7C3AED", goal = 120, unit = "seg", xpReward = 500, coinReward = 50, category = "fuerza"),
        Mission(603, "Piscina", "500m libres", "🏊", "#06B6D4", goal = 500, unit = "metros", xpReward = 800, coinReward = 80, category = "cardio")
    ),
    Calendar.SUNDAY to listOf(
        Mission(701, "Relax Total", "Meditación profunda", "🧘", "#06B6D4", goal = 20, unit = "min", xpReward = 400, coinReward = 40, category = "mente"),
        Mission(702, "Meal Prep", "Comidas de la semana", "🥗", "#10B981", goal = 1, unit = "vez", xpReward = 200, coinReward = 20, category = "salud"),
        Mission(703, "Caminata", "30 min barrio", "🚶", "#F59E0B", goal = 30, unit = "min", xpReward = 250, coinReward = 25, category = "cardio")
    )
)

val SHOP_ITEMS = listOf(
    // ── POTENCIADORES ─────────────────────────────────────────────────
    ShopItem(1,  "Poción Doble XP",     "PowerUp",  250,  "HOT",     "🧪", "Potenciadores", "Duplica la XP de todas tus misiones completadas hoy.",        "double_xp"),
    ShopItem(2,  "Imán de Monedas",     "PowerUp",  200,  "NUEVO",   "🧲", "Potenciadores", "Gana +50% de monedas extra en tus misiones.",                 "extra_coins"),
    ShopItem(3,  "Ticket de Reset",     "Especial", 150,  null,      "🎟️","Potenciadores", "Recarga misiones nuevas para hoy.",                            "reset_missions"),
    ShopItem(10, "Elixir Triple XP",    "LegendUp", 600,  "ÉPICO",   "💜","Potenciadores", "Triplica la XP ganada durante 3 misiones.",                   "triple_xp"),
    ShopItem(11, "Racha Protegida",     "SafeUp",   350,  null,      "🔒","Potenciadores", "Tu racha no se romperá mañana aunque no hagas login.",         "streak_shield"),
    ShopItem(12, "Misión Extra",        "BonusUp",  180,  "NUEVO",   "➕","Potenciadores", "Añade una misión aleatoria extra al día de hoy.",              "extra_mission"),

    // ── CONSUMIBLES ───────────────────────────────────────────────────
    ShopItem(4,  "Batido de Proteína",  "Muscle",   300,  null,      "🥤","Consumibles",   "+25% XP extra en misiones de FUERZA.",                        "strength_boost"),
    ShopItem(5,  "Bebida Isotónica",    "Nitro",    300,  null,      "⚡","Consumibles",   "+25% XP extra en misiones de CARDIO.",                        "cardio_boost"),
    ShopItem(6,  "Pack XP Instantánea","Legacy",   400,  "SALE",    "🎁","Consumibles",   "Recibe +300 XP al instante.",                                 "instant_xp"),
    ShopItem(13, "Vitaminas Zen",       "MindUp",   250,  null,      "🧘","Consumibles",   "+25% XP extra en misiones de MENTE.",                         "mind_boost"),
    ShopItem(14, "Agua Energética",     "HydroUp",  200,  null,      "💧","Consumibles",   "+25% XP extra en misiones de SALUD.",                         "health_boost"),
    ShopItem(15, "Mega Pack XP",        "MegaUp",   800,  "TOP",     "🚀","Consumibles",   "Recibe +1000 XP al instante.",                                "instant_xp_mega"),
    ShopItem(16, "Monedas x2",          "CoinUp",   350,  null,      "🪙","Consumibles",   "Dobla las monedas de tu próxima misión completada.",           "double_coins_next"),

    // ── ESPECIALES ────────────────────────────────────────────────────
    ShopItem(7,  "Escudo Protector",    "Guard",    500,  "PREMIUM", "🛡️","Especiales",   "Evita perder tu racha de login mañana.",                      "streak_guard"),
    ShopItem(8,  "Cinturón de Titán",   "Elite",    1200, "TOP",     "🏋️","Especiales",   "Cambia tu rol a 'Titán Legendario' permanentemente.",          "role_titan"),
    ShopItem(9,  "Piel de Oro",         "Visual",   800,  null,      "✨","Especiales",   "Efecto dorado especial en tu perfil.",                         "skin_gold"),
    ShopItem(17, "Marco Legendario",    "Visual",   600,  "NUEVO",   "👑","Especiales",   "Marco especial de leyenda en tu avatar.",                      "frame_legend"),
    ShopItem(18, "Título: Guerrero",    "Title",    400,  null,      "⚔️","Especiales",   "Desbloquea el título 'Guerrero Imparable'.",                   "title_warrior"),
    ShopItem(19, "Cofre Misterioso",    "Mystery",  500,  "ÉPICO",   "📦","Especiales",   "Contiene una recompensa aleatoria: XP, monedas o item raro.", "mystery_chest"),
    ShopItem(20, "Resetear Misiones",   "Admin",    1000, null,      "🔄","Especiales",   "Elimina todas tus misiones completadas y recarga el día.",     "reset_all")
)


val ACHIEVEMENT_DEFS = listOf(
    AchievementDef("a1", "Primer Paso", "👣", "#7C3AED") { u -> u.logins >= 1 },
    AchievementDef("a2", "Madrugador", "🌅", "#F59E0B") { u -> u.logins >= 3 },
    AchievementDef("a3", "Fuerza Bruta", "💪", "#EF4444") { u -> u.totalTrainings >= 5 },
    AchievementDef("a4", "Corredor", "🏃", "#3B82F6") { u -> u.totalTrainings >= 10 },
    AchievementDef("a5", "Hidratado", "💧", "#10B981") { u -> u.totalCalories >= 1000 },
    AchievementDef("a6", "Nivel 5", "⭐", "#F59E0B") { u -> u.level >= 5 },
    AchievementDef("a7", "Coleccionista", "🛍️", "#A855F7") { u -> u.inventory.isNotEmpty() },
    AchievementDef("a8", "Racha 7 días", "🔥", "#EF4444") { u -> u.logins >= 7 },
    AchievementDef("a9", "Zen Master", "🧘", "#06B6D4") { u -> u.totalTrainings >= 15 },
    AchievementDef("a10", "Ahorrador", "💰", "#10B981") { u -> u.coins >= 1000 },
    AchievementDef("a11", "Titán", "🔱", "#7C3AED") { u -> u.level >= 10 },
    AchievementDef("a12", "Velocista", "⚡", "#F59E0B") { u -> u.totalCalories >= 5000 },
    AchievementDef("a13", "Explorador", "🗺️", "#6366F1") { u -> u.totalTrainings >= 20 },
    AchievementDef("a14", "Guerrero", "⚔️", "#EF4444") { u -> u.level >= 15 },
    AchievementDef("a15", "Leyenda", "👑", "#F59E0B") { u -> u.level >= 20 },
    AchievementDef("a16", "Nutrición Pro", "🍎", "#10B981") { u -> u.inventory.size >= 3 },
    AchievementDef("a17", "Fiel Héroe", "❤️", "#EC4899") { u -> u.logins >= 30 },
    AchievementDef("a18", "Maestro Pesas", "🏋️", "#4B5563") { u -> u.totalTrainings >= 50 },
    AchievementDef("a19", "Atleta Elite", "🏆", "#F59E0B") { u -> u.xp >= 10000 },
    AchievementDef("a20", "Inmortal", "✨", "#A855F7") { u -> u.level >= 50 }
)

val AVATARS = listOf("🦸", "🦹", "🥷", "🧙", "🧛", "🧟", "🧞", "🧜", "🧚", "👽", "🤖", "🦊", "🦁", "🐯", "🐼")

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String?, val message: String, val success: Boolean)
data class BackendUser(
    val id: Long = 0, val name: String = "", val username: String = "",
    val email: String = "", val avatarUrl: String? = null,
    val xp: Int = 0, val xpNext: Int = 500, val level: Int = 1, val coins: Int = 0,
    val logins: Int = 0, val totalCalories: Int = 0, val totalTrainings: Int = 0,
    val weeklyActivity: List<Int> = List(7) { 0 }, val role: String = "ROLE_USER",
    val createdAt: String? = null
)
data class RegisterRequest(val username: String, val email: String, val password: String)
data class BackendMission(
    val id: Long = 0, val title: String = "", val description: String = "",
    val icon: String = "⚡", val colorHex: String = "#7C3AED", val status: String = "active",
    val progress: Int = 0, val goal: Int = 1, val unit: String = "",
    val xpReward: Int = 0, val coinReward: Int = 0, val category: String = "fuerza",
    val deadline: String? = null, val predefined: Boolean = false,
    val claimed: Boolean = false,
)
data class UpdateProfileRequest(val name: String? = null, val username: String? = null, val email: String? = null, val avatarUrl: String? = null)
data class CompleteMissionRequest(val xpReward: Int, val coinReward: Int, val calories: Int = 0)
data class SpendCoinsRequest(val amount: Int)
data class ClaimRewardResponse(val xpReward: Int, val coinReward: Int, val message: String)
