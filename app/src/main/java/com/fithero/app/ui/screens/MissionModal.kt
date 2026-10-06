package com.fithero.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fithero.app.data.Mission
import com.fithero.app.ui.theme.*

@Composable
fun MissionModal(
    mission: Mission,
    onClose: () -> Unit,
    onUpdate: (Int, Int) -> Unit,
    onDelete: (Int) -> Unit,
    onClaim: (Int) -> Unit = {},
    onEdit: (Mission) -> Unit = {}
) {
    var localProgress by remember { mutableStateOf(mission.progress) }
    var localStatus by remember { mutableStateOf(mission.status) }
    var localClaimed by remember { mutableStateOf(mission.claimed) }
    var showCannotDeleteDialog by remember { mutableStateOf(false) }

    val goal = mission.goal.takeIf { it > 0 } ?: 1
    val pct = (localProgress.toFloat() / goal).coerceIn(0f, 1f)
    val animatedPct by animateFloatAsState(targetValue = pct, animationSpec = tween(600), label = "progress")
    val color = parseColor(mission.colorHex)
    // isCompleted is only true once the BACKEND confirms status="completed".
    // localStatus=="completed" is set optimistically on the last click and
    // shows a loading spinner until the server acknowledges.
    val isCompleted = mission.status == "completed"

    var visible by remember { mutableStateOf(false) }
    var dismissing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { visible = true }
    fun dismiss() {
        if (!dismissing) {
            dismissing = true
            visible = false
            scope.launch { delay(320); onClose() }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(tween(340)) { it } + fadeIn(tween(200)),
        exit  = slideOutVertically(tween(300)) { it } + fadeOut(tween(300))
    ) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { dismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable(enabled = false) {},
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 36.dp)) {
                Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(4.dp))
                    .background(Gray200).align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(RoundedCornerShape(16.dp))
                            .background(color.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) { Text(mission.icon, fontSize = 26.sp) }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(mission.title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Gray900)
                        Text(mission.desc, fontSize = 13.sp, color = Gray500)
                    }
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp))
                            .background(
                                when {
                                    localClaimed -> Color(0xFFE0F2FE)
                                    isCompleted || localStatus == "completed" -> Color(0xFFD1FAE5)
                                    else -> PurpleBg
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            when {
                                localClaimed -> "✓ Recogida"
                                isCompleted || localStatus == "completed" -> "✓ Completada"
                                else -> "Activa"
                            },
                            fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = when {
                                localClaimed -> Color(0xFF0284C7)
                                isCompleted || localStatus == "completed" -> Color(0xFF059669)
                                else -> Purple
                            }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Progreso
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Progreso", fontSize = 13.sp, color = Gray500, fontWeight = FontWeight.SemiBold)
                            Text("$localProgress/${mission.goal} ${mission.unit}",
                                fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
                        }
                        Spacer(Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(10.dp)).background(PurpleBg)) {
                            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(animatedPct)
                                .clip(RoundedCornerShape(10.dp)).background(color))
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${(pct * 100).toInt()}% completado", fontSize = 12.sp, color = Gray400)
                            if (!isCompleted && localProgress < mission.goal)
                                Text("Faltan ${(mission.goal - localProgress).coerceAtLeast(0)} ${mission.unit}",
                                    fontSize = 12.sp, color = Gray400)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Recompensas
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Recompensa XP" to "+${mission.xpReward} ⚡", "Monedas" to "+${mission.coinReward} 🪙").forEach { (label, value) ->
                        Card(
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(label, fontSize = 11.sp, color = Gray400)
                                Text(value, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Amber)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                when {
                    // Completada localmente pero el backend aún no ha confirmado → spinner
                    localStatus == "completed" && !isCompleted && !localClaimed -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Purple,
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 3.dp
                            )
                        }
                    }

                    // Misión completada y recompensa ya recogida
                    localClaimed -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFE0F2FE)).padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✅ Recompensa ya recogida",
                                color = Color(0xFF0284C7), fontWeight = FontWeight.Bold,
                                fontSize = 14.sp, textAlign = TextAlign.Center)
                        }
                    }

                    // Misión completada, recompensa pendiente
                    isCompleted && !localClaimed -> {
                        Button(
                            onClick = {
                                localClaimed = true
                                onClaim(mission.id)
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                        ) {
                            Text("🎁 ¡Recoger recompensa! +${mission.xpReward}XP +${mission.coinReward}🪙",
                                fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
                                color = Color.White, textAlign = TextAlign.Center)
                        }
                    }

                    // Misión activa — botones de progreso
                    else -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    if (localProgress > 0) { localProgress -= 1; onUpdate(mission.id, -1) }
                                },
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Gray100, contentColor = Gray700),
                                enabled = localProgress > 0
                            ) { Text("−", fontSize = 22.sp, fontWeight = FontWeight.Bold) }

                            Button(
                                onClick = {
                                    if (localProgress < mission.goal) {
                                        localProgress += 1
                                        if (localProgress >= mission.goal) localStatus = "completed"
                                        onUpdate(mission.id, 1)
                                    }
                                },
                                modifier = Modifier.weight(2f).height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = color),
                                enabled = localProgress < mission.goal
                            ) {
                                Text(
                                    if (localProgress >= mission.goal - 1) "✓ Completar" else "+ Registrar progreso",
                                    fontWeight = FontWeight.Bold, fontSize = 15.sp
                                )
                            }
                        }
                        if (localProgress > 0 && localProgress < mission.goal) {
                            Spacer(Modifier.height(10.dp))
                            Text("¡Sigue así! Llevas $localProgress de ${mission.goal} ${mission.unit} 💪",
                                fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onEdit(mission) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF0FDF4),
                        contentColor = Color(0xFF059669)
                    )
                ) { Text("✏️ Editar misión", fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (mission.predefined) showCannotDeleteDialog = true
                        else onDelete(mission.id)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2), contentColor = Red)
                ) { Text("🗑️ Eliminar misión", fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
            }
        }
    }
    } // AnimatedVisibility

    if (showCannotDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showCannotDeleteDialog = false },
            title = {
                Text(
                    "❌ No se puede eliminar",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = Color(0xFF111827)
                )
            },
            text = {
                Text(
                    "Las misiones del sistema no se pueden eliminar. Solo puedes eliminar misiones personalizadas que hayas creado tú.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showCannotDeleteDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}