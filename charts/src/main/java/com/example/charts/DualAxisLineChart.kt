
package com.example.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.charts.model.Axis
import com.example.charts.model.ChartLine
import kotlin.math.abs
import kotlin.math.min

@Composable
fun DualAxisLineChart(
    lines: List<ChartLine>,
    modifier: Modifier = Modifier
) {
    val scaleX = remember { mutableStateOf(1f) }
    val panX = remember { mutableStateOf(0f) }
    var tappedX by remember { mutableStateOf<Float?>(null) }

    val transformModifier = Modifier.pointerInput(Unit) {
        detectTransformGestures { _, pan, zoom, _ ->
            scaleX.value = (scaleX.value * zoom).coerceIn(1f, 5f)
            panX.value = (panX.value + pan.x).coerceIn(-1000f, 1000f)
        }
    }

    Box(modifier.then(transformModifier)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val allPoints = lines.flatMap { it.points }
            val xMin = allPoints.minOfOrNull { it.x } ?: 0f
            val xMax = allPoints.maxOfOrNull { it.x } ?: 1f

            val animProgress = remember { Animatable(0f) }
            LaunchedEffect(lines) {
                animProgress.snapTo(0f)
                animProgress.animateTo(1f, animationSpec = tween(1000))
            }

            lines.forEach { line ->
                val axisLines = lines.filter { it.axis == line.axis }
                val yValues = axisLines.flatMap { it.points.map { p -> p.y } }
                val yMin = yValues.minOrNull() ?: 0f
                val yMax = yValues.maxOrNull() ?: 1f

                val path = Path()
                val points = line.points.map {
                    Offset(
                        ((it.x - xMin) / (xMax - xMin)) * width * scaleX.value + panX.value,
                        height * (1 - (it.y - yMin) / (yMax - yMin))
                    )
                }

                if (points.isNotEmpty()) path.moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val midX = (points[i - 1].x + points[i].x) / 2
                    path.cubicTo(midX, points[i - 1].y, midX, points[i].y, points[i].x, points[i].y)
                }

                drawPath(
                    path = path,
                    color = if (line.color == Color.Unspecified) MaterialTheme.colorScheme.primary else line.color,
                    style = Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.cornerPathEffect(10f)),
                    alpha = animProgress.value
                )
            }

            // Draw vertical line and tooltip for tapped point
            tappedX?.let { xTap ->
                val allOffsets = lines.flatMap { line ->
                    line.points.map {
                        val px = ((it.x - xMin) / (xMax - xMin)) * width * scaleX.value + panX.value
                        px to it
                    }
                }

                val closest = allOffsets.minByOrNull { abs(it.first - xTap) }?.second
                closest?.let {
                    drawLine(
                        color = Color.Gray,
                        start = Offset(xTap, 0f),
                        end = Offset(xTap, height),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawCircle(
                        color = Color.Red,
                        radius = 6.dp.toPx(),
                        center = Offset(xTap, height * (1 - (it.y - yValues.minOrNull()!!) / (yValues.maxOrNull()!! - yValues.minOrNull()!!)))
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        "x=${it.x}, y=${it.y}",
                        xTap,
                        20f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 30f
                        }
                    )
                }
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset -> tappedX = offset.x }
                }
        )
    }
}
