package com.fithero.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fithero.app.data.SHOP_ITEMS
import com.fithero.app.data.ShopItem
import com.fithero.app.data.User
import com.fithero.app.ui.theme.*

@Composable
fun ShopScreen(user: User, onBuy: (ShopItem) -> Unit) {
    var selectedTab by remember { mutableStateOf("Potenciadores") }
    val tabs = listOf("Potenciadores", "Consumibles", "Especiales")

    val bannerData = mapOf(
        "Potenciadores" to Triple("⚡", "¡Multiplica tu progreso!", "Activa bonificaciones para tus misiones de hoy."),
        "Consumibles"   to Triple("🥤", "¡Alimenta tu rendimiento!", "Consumibles que potencian categorías específicas."),
        "Especiales"    to Triple("👑", "¡Items exclusivos!", "Objetos únicos para personalizar tu héroe.")
    )

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Surface(shadowElevation = 2.dp, color = Color.White) {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tienda Héroe", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Gray900)
                        Text("${SHOP_ITEMS.size} items disponibles", fontSize = 12.sp, color = Gray400)
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Amber).padding(horizontal = 14.dp, vertical = 6.dp)) {
                        Text("🪙 ${user.coins}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tabs.forEach { tab ->
                        val count = SHOP_ITEMS.count { it.category == tab }
                        Button(
                            onClick = { selectedTab = tab },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == tab) Purple else Gray100,
                                contentColor = if (selectedTab == tab) Color.White else Gray500
                            )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(tab, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("$count items", fontSize = 9.sp, color = if (selectedTab == tab) Color.White.copy(alpha = 0.7f) else Gray400)
                            }
                        }
                    }
                }
            }
        }

        val filtered = SHOP_ITEMS.filter { it.category == selectedTab }
        val banner = bannerData[selectedTab] ?: Triple("⚡", "", "")

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(PurpleLight, Indigo)))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(banner.first, fontSize = 32.sp)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(banner.second, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(banner.third, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Items ya comprados
            val owned = filtered.filter { user.inventory.contains(it.id) }
            val available = filtered.filter { !user.inventory.contains(it.id) }

            if (available.isNotEmpty()) {
                item {
                    Text("Disponibles", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = Gray500, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                }
                items(available) { item ->
                    ShopItemRow(item = item, user = user, owned = false, onBuy = onBuy)
                }
            }

            if (owned.isNotEmpty()) {
                item {
                    Text("Ya tienes", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = Gray500, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                }
                items(owned) { item ->
                    ShopItemRow(item = item, user = user, owned = true, onBuy = {})
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun ShopItemRow(item: ShopItem, user: User, owned: Boolean, onBuy: (ShopItem) -> Unit) {
    val canAfford = user.coins >= item.price
    val tagColor = when (item.tag) {
        "HOT" -> Red
        "NUEVO" -> Green
        "SALE" -> Color(0xFFEF4444)
        "TOP" -> Purple
        "PREMIUM" -> Color(0xFFD97706)
        "ÉPICO" -> Color(0xFF7C3AED)
        else -> Purple
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (owned) Color(0xFFF9FAFB) else Color.White),
        elevation = CardDefaults.cardElevation(if (owned) 0.dp else 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp))
                    .background(if (owned) Gray200 else PurpleBg),
                contentAlignment = Alignment.Center
            ) {
                Text(item.emoji, fontSize = 28.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.name, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
                        color = if (owned) Gray400 else Gray900)
                    if (item.tag != null && !owned) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                .background(tagColor.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(item.tag, color = tagColor, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
                Text(item.description, fontSize = 11.sp, color = Gray500, lineHeight = 15.sp)
                Spacer(Modifier.height(6.dp))
                if (!owned) {
                    Text("🪙 ${item.price}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Amber)
                } else {
                    Text("✅ En tu inventario", fontSize = 11.sp, color = Green, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.width(10.dp))

            if (!owned) {
                Button(
                    onClick = { onBuy(item) },
                    enabled = canAfford,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple,
                        disabledContainerColor = Gray200
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        if (canAfford) "Comprar" else "Faltan\n🪙${item.price - user.coins}",
                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = if (canAfford) Color.White else Gray500
                    )
                }
            }
        }
    }
}