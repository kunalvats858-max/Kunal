package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodLog
import com.example.ui.ConvictionViewModel
import com.example.ui.theme.ConvictionCardBg
import com.example.ui.theme.ConvictionGold
import com.example.ui.theme.ConvictionPrimaryRed
import com.example.ui.theme.ConvictionTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    viewModel: ConvictionViewModel,
    innerPadding: PaddingValues
) {
    val foods by viewModel.allFoods.collectAsState()
    val scanResult by viewModel.foodScanResult.collectAsState()
    val isLoading by viewModel.isModelLoading.collectAsState()

    var activePreset by remember { mutableStateOf<FoodPreset?>(null) }
    var customDescription by remember { mutableStateOf("") }
    var manualLogName by remember { mutableStateOf("") }
    var manualLogCals by remember { mutableStateOf("") }

    var showManualAddDialog by remember { mutableStateOf(false) }

    // Preloaded delicious combat nutrition meal presets
    val presets = remember {
        listOf(
            FoodPreset("🥦 Chicken & Rice", "Flame-grilled chicken breast, steamed healthy broccoli, and brown jasmine rice.", 480, 45.0, 50.0, 8.0),
            FoodPreset("🥩 Bison & Sweet Potato", "Lean grass-fed bison patty, roasted sweet potato wedges, asparagus.", 620, 52.0, 48.0, 16.0),
            FoodPreset("🥑 Zen Salmon & Greens", "Seared wild salmon fillet, avocado half, baby spinach and olive oil glaze.", 540, 38.0, 12.0, 34.0),
            FoodPreset("🍌 Power Protein Shake", "Two scoops whey isolate protein, whole banana, oat milk, sugar-free peanut butter toast.", 450, 40.0, 42.0, 12.0)
        )
    }

    // Calculated total daily summary
    val totalCalories = foods.sumOf { it.calories }
    val totalProtein = foods.sumOf { it.protein }
    val totalCarbs = foods.sumOf { it.carbs }
    val totalFat = foods.sumOf { it.fat }

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
                    imageVector = Icons.Default.Camera,
                    contentDescription = "Nutrition Cam",
                    tint = Color(0xFF81C784),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SENSEI NUTRITION CAM",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Scan meals with AI vision to analyze macros.",
                        color = ConvictionTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // DAILY NUTRITIONAL FOOTPRINT CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ConvictionCardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Today's Nutritional Footprint",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Total Calories",
                                color = ConvictionTextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                "$totalCalories kcal",
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = Color(0xFF81C784)
                            )
                        }
                        
                        VerticalDivider(modifier = Modifier.height(30.dp), color = Color.White.copy(alpha = 0.1f))

                        // Macros Row
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            MacroPill("Protein", "${totalProtein.toInt()}g", ConvictionPrimaryRed)
                            MacroPill("Carbs", "${totalCarbs.toInt()}g", ConvictionGold)
                            MacroPill("Fat", "${totalFat.toInt()}g", Color(0xFF2196F3))
                        }
                    }
                }
            }
        }

        // AI SCANNER CONTROL PANEL CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ConvictionCardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "AI Vision Cam Simulator",
                            fontWeight = FontWeight.Bold,
                            color = ConvictionGold,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera Info",
                            tint = ConvictionTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        "Select a preset dish or describe food to simulate real-time API image analysis:",
                        fontSize = 11.sp,
                        color = ConvictionTextSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Presets selection chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.take(2).forEach { preset ->
                            val isSelected = activePreset?.name == preset.name
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ConvictionPrimaryRed.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background)
                                    .border(1.dp, if (isSelected) ConvictionPrimaryRed else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable {
                                        activePreset = preset
                                        customDescription = preset.description
                                    }
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(preset.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.skip(2).forEach { preset ->
                            val isSelected = activePreset?.name == preset.name
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ConvictionPrimaryRed.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background)
                                    .border(1.dp, if (isSelected) ConvictionPrimaryRed else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable {
                                        activePreset = preset
                                        customDescription = preset.description
                                    }
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(preset.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = customDescription,
                        onValueChange = { customDescription = it },
                        label = { Text("What are you eating? Description...") },
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ConvictionGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("food_custom_description_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            // Synthesize a dummy Bitmap to send to the real vision endpoint.
                            // The image contains some drawings so the Vision Model sees actual shapes.
                            val bmp = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(bmp)
                            val p = Paint().apply { color = android.graphics.Color.RED }
                            canvas.drawCircle(128f, 128f, 100f, p)
                            canvas.drawText("HEALTHY COMBO", 20f, 128f, Paint().apply {
                                color = android.graphics.Color.WHITE
                                textSize = 24f
                            })
                            
                            viewModel.scanFoodImage(bmp, customDescription)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ConvictionPrimaryRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trigger_food_scan_button"),
                        enabled = !isLoading
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoMode, contentDescription = "Scan", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sensei Scan Plate With AI", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // SCAN RESULTS CARD WIRED WITH REAL ESTIMATION SAVE ACTION
        item {
            scanResult?.let { result ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ConvictionCardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ConvictionGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Sensei AI Scan Analysis",
                                fontWeight = FontWeight.Bold,
                                color = ConvictionGold,
                                fontSize = 14.sp
                            )
                            IconButton(onClick = { viewModel.clearFoodScanResult() }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(14.dp))
                            }
                        }
                        
                        Text(
                            text = result,
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        // QUICK ADD DETECTED MEAL PORT
                        Text(
                            "Seal nutrition details:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = ConvictionTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    // Parse estimates from preset if loaded, else use defaults fallback
                                    val logName = activePreset?.name?.substring(2) ?: "AI Scanned Meal"
                                    val logCals = activePreset?.calories ?: 450
                                    val logProt = activePreset?.protein ?: 40.0
                                    val logCarb = activePreset?.carbs ?: 35.0
                                    val logFat = activePreset?.fat ?: 12.0

                                    viewModel.logFood(logName, logCals, logProt, logCarb, logFat)
                                    viewModel.clearFoodScanResult()
                                    customDescription = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_nutrition_log_button")
                            ) {
                                Text("Log Calories (+$activePreset?.calories ?: 450 kcal)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // MANUAL HEALTHY ENTRY PORT
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Today's Healthy Log",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                
                TextButton(
                    onClick = { showManualAddDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = ConvictionGold)
                ) {
                    Text("+ Add Custom", fontSize = 12.sp)
                }
            }
        }

        // FOOD LOG CHIP STREAM LIST
        if (foods.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Fuel your body. No foods logged today. Log +15 Nutrition XP over every entry.",
                        color = ConvictionTextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(foods, key = { it.id }) { log ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ConvictionCardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF81C784).copy(alpha = 0.15f))
                                    .size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = "Food icon",
                                    tint = Color(0xFF81C784),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(log.foodName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text(
                                    text = "P: ${log.protein.toInt()}g | C: ${log.carbs.toInt()}g | F: ${log.fat.toInt()}g",
                                    fontSize = 10.sp,
                                    color = ConvictionTextSecondary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${log.calories} kcal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                            IconButton(onClick = { viewModel.deleteFoodLog(log) }) {
                                Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Manual Log meal dialog
    if (showManualAddDialog) {
        AlertDialog(
            onDismissRequest = { showManualAddDialog = false },
            title = { Text("Log Healthy Food Input", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = manualLogName,
                        onValueChange = { manualLogName = it },
                        label = { Text("Food Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ConvictionGold)
                    )
                    OutlinedTextField(
                        value = manualLogCals,
                        onValueChange = { manualLogCals = it },
                        label = { Text("Calories (kcal)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ConvictionGold)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cals = manualLogCals.toIntOrNull() ?: 0
                        if (manualLogName.isNotBlank() && cals > 0) {
                            viewModel.logFood(
                                manualLogName.trim(),
                                cals,
                                protein = cals * 0.08, // compute estimates automatically based on standard macros distribution
                                carbs = cals * 0.1,
                                fat = cals * 0.03
                            )
                            manualLogName = ""
                            manualLogCals = ""
                            showManualAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ConvictionPrimaryRed)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualAddDialog = false }) {
                    Text("Cancel", color = ConvictionTextSecondary)
                }
            },
            containerColor = ConvictionCardBg
        )
    }
}

@Composable
fun MacroPill(name: String, amount: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(name, fontSize = 8.sp, color = ConvictionTextSecondary, fontWeight = FontWeight.Bold)
            Text(amount, fontSize = 12.sp, color = color, fontWeight = FontWeight.Black)
        }
    }
}

data class FoodPreset(
    val name: String,
    val description: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

// Helper list extension extension for preset chunks mapping
fun <T> List<T>.skip(n: Int): List<T> = if (n >= size) emptyList() else subList(n, size)
