package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConvictionViewModel
import com.example.ui.theme.ConvictionCardBg
import com.example.ui.theme.ConvictionGold
import com.example.ui.theme.ConvictionPrimaryRed
import com.example.ui.theme.ConvictionTextSecondary

@Composable
fun WorkoutsScreen(
    viewModel: ConvictionViewModel,
    innerPadding: PaddingValues
) {
    val workoutsList = remember { getMartialArtsData() }
    var selectedArt by remember { mutableStateOf<MartialArtStyle?>(null) }
    
    var showLogSuccessMessage by remember { mutableStateOf(false) }
    var lastLoggedName by remember { mutableStateOf("") }
    var lastLoggedXp by remember { mutableStateOf(0) }

    var trainingDuration by remember { mutableStateOf(30f) }

    val loggedWorkouts by viewModel.allWorkouts.collectAsState()

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
                    imageVector = Icons.Default.SportsMartialArts,
                    contentDescription = "Combat Logo",
                    tint = ConvictionPrimaryRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "COMBAT ACADEMY",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Train in multi-style martial arts & earn physical skill XP.",
                        color = ConvictionTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Action feedback feedback overlay banner
        item {
            AnimatedVisibility(visible = showLogSuccessMessage) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Logged",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Locked in: $lastLoggedName (+$lastLoggedXp XP!)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { showLogSuccessMessage = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Feedback",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Active Logging Module if an art is selected
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Martial Discipline",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ConvictionGold
                )
                if (selectedArt != null) {
                    TextButton(onClick = { selectedArt = null }) {
                        Text("Clear Selection", color = ConvictionTextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Selection Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                workoutsList.chunked(2).forEach { rowList ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowList.forEach { art ->
                            val isSelected = selectedArt?.name == art.name
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) ConvictionPrimaryRed.copy(alpha = 0.2f) else ConvictionCardBg)
                                    .border(
                                        1.dp,
                                        if (isSelected) ConvictionPrimaryRed else Color.White.copy(alpha = 0.05f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedArt = art
                                        trainingDuration = 30f // Reset slider
                                    }
                                    .padding(14.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Column {
                                    Icon(
                                        imageVector = if (art.type == "Grappling") Icons.Default.Handshake else Icons.Default.FitnessCenter,
                                        contentDescription = art.type,
                                        tint = if (isSelected) ConvictionPrimaryRed else ConvictionGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = art.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = art.type,
                                        color = ConvictionTextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Technique Detail & Training Logger
        item {
            selectedArt?.let { art ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ConvictionCardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Technique Library: ${art.name}",
                                color = ConvictionGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ConvictionPrimaryRed.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = art.type,
                                    color = ConvictionPrimaryRed,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = art.info,
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Core Exercises:",
                            color = ConvictionGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        art.drills.forEach { drill ->
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DoubleArrow,
                                    contentDescription = "point",
                                    tint = ConvictionPrimaryRed,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(drill, color = Color.White, fontSize = 11.sp)
                            }
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = Color.White.copy(alpha = 0.08f)
                        )

                        // Durations Logger
                        Text(
                            text = "LOG PHYSICAL TRAINING",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Record training on this discipline to award physical XP.",
                            fontSize = 10.sp,
                            color = ConvictionTextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Minutes: ${trainingDuration.toInt()} mins",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "+${trainingDuration.toInt() + 15} XP Reward",
                                color = ConvictionGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }

                        Slider(
                            value = trainingDuration,
                            onValueChange = { trainingDuration = it },
                            valueRange = 10f..120f,
                            colors = SliderDefaults.colors(
                                thumbColor = ConvictionPrimaryRed,
                                activeTrackColor = ConvictionPrimaryRed,
                                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.testTag("training_duration_slider")
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val mins = trainingDuration.toInt()
                                viewModel.logWorkout(art.name, art.name, mins)
                                lastLoggedName = art.name
                                lastLoggedXp = mins + 15
                                showLogSuccessMessage = true
                                selectedArt = null // Clear selection state
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ConvictionPrimaryRed),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("log_workout_submit_button")
                        ) {
                            Text(
                                text = "Seal Combat Entry: Log Training",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ConvictionCardBg)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SportsMartialArts,
                        contentDescription = "Combat",
                        tint = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Click a martial art style above to access combat training and log session achievements.",
                        color = ConvictionTextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Historical Workouts Scroll list
        item {
            Text(
                text = "Combat Log History",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (loggedWorkouts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No combat records registered in history.",
                        color = ConvictionTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            items(loggedWorkouts, key = { it.id }) { log ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ConvictionCardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ConvictionPrimaryRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = "Workout Done",
                                    tint = ConvictionPrimaryRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = log.title,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${log.durationMin} mins duration",
                                    color = ConvictionTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ConvictionGold.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+${log.xpGained} XP",
                                color = ConvictionGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class MartialArtStyle(
    val name: String,
    val type: String, // Striking / Grappling
    val info: String,
    val drills: List<String>
)

fun getMartialArtsData(): List<MartialArtStyle> {
    return listOf(
        MartialArtStyle(
            name = "MMA",
            type = "Grappling & Striking",
            info = "Mixed Martial Arts centers integration: seamlessly combining punches, kicks, clinching grids, wrestling pickups, and submissions. Discipline is keeping constant posture switching.",
            drills = listOf("Sprawl to cross-punch combo (3 sets of 10)", "Wall-pin transition to single leg trap", "Combative sparring pace rhythm drill")
        ),
        MartialArtStyle(
            name = "Boxing",
            type = "Striking",
            info = "The Sweet Science. Master head movement, fluid footwork, high guard stance, and clean crisp punches: Jabs, Crosses, Hooks, and Uppercuts.",
            drills = listOf("Heavy shadowboxing (6 rounds, 3 mins each)", "Double-Jab-Cross-Hook bag series (10 mins)", "Slip and weave under elastic cord")
        ),
        MartialArtStyle(
            name = "Kickboxing",
            type = "Striking",
            info = "Dutch style punches-to-kicks blending. Combines clean ring punches with explosive roundhouse leg and liver kicks.",
            drills = listOf("Jab-Cross-Left Hook-Right Roundhouse kick drill (20 reps)", "Teep / Front push kick spacing control", "Leg-kick hardening and bag conditioning")
        ),
        MartialArtStyle(
            name = "Muay Thai",
            type = "Striking",
            info = "The Art of Eight Limbs. Unleash absolute combat damage using fists, elbows, knees, and shins. Focus heavy on clinch dominance.",
            drills = listOf("Thai-clinch knee strike reps (50 total)", "Intermittent elbow and shin-block series (10 mins)", "Aggressive heavy-bag roundhouse kicks")
        ),
        MartialArtStyle(
            name = "Wrestling",
            type = "Grappling",
            info = "Explosive leverage other combat styles cannot match. Control the hip plane, shoot double-legs, block with sprawls, and control top weights.",
            drills = listOf("Penetration shot technique drill (30 reps)", "Heavy sprawling speed response drills (3 mins)", "Slam ball slams representing body pick-ups")
        ),
        MartialArtStyle(
            name = "BJJ",
            type = "Grappling",
            info = "Brazilian Jiu-Jitsu. Defeat giants with submission leverages. Focus on closed guards, guard passing, hip escapes, sweeps, and armlocks.",
            drills = listOf("Solo shrimping hips escapes (50m total)", "Closed-guard bridge and sweep simulation", "Instructional leverage technique drill")
        ),
        MartialArtStyle(
            name = "Kyokushin Karate",
            type = "Striking",
            info = "The Ultimate Hard Karate. Stand in fire and endure. Emphasizes bare-knuckle discipline, hard combinations, blocks, and low leg kick barrages.",
            drills = listOf("Ibuki breathing power punches (5 mins)", "Hardening body-contact and heavy-bag kicks", "Kata Form Sanchin discipline practice")
        )
    )
}
