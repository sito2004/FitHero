package com.fithero.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.fithero.app.data.Mission
import com.fithero.app.data.User
import com.fithero.app.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: User,
    missions: List<Mission>,
    onMissionClick: (Mission) -> Unit,
    onAddMission: () -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val pullState = rememberPullToRefreshState()
    LaunchedEffect(pullState.isRefreshing) {
        if (pullState.isRefreshing) onRefresh()
    }
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) pullState.endRefresh()
    }

    val xpPct = if (user.xpNext > 0) (user.xp.toFloat() / user.xpNext).coerceIn(0f, 1f) else 0f
    val animatedXpPct by animateFloatAsState(targetValue = xpPct, animationSpec = tween(700), label = "xp")
    val completedCount = missions.count { it.status == "completed" }
    val allCompleted = missions.isNotEmpty() && missions.all { it.status == "completed" || it.claimed }

    val dayName = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Lunes"
        Calendar.TUESDAY -> "Martes"
        Calendar.WEDNESDAY -> "Miércoles"
        Calendar.THURSDAY -> "Jueves"
        Calendar.FRIDAY -> "Viernes"
        Calendar.SATURDAY -> "Sábado"
        Calendar.SUNDAY -> "Domingo"
        else -> "Hoy"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.nestedScroll(pullState.nestedScrollConnection).fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize().background(Background)) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Purple, PurpleDark)))
                        .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 28.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(50.dp).clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (user.avatar.startsWith("content://") || user.avatar.startsWith("http")) {
                                        AsyncImage(model = user.avatar, contentDescription = null,
                                            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    } else {
                                        Text(user.avatar, fontSize = 28.sp)
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Nivel de Jugador", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text(user.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text("Nivel ${user.level}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Experiencia (XP)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text("${user.xp}/${user.xpNext} XP", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { animatedXpPct },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(10.dp)),
                            color = Color(0xFFA78BFA),
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            StatItem("Misiones", completedCount.toString())
                            StatItem("Logins", user.logins.toString())
                            StatItem("Monedas", user.coins.toString())
                        }
                    }
                }
            }

            item {
                Text("Misiones de $dayName", modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Gray900)
            }

            items(missions) { mission ->
                MissionCard(mission = mission, onClick = { onMissionClick(mission) })
            }

            if (allCompleted) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🏆", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "¡Todas las misiones completadas!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF065F46)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "¡Increíble trabajo hoy! Vuelve mañana para nuevas misiones.",
                                fontSize = 13.sp,
                                color = Color(0xFF059669),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }

        PullToRefreshContainer(
            state = pullState,
            modifier = Modifier.align(Alignment.TopCenter),
            containerColor = Purple,
            contentColor = Color.White
        )
        } // inner Box (nestedScroll)

        // FAB abajo a la derecha
        FloatingActionButton(
            onClick = onAddMission,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 90.dp),
            containerColor = Purple,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir misión", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
    }
}

fun tiempoRestante(mission: Mission): String {
    return try {
        if (mission.predefined) {
            // Misión del sistema → cuenta hasta medianoche
            val now = LocalDateTime.now()
            val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
            val hours = ChronoUnit.HOURS.between(now, midnight)
            val minutes = ChronoUnit.MINUTES.between(now, midnight) % 60
            if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        } else {
            // Misión personalizada → usa deadline
            val deadline = mission.deadline ?: return ""
            val deadlineDate = LocalDate.parse(deadline, DateTimeFormatter.ISO_DATE)
            val now = LocalDate.now()
            val days = ChronoUnit.DAYS.between(now, deadlineDate)
            when {
                days < 0 -> "Expirada"
                days == 0L -> "Hoy"
                days == 1L -> "Mañana"
                else -> "${days}d"
            }
        }
    } catch (e: Exception) { "" }
}

@Composable
fun MissionCard(mission: Mission, onClick: () -> Unit) {
    val pct = if (mission.goal > 0) (mission.progress.toFloat() / mission.goal).coerceIn(0f, 1f) else 0f
    val animatedPct by animateFloatAsState(targetValue = pct, animationSpec = tween(500), label = "cardProgress")
    val color = parseColor(mission.colorHex)
    val tiempo = tiempoRestante(mission)
    val isExpired = tiempo == "Expirada"

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(mission.icon, fontSize = 24.sp, modifier = Modifier.padding(end = 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(mission.title, fontWeight = FontWeight.Bold, color = Gray900,
                        modifier = Modifier.weight(1f))
                    if (tiempo.isNotEmpty() && mission.status != "completed") {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isExpired) Color(0xFFFEF2F2) else Color(0xFFF3F4F6))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "⏱ $tiempo",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpired) Red else Gray500
                            )
                        }
                    }
                }
                Text(mission.desc, fontSize = 12.sp, color = Gray500)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedPct },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = color,
                    trackColor = PurpleBg
                )
            }
            Text("+${mission.xpReward} XP", modifier = Modifier.padding(start = 8.dp),
                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}