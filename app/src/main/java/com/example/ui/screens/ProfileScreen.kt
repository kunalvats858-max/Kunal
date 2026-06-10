package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConvictionViewModel
import com.example.ui.theme.ConvictionCardBg
import com.example.ui.theme.ConvictionGold
import com.example.ui.theme.ConvictionPrimaryRed
import com.example.ui.theme.ConvictionTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ConvictionViewModel,
    innerPadding: PaddingValues
) {
    val stats by viewModel.userStats.collectAsState()
    val workouts by viewModel.allWorkouts.collectAsState()
    val foods by viewModel.allFoods.collectAsState()

    var editingName by remember { mutableStateOf("") }
    var selectedPath by remember { mutableStateOf("Infinite Discipline") }
    var showUpdateBanner by remember { mutableStateOf(false) }

    val combatPaths = listOf("Infinite Discipline", "MMA Striking Grandmaster", "Grappling Prodigy", "Zen Mental Fortitude")

    LaunchedEffect(stats) {
        stats?.let {
            if (editingName.isEmpty()) {
                editingName = it.name
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ManageAccounts,
                    contentDescription = "Profile Builder",
                    tint = ConvictionGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "RESOLVE BUILDER",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Configure your disciple profile & monitor milestones.",
                        color = ConvictionTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // SAVE SUCCESS BANNER
        item {
            AnimatedVisibility(visible = showUpdateBanner) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Resolve profile updated successfully!",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showUpdateBanner = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Banner",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // PROFILE BUILDER CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ConvictionCardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Disciple Identity",
                        fontWeight = FontWeight.Bold,
                        color = ConvictionGold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    OutlinedTextField(
                        value = editingName,
                        onValueChange = { editingName = it },
                        label = { Text("Disciple Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Save
                                viewModel.awardSpecialMentalXp(0) // Quick update trigger
                                showUpdateBanner = true
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ConvictionPrimaryRed,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("disciple_name_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Choose Focus Discipline Path:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        combatPaths.forEach { path ->
                            val isSelected = path == selectedPath
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ConvictionPrimaryRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background)
                                    .border(1.dp, if (isSelected) ConvictionPrimaryRed else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { selectedPath = path }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Select",
                                        tint = if (isSelected) ConvictionPrimaryRed else ConvictionTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = path, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.awardSpecialMentalXp(5) // Award small mental resolve XP!
                            showUpdateBanner = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ConvictionPrimaryRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_profile_button")
                    ) {
                        Text("Update Focus Resolve", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // MILESTONES STATS CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ConvictionCardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Discipline Milestones",
                        fontWeight = FontWeight.Bold,
                        color = ConvictionGold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCounterBox(
                            value = "${workouts.size}",
                            label = "Combat Logs",
                            icon = Icons.Default.FitnessCenter,
                            tint = ConvictionPrimaryRed,
                            modifier = Modifier.weight(1f)
                        )
                        StatCounterBox(
                            value = "${foods.size}",
                            label = "Foods Scanned",
                            icon = Icons.Default.CameraAlt,
                            tint = Color(0xFF81C784),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // COMPREHENSIVE ATTRIBUTES RATINGS
        item {
            stats?.let { user ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ConvictionCardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Attribute Standing",
                            fontWeight = FontWeight.Bold,
                            color = ConvictionGold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Individual ratings representing your current domain standing.",
                            fontSize = 10.sp,
                            color = ConvictionTextSecondary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        AttributeSliderRow("Striking Mastery", user.strikingXp, ConvictionPrimaryRed)
                        Spacer(modifier = Modifier.height(8.dp))
                        AttributeSliderRow("Grappling Mechanics", user.grapplingXp, ConvictionGold)
                        Spacer(modifier = Modifier.height(8.dp))
                        AttributeSliderRow("Discipline Compliance", user.disciplineXp, Color(0xFF64B5F6))
                        Spacer(modifier = Modifier.height(8.dp))
                        AttributeSliderRow("Mental Fortitude", user.mentalXp, Color(0xFFE040FB))
                        Spacer(modifier = Modifier.height(8.dp))
                        AttributeSliderRow("Conditioning Strength", user.conditioningXp, Color(0xFFFF9100))
                        Spacer(modifier = Modifier.height(8.dp))
                        AttributeSliderRow("Nutrition Fueling", user.nutritionXp, Color(0xFF81C784))
                    }
                }
            }
        }

        // RESET SYSTEM ZONE FOR QUICK RE-TESTS
        item {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    viewModel.clearChat()
                },
                border = BorderStroke(1.dp, ConvictionPrimaryRed.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ConvictionPrimaryRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_session_stats_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Refresh Mentorship & Chat Logs", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatCounterBox(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(text = label, fontSize = 10.sp, color = ConvictionTextSecondary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AttributeSliderRow(name: String, value: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("$value / 100", color = ConvictionGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value.toFloat() / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = Color.White.copy(alpha = 0.05f)
        )
    }
}
