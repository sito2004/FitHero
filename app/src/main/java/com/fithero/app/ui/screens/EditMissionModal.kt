package com.fithero.app.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fithero.app.data.BackendMission
import com.fithero.app.data.Mission
import com.fithero.app.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMissionModal(
    mission: Mission,
    onClose: () -> Unit,
    onSave: (Int, BackendMission) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(mission.title) }
    var goal by remember { mutableStateOf(mission.goal.toString()) }
    var unit by remember { mutableStateOf(mission.unit) }
    var selectedCategory by remember { mutableStateOf(mission.category) }
    var selectedIcon by remember { mutableStateOf(mission.icon) }
    var deadline by remember { mutableStateOf(mission.deadline) }
    var error by remember { mutableStateOf("") }

    val categorias = listOf(
        Triple("fuerza", "💪", "#7C3AED"),
        Triple("cardio", "🏃", "#F59E0B"),
        Triple("salud", "💧", "#10B981"),
        Triple("habito", "⏰", "#3B82F6"),
        Triple("mente", "🧘", "#06B6D4")
    )

    val cal = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            deadline = LocalDate.of(year, month + 1, day).format(DateTimeFormatter.ISO_DATE)
        },
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
    ).apply { datePicker.minDate = System.currentTimeMillis() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.width(40.dp).height(4.dp)
                        .clip(RoundedCornerShape(4.dp)).background(Gray200)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 36.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(selectedIcon, fontSize = 24.sp)
                Text("Editar Misión", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Gray900)
            }
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Nombre", color = Gray400) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple, unfocusedBorderColor = Gray200),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = goal, onValueChange = { goal = it },
                    label = { Text("Objetivo", color = Gray400) },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple, unfocusedBorderColor = Gray200),
                    singleLine = true
                )
                OutlinedTextField(
                    value = unit, onValueChange = { unit = it },
                    label = { Text("Unidad", color = Gray400) },
                    modifier = Modifier.weight(1.5f), shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple, unfocusedBorderColor = Gray200),
                    singleLine = true
                )
            }
            Spacer(Modifier.height(12.dp))

            Text("Fecha límite (opcional)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Gray500)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { datePickerDialog.show() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleBg, contentColor = Purple)
                ) {
                    Text("📅 ${deadline ?: "Sin fecha"}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                if (deadline != null) {
                    TextButton(onClick = { deadline = null }) {
                        Text("✕ Quitar", color = Red, fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            Text("Categoría", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Gray500)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categorias.forEach { (cat, icon, color) ->
                    val selected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) parseColor(color).copy(alpha = 0.15f) else Gray100)
                            .clickable { selectedCategory = cat; selectedIcon = icon }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(icon, fontSize = 18.sp)
                            Text(
                                cat, fontSize = 9.sp,
                                color = if (selected) parseColor(color) else Gray400,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(error, color = Red, fontSize = 12.sp)
            }
            Spacer(Modifier.height(16.dp))

            val goalInt = goal.toIntOrNull() ?: mission.goal
            val xpEstimado = if (mission.predefined) mission.xpReward
                             else (goalInt * 50).coerceIn(100, 1000)
            val coinsEstimados = if (mission.predefined) mission.coinReward else xpEstimado / 10

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Recompensa XP", fontSize = 11.sp, color = Gray400)
                        Text("+$xpEstimado ⚡", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Amber)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Monedas", fontSize = 11.sp, color = Gray400)
                        Text("+$coinsEstimados 🪙", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Amber)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isBlank()) { error = "El nombre es obligatorio"; return@Button }
                    if (goal.isBlank() || goal.toIntOrNull() == null || goal.toInt() <= 0) {
                        error = "El objetivo debe ser un número mayor que 0"; return@Button
                    }
                    if (unit.isBlank()) { error = "La unidad es obligatoria"; return@Button }
                    val colorHex = categorias.find { it.first == selectedCategory }?.third ?: mission.colorHex
                    onSave(
                        mission.id,
                        BackendMission(
                            id = mission.id.toLong(),
                            title = title,
                            description = mission.desc,
                            icon = selectedIcon,
                            colorHex = colorHex,
                            status = mission.status,
                            progress = mission.progress,
                            goal = goal.toInt(),
                            unit = unit,
                            xpReward = xpEstimado,
                            coinReward = coinsEstimados,
                            category = selectedCategory,
                            deadline = deadline,
                            predefined = mission.predefined,
                            claimed = mission.claimed
                        )
                    )
                    onClose()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple)
            ) {
                Text("Actualizar Misión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
