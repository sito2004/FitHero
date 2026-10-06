package com.fithero.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fithero.app.ui.theme.*

@Composable
fun LoginScreen(
    onLogin: (String, String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    onRegister: (String, String, String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit = { _, _, _, _, _ -> }
) {
    var isLoginMode by remember { mutableStateOf(true) }

    // Login fields
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }


    // Register fields
    var regUsername by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regShowPass by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors = listOf(Color(0xFF1a1035), Color(0xFF2d1b69), Color(0xFF0f2460))))
            .padding(28.dp)
    ) {
        repeat(4) { i ->
            Box(
                modifier = Modifier.size((180 + i * 80).dp).align(Alignment.TopEnd)
                    .offset(x = (60 + i * 30).dp, y = (-40 - i * 30).dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.04f))
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(20.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFFA855F7), Purple))),
                    contentAlignment = Alignment.Center
                ) { Text("💪", fontSize = 30.sp) }
                Spacer(Modifier.height(16.dp))
                Text(
                    if (isLoginMode) "Bienvenido" else "Crear Cuenta",
                    color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    if (isLoginMode) "Ingresa tus credenciales para continuar" else "Únete a FitHero",
                    color = Color.White.copy(alpha = 0.55f), fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(20.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    if (isLoginMode) {
                        // ── LOGIN ──────────────────────────────────────────
                        OutlinedTextField(
                            value = username, onValueChange = { username = it },
                            placeholder = { Text("Usuario", color = Gray400) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Gray200, focusedBorderColor = Purple,
                                unfocusedContainerColor = Color(0xFFF9FAFB), focusedContainerColor = Color(0xFFF9FAFB)
                            ), singleLine = true
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = password, onValueChange = { password = it },
                            placeholder = { Text("Contraseña", color = Gray400) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { showPass = !showPass }) {
                                    Icon(if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Gray400)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Gray200, focusedBorderColor = Purple,
                                unfocusedContainerColor = Color(0xFFF9FAFB), focusedContainerColor = Color(0xFFF9FAFB)
                            ), singleLine = true
                        )

                        if (error.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text(error, color = Red, fontSize = 13.sp)
                        }

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (username.isBlank() || password.isBlank()) {
                                    error = "Por favor rellena todos los campos"
                                } else {
                                    error = ""
                                    loading = true
                                    onLogin(username, password,{
                                        loading = false},
                                        {msg -> loading = false
                                                         error = msg
                                    })
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple),
                            enabled = !loading
                        ) {
                            if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            else Text("Iniciar Sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                    } else {
                        // ── REGISTRO ───────────────────────────────────────
                        OutlinedTextField(
                            value = regUsername, onValueChange = { regUsername = it },
                            placeholder = { Text("Nombre de usuario", color = Gray400) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Gray200, focusedBorderColor = Purple,
                                unfocusedContainerColor = Color(0xFFF9FAFB), focusedContainerColor = Color(0xFFF9FAFB)
                            ), singleLine = true
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = regEmail, onValueChange = { regEmail = it },
                            placeholder = { Text("Correo electrónico", color = Gray400) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Gray200, focusedBorderColor = Purple,
                                unfocusedContainerColor = Color(0xFFF9FAFB), focusedContainerColor = Color(0xFFF9FAFB)
                            ), singleLine = true
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = regPassword, onValueChange = { regPassword = it },
                            placeholder = { Text("Contraseña", color = Gray400) },
                            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                            visualTransformation = if (regShowPass) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { regShowPass = !regShowPass }) {
                                    Icon(if (regShowPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = Gray400)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Gray200, focusedBorderColor = Purple,
                                unfocusedContainerColor = Color(0xFFF9FAFB), focusedContainerColor = Color(0xFFF9FAFB)
                            ), singleLine = true
                        )

                        if (error.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text(error, color = Red, fontSize = 13.sp)
                        }

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (regUsername.isBlank() || regEmail.isBlank() || regPassword.isBlank()) {
                                    error = "Por favor rellena todos los campos"
                                } else {
                                    error = ""
                                    loading = true
                                    onRegister(regUsername, regEmail, regPassword,
                                        { loading = false; isLoginMode = true; error = "✅ Cuenta creada." },
                                        { msg -> loading = false; error = msg }
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple),
                            enabled = !loading
                        ) {
                            if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            else Text("Crear Cuenta", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    } // <--- ESTE ES EL CIERRE DEL 'else' DEL REGISTRO

                    Spacer(Modifier.height(16.dp))

                    // AHORA EL ROW ESTÁ FUERA, SERÁ VISIBLE TANTO EN LOGIN COMO EN REGISTRO
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            if (isLoginMode) "¿No tienes cuenta? " else "¿Ya tienes cuenta? ",
                            color = Gray500, fontSize = 14.sp
                        )
                        Text(
                            if (isLoginMode) "Regístrate" else "Inicia sesión",
                            color = Purple, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                isLoginMode = !isLoginMode
                                error = ""
                                loading = false
                            }
                        )
                    }
                } // Cierre del Column de la Card
            } // Cierre de la Card
        } // Cierre del Column principal
    } // Cierre del Box principal
}