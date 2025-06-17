
package com.example.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.charts.DualAxisLineChart
import com.example.charts.model.Axis
import com.example.charts.model.ChartLine
import com.example.charts.model.Point

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoApp()
        }
    }
}

@Composable
fun DemoApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val temperatureLine = ChartLine(
                points = listOf(
                    Point(0f, -10f),
                    Point(1f, 0f),
                    Point(2f, 15f),
                    Point(3f, 30f)
                ),
                axis = Axis.LEFT,
                color = MaterialTheme.colorScheme.primary,
                name = "Temperature"
            )
            val humidityLine = ChartLine(
                points = listOf(
                    Point(0f, 20f),
                    Point(1f, 40f),
                    Point(2f, 70f),
                    Point(3f, 100f)
                ),
                axis = Axis.RIGHT,
                color = MaterialTheme.colorScheme.secondary,
                name = "Humidity"
            )
            DualAxisLineChart(
                lines = listOf(temperatureLine, humidityLine),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
