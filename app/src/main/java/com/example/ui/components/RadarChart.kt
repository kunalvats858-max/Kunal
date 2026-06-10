package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ConvictionGold
import com.example.ui.theme.ConvictionPrimaryRed
import com.example.ui.theme.ConvictionTextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AttributeRadarChart(
    striking: Int,
    grappling: Int,
    discipline: Int,
    mental: Int,
    conditioning: Int,
    nutrition: Int,
    modifier: Modifier = Modifier
) {
    val labels = listOf("Striking", "Grappling", "Discipline", "Mental", "Conditioning", "Nutrition")
    val values = listOf(striking, grappling, discipline, mental, conditioning, nutrition)
    
    // Animate points for visual polish when values change
    val animatedValues = values.map { valState ->
        animateFloatAsState(
            targetValue = valState.toFloat().coerceIn(10f, 100f) / 100f,
            animationSpec = tween(durationMillis = 800),
            label = "chart_anim"
        )
    }

    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(32.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width.coerceAtMost(size.height) / 2
            val numPoints = 6
            val angleStep = (2 * Math.PI / numPoints).toFloat()

            // Drawing the hexagonal grid web
            val gridLevels = 4
            for (level in 1..gridLevels) {
                val currentRadius = radius * (level.toFloat() / gridLevels)
                val gridPath = Path().apply {
                    for (i in 0 until numPoints) {
                        val angle = i * angleStep - Math.PI.toFloat() / 2
                        val x = center.x + currentRadius * cos(angle)
                        val y = center.y + currentRadius * sin(angle)
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }
                drawPath(
                    path = gridPath,
                    color = Color.White.copy(alpha = 0.08f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Draw spoke lines from center to outer points
            for (i in 0 until numPoints) {
                val angle = i * angleStep - Math.PI.toFloat() / 2
                val outerX = center.x + radius * cos(angle)
                val outerY = center.y + radius * sin(angle)
                drawLine(
                    color = Color.White.copy(alpha = 0.12f),
                    start = center,
                    end = Offset(outerX, outerY),
                    strokeWidth = 1.dp.toPx()
                )

                // Render Labels beautifully around the radar chart
                val labelText = labels[i]
                val labelVal = values[i]
                val textLayoutResult = textMeasurer.measure(
                    text = "$labelText ($labelVal)",
                    style = androidx.compose.ui.text.TextStyle(
                        color = ConvictionTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                )

                // Offset labels for positioning outside the polygon
                val labelRadius = radius + 15.dp.toPx()
                val lx = center.x + labelRadius * cos(angle) - textLayoutResult.size.width / 2
                val ly = center.y + labelRadius * sin(angle) - textLayoutResult.size.height / 2
                
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(lx, ly)
                )
            }

            // Draw Value Polyline (User's attributes area)
            val fillPath = Path()
            val strokePath = Path()

            for (i in 0 until numPoints) {
                val angle = i * angleStep - Math.PI.toFloat() / 2
                val currentVal = animatedValues[i].value
                val x = center.x + radius * currentVal * cos(angle)
                val y = center.y + radius * currentVal * sin(angle)

                if (i == 0) {
                    fillPath.moveTo(x, y)
                    strokePath.moveTo(x, y)
                } else {
                    fillPath.lineTo(x, y)
                    strokePath.lineTo(x, y)
                }
            }
            fillPath.close()
            strokePath.close()

            // Translucent glowing fill representing inner resolve
            drawPath(
                path = fillPath,
                color = ConvictionPrimaryRed.copy(alpha = 0.35f)
            )

            // Dynamic Crimson contour
            drawPath(
                path = strokePath,
                color = ConvictionPrimaryRed,
                style = Stroke(width = 2.dp.toPx())
            )

            // Draw little gold dots on vertices
            for (i in 0 until numPoints) {
                val angle = i * angleStep - Math.PI.toFloat() / 2
                val currentVal = animatedValues[i].value
                val x = center.x + radius * currentVal * cos(angle)
                val y = center.y + radius * currentVal * sin(angle)

                drawCircle(
                    color = ConvictionGold,
                    radius = 3.5.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}
