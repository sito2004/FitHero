package com.fithero.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fithero.app.data.ACHIEVEMENT_DEFS
import com.fithero.app.data.Mission
import com.fithero.app.data.User
import com.fithero.app.ui.theme.*

@Composable
fun ProfileScreen(
    user: User,
    missions: List<Mission>,
    onEditProfile: () -> Unit,
    onShowAllAchievements: () -> Unit,
    onSettings: () -> Unit
) {
    val xpPct = if (user.xpNext > 0) (user.xp.toFloat() / user.xpNext).coerceIn(0f, 1f) else 0f
    val achievements = ACHIEVEMENT_DEFS.map { it to it.condition(user) }
    val earnedCount = achievements.count { it.second }
    val days = listOf("L", "M", "X", "J", "V", "S", "D")
    val maxActivity = user.weeklyActivity.maxOrNull()?.takeIf { it > 0 } ?: 1

    LazyColumn(modifier = Modifier.fillMaxSize().background(Background)) {
        // Banner
        item {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp)
                    .background(Brush.linearGradient(listOf(Purple, PurpleLight, Indigo)))
            ) {
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.radialGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
                ))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mi Perfil", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    Text("⚙️", fontSize = 30.sp, modifier = Modifier.clickableNoRipple(onSettings))
                }
            }
        }

        // Avatar card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-30).dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(
                            modifier = Modifier.size(70.dp).clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Purple, PurpleLight))),
                            contentAlignment = Alignment.Center
                        ) {
                            if (user.avatar.startsWith("content://") || user.avatar.startsWith("http")) {
                                AsyncImage(
                                    model = user.avatar,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(user.avatar, fontSize = 30.sp)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(user.name, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Gray900)
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(PurpleBg).padding(horizontal = 10.dp, vertical = 3.dp)
                                ) { Text("Nivel ${user.level}", fontSize = 10.sp, color = Purple, fontWeight = FontWeight.Bold) }
                            }
                            Text("${user.role} · Miembro desde ${user.memberSince}", fontSize = 12.sp, color = Gray500)
                            Spacer(Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Experiencia (XP)", fontSize = 11.sp, color = Gray400)
                                Text("${user.xp}/${user.xpNext} XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Purple)
                            }
                            Spacer(Modifier.height(4.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(10.dp)).background(PurpleBg)) {
                                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(xpPct).clip(RoundedCornerShape(10.dp))
                                    .background(Brush.horizontalGradient(listOf(Purple, PurpleLight))))
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onEditProfile,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleBgLight)
                    ) {
                        Text("✏️ Editar Perfil", color = Purple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Stats
        item {
            Text("Estadísticas", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Gray900,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 10.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Triple("🔥", "${user.logins}", "Días seguidos"),
                    Triple("⚡", "${"%.1f".format(user.totalCalories / 1000f)}k", "Calorías totales"),
                    Triple("↔️", "${user.totalTrainings}", "Entrenamientos")
                ).forEach { (icon, value, label) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PurpleBg)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(icon, fontSize = 22.sp)
                            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Gray900)
                            Text(label, fontSize = 10.sp, color = Gray500, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        // Achievements
        item {
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Logros Recientes", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Gray900)
                Text("Ver todos", fontSize = 13.sp, color = Purple, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onShowAllAchievements() })
            }
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        achievements.take(4).forEach { (def, earned) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(70.dp)) {
                                Box(
                                    modifier = Modifier.size(56.dp).clip(CircleShape)
                                        .background(if (earned) parseColor(def.colorHex) else Color(0xFFF3F4F6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(if (earned) def.icon else "🔒", fontSize = 24.sp)
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(def.label, fontSize = 10.sp, color = Gray500, textAlign = TextAlign.Center, lineHeight = 12.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Progreso de logros", fontSize = 12.sp, color = Gray500)
                        Text("$earnedCount/${ACHIEVEMENT_DEFS.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gray900)
                    }
                    Spacer(Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(10.dp)).background(PurpleBg)) {
                        Box(modifier = Modifier.fillMaxHeight()
                            .fillMaxWidth(if (ACHIEVEMENT_DEFS.isNotEmpty()) earnedCount.toFloat() / ACHIEVEMENT_DEFS.size else 0f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.horizontalGradient(listOf(Purple, PurpleLight))))
                    }
                }
            }
        }

        // Weekly Activity
        item {
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Actividad Semanal", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Gray900)
                Text("Esta semana", fontSize = 12.sp, color = Gray400)
            }
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(90.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    user.weeklyActivity.forEachIndexed { i, v ->
                        val heightFraction = if (maxActivity > 0) (if (v == 0) 0.15f else (v.toFloat() / maxActivity).coerceIn(0.15f, 1f)) else 0.15f
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier.width(28.dp).fillMaxHeight(heightFraction)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (v > 0) Brush.verticalGradient(listOf(PurpleLight, Purple))
                                        else Brush.verticalGradient(listOf(PurpleBg, PurpleBg))
                                    )
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(days[i], fontSize = 11.sp, color = Gray400)
                        }
                    }
                }
            }
            Spacer(Modifier.height(100.dp))
        }
    }
}

fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource(),
        indication = null,
        onClick = onClick
    )
)