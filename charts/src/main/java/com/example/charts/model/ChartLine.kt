
package com.example.charts.model

import androidx.compose.ui.graphics.Color

enum class Axis { LEFT, RIGHT }

data class ChartLine(
    val points: List<Point>,
    val axis: Axis,
    val color: Color = Color.Unspecified,
    val name: String = ""
)
