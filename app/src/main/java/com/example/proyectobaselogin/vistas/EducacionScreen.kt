package com.example.proyectobaselogin.vistas

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Pantalla de educación - información sobre consumo energético con ejemlos
 */
@Composable
fun EducacionScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Educación Energética",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))


        Text(
            text = "Consejos para reducir tu consumo energético",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))


        Text(
            text = "Usa bombillas LED: Consumen hasta un 80% menos que las incandescentes y duran más.\n" +
                    "\n" +
                    "Aprovecha la luz natural: Abre cortinas y persianas durante el día.\n" +
                    "\n" +
                    "Apaga las luces cuando salgas de una habitación.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

