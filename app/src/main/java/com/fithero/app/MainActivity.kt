package com.fithero.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fithero.app.data.ShopItem
import com.fithero.app.ui.screens.*
import com.fithero.app.ui.theme.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val viewModel: AppViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AppViewModel(context) as T
                }
            })
            FitHeroApp(viewModel)
        }
    }
}

@Composable
fun FitHeroApp(viewModel: AppViewModel) {
    val user by viewModel.user.collectAsState()
    val missions by viewModel.missions.collectAsState()
    val toast by viewModel.toast.collectAsState()
    val loggedIn by viewModel.isLoggedIn.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    var currentTab by remember { mutableStateOf("home") }
    var selectedMission by remember { mutableStateOf<com.fithero.app.data.Mission?>(null) }
    var missionToEdit by remember { mutableStateOf<com.fithero.app.data.Mission?>(null) }
    var showEditProfile by remember { mutableStateOf(false) }
    var showAchievements by remember { mutableStateOf(false) }
    var showAddMission by remember { mutableStateOf(false) }
    var purchaseItem by remember { mutableStateOf<ShopItem?>(null) }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(DarkBg),
            contentAlignment = Alignment.Center
        ) {
            if (!loggedIn) {
                LoginScreen(
                    onLogin = { username, password, onSuccess, onError ->
                        viewModel.login(
                            username = username,
                            password = password,
                            onSuccess = { onSuccess() },
                            onError = { msg -> onError(msg) }
                        )
                    },
                    onRegister = { username, email, password, onSuccess, onError ->
                        viewModel.register(
                            username = username,
                            email = email,
                            password = password,
                            onSuccess = { onSuccess() },
                            onError = { msg -> onError(msg) }
                        )
                    }
                )
            } else {
                if (user == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Purple)
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize().background(Background)) {
                        Box(modifier = Modifier.weight(1f)) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(180)) togetherWith
                                    fadeOut(animationSpec = tween(180))
                                },
                                label = "tab",
                                modifier = Modifier.fillMaxSize()
                            ) { tab ->
                                when (tab) {
                                    "home" -> HomeScreen(
                                        user = user!!,
                                        missions = missions,
                                        onMissionClick = { selectedMission = it },
                                        onAddMission = { showAddMission = true },
                                        isRefreshing = isRefreshing,
                                        onRefresh = { viewModel.refreshData() }
                                    )
                                    "profile" -> ProfileScreen(
                                        user = user!!,
                                        missions = missions,
                                        onEditProfile = { showEditProfile = true },
                                        onShowAllAchievements = { showAchievements = true },
                                        onSettings = { currentTab = "settings" }
                                    )
                                    "shop" -> ShopScreen(
                                        user = user!!,
                                        onBuy = { purchaseItem = it }
                                    )
                                    "settings" -> SettingsScreen(
                                        viewModel = viewModel,
                                        user = user!!,
                                        onLogout = {}
                                    )
                                    else -> {}
                                }
                            }

                            var toastText by remember { mutableStateOf("") }
                            if (toast != null) toastText = toast!!
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                                    .padding(top = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ToastMessage(visible = toast != null, message = toastText)
                            }

                            // Sync selected mission when backend confirms updates
                            LaunchedEffect(missions) {
                                selectedMission = selectedMission?.let { sel ->
                                    missions.find { it.id == sel.id }
                                }
                            }

                            selectedMission?.let { mission ->
                                MissionModal(
                                    mission = mission,
                                    onClose = { selectedMission = null },
                                    onUpdate = { id, delta ->
                                        viewModel.updateMission(id, delta)
                                        // Don't touch selectedMission here —
                                        // LaunchedEffect(missions) updates it once backend confirms
                                    },
                                    onDelete = { id ->
                                        viewModel.deleteMission(id)
                                        selectedMission = null
                                    },
                                    onClaim = { id ->
                                        viewModel.claimReward(id)
                                        selectedMission = null
                                    },
                                    onEdit = { m ->
                                        selectedMission = null
                                        missionToEdit = m
                                    }
                                )
                            }

                            if (showAddMission) {
                                AddMissionModal(
                                    onClose = { showAddMission = false },
                                    onSave = { mission ->
                                        viewModel.crearMision(mission)
                                        showAddMission = false
                                    }
                                )
                            }

                            missionToEdit?.let { mission ->
                                EditMissionModal(
                                    mission = mission,
                                    onClose = { missionToEdit = null },
                                    onSave = { id, updated ->
                                        viewModel.editMission(id, updated)
                                        missionToEdit = null
                                    }
                                )
                            }

                            if (showEditProfile) {
                                EditProfileModal(
                                    user = user!!, // <-- Añade !! aquí
                                    onSave = { name, username, email, avatar ->
                                        viewModel.saveProfile(name, username, email, avatar)
                                        showEditProfile = false
                                    },
                                    onClose = { showEditProfile = false }
                                )
                            }

                            if (showAchievements) {
                                AchievementsModal(
                                    user = user!!, // <-- Añade !! aquí
                                    onClose = { showAchievements = false })
                            }

                            purchaseItem?.let { item ->
                                PurchaseModal(
                                    item = item,
                                    onConfirm = { viewModel.buyItem(item); purchaseItem = null },
                                    onClose = { purchaseItem = null }
                                )
                            }
                        }

                        BottomNavBar(currentTab = currentTab, onTabChange = { currentTab = it })
                    }
                }
            }
        }
    }
}
@Composable
fun ToastMessage(visible: Boolean, message: String) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(tween(200)),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(tween(150))
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1F2937))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(message, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun BottomNavBar(currentTab: String, onTabChange: (String) -> Unit) {
    val items = listOf(
        Triple("home", "📋", "Misiones"),
        Triple("profile", "👤", "Perfil"),
        Triple("shop", "🛍️", "Tienda"),
    )

    Surface(color = Color.White, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 10.dp)
        ) {
            items.forEach { (id, icon, label) ->
                val isActive = currentTab == id

                TextButton(
                    onClick = { onTabChange(id) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isActive) Purple else Color(0xFFA0A0B8)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(icon, fontSize = 22.sp)
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}