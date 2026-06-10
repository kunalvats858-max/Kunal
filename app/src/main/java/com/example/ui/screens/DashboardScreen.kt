package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.ui.ConvictionViewModel
import com.example.ui.components.AttributeRadarChart
import com.example.ui.theme.ConvictionCardBg
import com.example.ui.theme.ConvictionGold
import com.example.ui.theme.ConvictionPrimaryRed
import com.example.ui.theme.ConvictionTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ConvictionViewModel,
    innerPadding: PaddingValues
) {
    val stats by viewModel.userStats.collectAsState()
    val habits by viewModel.allHabits.collectAsState()

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }
    var newHabitCategory by remember { mutableStateOf("Discipline") }

    // Curated quotes for discipline and mental toughness
    val quotes = remember {
        listOf(
            "Do nothing which is of no use. — Miyamoto Musashi",
            "Discipline is choosing between what you want now and what you want most. — Abraham Lincoln",
            "The successful warrior is the average man, with laser-like focus. — Bruce Lee",
            "You have power over your mind - not outside events. Realize this, and you will find strength. — Marcus Aurelius",
            "The more you sweat in training, the less you bleed in combat. — Navy SEAL Saying",
            "A disciplined mind leads to happiness, and an undisciplined mind leads to suffering. — The Buddha"
        )
    }
    val randomizedQuote = remember { quotes.random() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title & Welcome
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CONVICTION",
                        color = ConvictionPrimaryRed,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Build discipline, master physical arts.",
                        color = ConvictionTextSecondary,
                        fontSize = 11.sp
                    )
                }
                
                // XP Level Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(ConvictionPrimaryRed, ConvictionGold)
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Gold Medal",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LEVEL ${stats?.level ?: 1}",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // LEVEL PROGRESS BAR CARD
        item {
            stats?.let { user ->
                val xpInCurrentLevel = user.xp % 150
                val progressFraction = xpInCurrentLevel.toFloat() / 150f
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = ConvictionCardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total Progress",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                "${user.xp} XP Cumulative",
                                color = ConvictionGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = ConvictionPrimaryRed,
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "$xpInCurrentLevel / 150 XP to Next Level",
                                color = ConvictionTextSecondary,
                                fontSize = 11.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "XP Icon",
                                tint = ConvictionGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // ATTRIBUTE RADAR CHART CARD
        item {
            stats?.let { user ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ConvictionCardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Growth Segments Polygon",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ConvictionGold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            "Your discipline footprint spanning martial skill and resolve.",
                            fontSize = 11.sp,
                            color = ConvictionTextSecondary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        
                        // Custom Radar Chart Drawing
                        AttributeRadarChart(
                            striking = user.strikingXp,
                            grappling = user.grapplingXp,
                            discipline = user.disciplineXp,
                            mental = user.mentalXp,
                            conditioning = user.conditioningXp,
                            nutrition = user.nutritionXp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
            }
        }

        // MIYAMOTO MUSASHI / WARRIOR QUOTE CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ConvictionPrimaryRed.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ConvictionPrimaryRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Discipline Focus",
                        tint = ConvictionGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = randomizedQuote,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // DAILY TASKS / CODE OF DISCIPLINE LIST
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Code Of Discipline",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        "Adhere to your vows. Completing each grants +15 Discipline XP.",
                        fontSize = 11.sp,
                        color = ConvictionTextSecondary
                    )
                }
                
                IconButton(
                    onClick = { showAddHabitDialog = true },
                    modifier = Modifier.testTag("add_habit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add Habit",
                        tint = ConvictionPrimaryRed,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Render habits
        if (habits.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Vows completed! Create custom rules with the + indicator above.",
                        color = ConvictionTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                HabitRowItem(
                    habit = habit,
                    onToggle = { viewModel.toggleHabit(habit) },
                    onDelete = { viewModel.deleteHabit(habit) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Habit Dialog
    if (showAddHabitDialog) {
        val categories = listOf("Mental", "Discipline", "Physical", "Nutrition")
        AlertDialog(
            onDismissRequest = { showAddHabitDialog = false },
            title = {
                Text(
                    "Commit to a New Vow",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newHabitName,
                        onValueChange = { newHabitName = it },
                        label = { Text("Discipline Vow Name") },
                        isError = newHabitName.isBlank(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ConvictionPrimaryRed,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("habit_name_input")
                    )

                    Text(
                        "Vow Category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ConvictionTextSecondary
                    )

                    // Simple chips for selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = cat == newHabitCategory
                            FilterChip(
                                selected = isSelected,
                                onClick = { newHabitCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ConvictionPrimaryRed.copy(alpha = 0.3f),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newHabitName.isNotBlank()) {
                            viewModel.addHabit(newHabitName.trim(), newHabitCategory)
                            newHabitName = ""
                            showAddHabitDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ConvictionPrimaryRed),
                    modifier = Modifier.testTag("confirm_habit_button")
                ) {
                    Text("Seal Vow", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddHabitDialog = false }) {
                    Text("Forsake", color = ConvictionTextSecondary)
                }
            },
            containerColor = ConvictionCardBg
        )
    }
}

@Composable
fun HabitRowItem(
    habit: Habit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isCompletedToday) ConvictionPrimaryRed.copy(alpha = 0.08f) else ConvictionCardBg
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (habit.isCompletedToday) ConvictionPrimaryRed.copy(alpha = 0.4f) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Category Specific Icon badge
                val (color, icon) = when (habit.category.lowercase()) {
                    "mental" -> Pair(ConvictionGold, Icons.Default.Psychology)
                    "physical" -> Pair(ConvictionPrimaryRed, Icons.Default.FitnessCenter)
                    "nutrition" -> Pair(Color(0xFF81C784), Icons.Default.Restaurant)
                    else -> Pair(Color(0xFF64B5F6), Icons.Default.Shield)
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = habit.category,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = habit.name,
                        color = if (habit.isCompletedToday) ConvictionTextSecondary else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        style = if (habit.isCompletedToday) MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        ) else LocalTextStyle.current
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = habit.category,
                            color = ConvictionTextSecondary,
                            fontSize = 11.sp
                        )
                        if (habit.streak > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak Fire",
                                    tint = ConvictionGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "${habit.streak}d Streak",
                                    color = ConvictionGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Un-check / Check trigger & Delete button action
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (habit.isCompletedToday) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Toggle Vow",
                        tint = if (habit.isCompletedToday) ConvictionPrimaryRed else ConvictionTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Exterminate Vow",
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
