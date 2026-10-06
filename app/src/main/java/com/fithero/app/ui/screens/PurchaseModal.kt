package com.fithero.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fithero.app.data.ShopItem
import com.fithero.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PurchaseModal(item: ShopItem, onConfirm: () -> Unit, onClose: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    var dismissing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { visible = true }
    fun dismiss() {
        if (!dismissing) {
            dismissing = true
            visible = false
            scope.launch { delay(260); onClose() }
        }
    }

    // Fondo: fade in/out
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)),
        exit  = fadeOut(tween(260))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { dismiss() },
            contentAlignment = Alignment.Center
        ) {
            // Tarjeta: escala + fade
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(tween(260), initialScale = 0.85f) + fadeIn(tween(220)),
                exit  = scaleOut(tween(220), targetScale = 0.85f) + fadeOut(tween(200))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(item.emoji, fontSize = 60.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                        Text(item.brand, fontSize = 13.sp, color = Gray400, modifier = Modifier.padding(bottom = 16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFFFF7ED))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🪙 ${item.price} Monedas", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Amber)
                        }
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple)
                        ) {
                            Text("Confirmar Compra", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = { dismiss() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Gray100, contentColor = Gray500)
                        ) {
                            Text("Cancelar", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
