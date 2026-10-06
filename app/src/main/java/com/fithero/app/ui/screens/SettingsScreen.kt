package com.fithero.app.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import com.fithero.app.AppViewModel
import com.fithero.app.data.HealthConnectManager
import com.fithero.app.data.User
import com.fithero.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: AppViewModel, user: User, onLogout: () -> Unit) {
    val context = LocalContext.current
    val healthManager = remember { HealthConnectManager(context) }
    val scope = rememberCoroutineScope()

    var healthAvailable by remember { mutableStateOf(healthManager.isAvailable()) }
    var hasPermissions by remember { mutableStateOf(false) }
    var syncing by remember { mutableStateOf(false) }
    var syncMessage by remember { mutableStateOf("") }

    // Cambiar contraseña
    var showChangePassword by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMessage by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(healthManager.permissions)) {
            hasPermissions = true
            syncMessage = "✅ Permisos concedidos"
        } else {
            hasPermissions = false
            syncMessage = "❌ Permisos denegados"
        }
    }

    // Comprobación inicial
    LaunchedEffect(Unit) {
        if (healthAvailable) hasPermissions = healthManager.hasPermissions()
    }

    // Re-comprueba los permisos cada vez que el usuario vuelve a esta pantalla
    // (por ejemplo, tras concederlos en los ajustes del sistema o en Health Connect)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && healthAvailable) {
                scope.launch { hasPermissions = healthManager.hasPermissions() }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Ajustes", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
            color = Gray900, modifier = Modifier.padding(bottom = 20.dp))

        // ── Datos personales ──────────────────────────────────────────
        SectionTitle("👤 Datos personales")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DataRow(Icons.Default.Person, "Usuario", "@${user.username}")
                DataRow(Icons.Default.Badge, "Nombre", user.name.ifEmpty { "Sin nombre" })
                DataRow(Icons.Default.Email, "Email", user.email)
                DataRow(Icons.Default.Star, "Nivel", "${user.level}")
                DataRow(Icons.Default.EmojiEvents, "XP Total", "${user.xp} / ${user.xpNext}")
                DataRow(Icons.Default.MonetizationOn, "Monedas", "${user.coins} 🪙")
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Cambiar contraseña ────────────────────────────────────────
        SectionTitle("🔒 Seguridad")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { showChangePassword = !showChangePassword },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleBg, contentColor = Purple)
                ) {
                    Text("🔑 Cambiar contraseña", fontWeight = FontWeight.Bold)
                }

                if (showChangePassword) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPassword, onValueChange = { newPassword = it },
                        label = { Text("Nueva contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple)
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple)
                    )
                    if (passwordMessage.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(passwordMessage, fontSize = 13.sp,
                            color = if (passwordMessage.startsWith("✅")) Color(0xFF059669) else Red)
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (newPassword.isBlank() || confirmPassword.isBlank()) {
                                passwordMessage = "❌ Rellena todos los campos"
                            } else if (newPassword != confirmPassword) {
                                passwordMessage = "❌ Las contraseñas no coinciden"
                            } else if (newPassword.length < 4) {
                                passwordMessage = "❌ Mínimo 4 caracteres"
                            } else {
                                scope.launch {
                                    viewModel.cambiarPassword(newPassword) { ok ->
                                        passwordMessage = if (ok) "✅ Contraseña actualizada"
                                        else "❌ Error al actualizar"
                                        if (ok) {
                                            newPassword = ""
                                            confirmPassword = ""
                                            showChangePassword = false
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) {
                        Text("Guardar contraseña", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Health Connect ────────────────────────────────────────────
        SectionTitle("🏃 Health Connect")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!healthAvailable) {
                    Text("❌ Health Connect no disponible en este dispositivo",
                        fontSize = 13.sp, color = Red)
                } else if (!hasPermissions) {
                    Button(
                        onClick = { permissionLauncher.launch(healthManager.permissions) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) {
                        Text("Conceder permisos", fontWeight = FontWeight.Bold)
                    }
                    if (syncMessage.startsWith("❌")) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Si el diálogo no aparece, los permisos fueron denegados previamente. Ábrelos manualmente:",
                            fontSize = 12.sp, color = Gray500
                        )
                        Spacer(Modifier.height(6.dp))
                        Button(
                            onClick = {
                                context.startActivity(
                                    Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Gray100, contentColor = Purple)
                        ) {
                            Text("⚙️ Abrir ajustes de Health Connect", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                } else {
                    // --- BOTÓN DE SINCRONIZACIÓN (Este estaba perfecto) ---
                    Button(
                        onClick = {
                            scope.launch {
                                syncing = true
                                syncMessage = ""
                                val calorias = healthManager.readCaloriasHoy()
                                if (calorias > 0) {
                                    viewModel.sincronizarCalorias(calorias.toInt())
                                    syncMessage = "✅ ${calorias.toInt()} kcal sincronizadas"
                                } else {
                                    syncMessage = "ℹ️ No hay datos de calorías hoy"
                                }
                                syncing = false
                            }
                        },
                        enabled = !syncing,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) {
                        if (syncing) CircularProgressIndicator(color = Color.White,
                            modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("🔄 Sincronizar calorías de hoy", fontWeight = FontWeight.Bold)
                    }
                }

                if (syncMessage.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(syncMessage, fontSize = 13.sp, color = Gray700)
                }
            }
        } // <-- Esta es la llave que te faltaba antes!

        Spacer(Modifier.height(16.dp))

        // ── Cerrar sesión ─────────────────────────────────────────────
        Button(
            onClick = { viewModel.logout(); onLogout() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2), contentColor = Red)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Cerrar Sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
        color = Gray500, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
fun DataRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Purple,
            modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Gray400)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Gray900)
        }
    }
}