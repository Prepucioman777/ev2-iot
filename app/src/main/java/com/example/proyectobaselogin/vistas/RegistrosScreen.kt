package com.example.proyectobaselogin.vistas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pantalla de registros - muestra el historial de consumos mensuales con gráfico de barras
 */
@Composable
fun RegistrosScreen() {
    // Datos de consumo mensual (ejemplo)
    val consumosMensuales = listOf(
        "Ene" to 0.12f,
        "Feb" to 0.15f,
        "Mar" to 0.11f,
        "Abr" to 0.13f,
        "May" to 0.14f,
        "Jun" to 0.10f
    )
    
    val maxConsumo = consumosMensuales.maxOfOrNull { it.second } ?: 0.2f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Historial de Consumos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        // Gráfico de barras
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            BarChart(
                data = consumosMensuales,
                maxValue = maxConsumo,
                primaryColor = MaterialTheme.colorScheme.primary,
                surfaceColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(24.dp))

        // Leyenda
        Text(
            text = "Consumo mensual en amperios (A)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

/**
 * Componente de gráfico de barras para mostrar consumo mensual
 */
@Composable
fun BarChart(
    data: List<Pair<String, Float>>,
    maxValue: Float,
    primaryColor: androidx.compose.ui.graphics.Color,
    surfaceColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val barWidth = size.width / (data.size * 2f)
        val chartHeight = size.height * 0.7f
        val chartBottom = size.height - 50f
        val spacing = barWidth
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 30f
            textAlign = android.graphics.Paint.Align.CENTER
        }

        data.forEachIndexed { index, (label, value) ->
            val x = spacing + (barWidth + spacing) * index + barWidth / 2
            val barHeight = (value / maxValue) * chartHeight
            val barTop = chartBottom - barHeight

            // Dibujar barra
            drawRect(
                color = primaryColor,
                topLeft = Offset(x - barWidth / 2, barTop),
                size = Size(barWidth, barHeight)
            )

            // Dibujar etiqueta del mes
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    label,
                    x,
                    size.height - 10f,
                    paint
                )
                // Dibujar valor sobre la barra
                canvas.nativeCanvas.drawText(
                    String.format("%.2f", value),
                    x,
                    barTop - 10f,
                    paint
                )
            }
        }

        // Línea base
        drawLine(
            color = surfaceColor.copy(alpha = 0.3f),
            start = Offset(0f, chartBottom),
            end = Offset(size.width, chartBottom),
            strokeWidth = 2f
        )
    }
}

