package com.example.proyectobaselogin.vistas

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.proyectobaselogin.bluetooth.BluetoothManager
import com.example.proyectobaselogin.bluetooth.PermissionsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Pantalla principal que muestra el consumo de una lámpara con una barra de progreso tipo dona
 * Permite conectar por Bluetooth al Arduino para obtener datos en tiempo real
 */
@Composable
fun PrincipalScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Inicializar BluetoothManager
    val bluetoothManager = remember { BluetoothManager(context) }
    
    // Estado del consumo actual (se actualiza desde Arduino)
    var consumoActual by remember { mutableFloatStateOf(0.1f) }
    // Meta de consumo en amperios (ahora es mutable)
    var metaConsumo by remember { mutableFloatStateOf(0.15f) }
    // Estado de conexión Bluetooth
    var isConnected by remember { mutableStateOf(false) }
    // Estado de permisos
    var hasPermissions by remember { mutableStateOf(false) }
    // Estado para mostrar/ocultar el diálogo de ajuste de meta
    var showMetaDialog by remember { mutableStateOf(false) }
    // Estado para activar/desactivar la simulación de consumo
    var simularConsumo by remember { mutableStateOf(false) }
    // Porcentaje del consumo respecto a la meta
    val porcentaje = (consumoActual / metaConsumo).coerceAtMost(1f)
    
    // Solicitar permisos cuando se monta el componente
    PermissionsManager.RequestBluetoothPermissions(
        onPermissionsGranted = {
            hasPermissions = true
        },
        onPermissionsDenied = {
            hasPermissions = false
            Toast.makeText(context, "Se necesitan permisos de Bluetooth para conectar", Toast.LENGTH_LONG).show()
        }
    )
    
    // Limpiar conexión cuando se desmonte el componente
    DisposableEffect(Unit) {
        onDispose {
            if (bluetoothManager.isConnected()) {
                bluetoothManager.disconnect()
            }
        }
    }
    
    // Leer datos continuamente cuando está conectado
    LaunchedEffect(isConnected) {
        if (isConnected && bluetoothManager.isConnected()) {
            while (isConnected && bluetoothManager.isConnected()) {
                val data = withContext(Dispatchers.IO) {
                    bluetoothManager.readData()
                }
                data?.let {
                    try {
                        // Parsear el valor de amperios desde el Arduino
                        // El Arduino debería enviar el valor como texto, ej: "0.12"
                        val value = it.trim().toFloatOrNull()
                        value?.let { 
                            consumoActual = it
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                delay(1000) // Leer cada segundo
            }
        }
    }
    
    // Simular consumo cuando está activado (incrementa 0.00002 A por segundo)
    LaunchedEffect(simularConsumo) {
        if (simularConsumo) {
            while (simularConsumo) {
                consumoActual += 0.00002f
                delay(1000) // Incrementar cada segundo
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Mi consumo⚡",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Barra de progreso tipo dona
        Box(
            modifier = Modifier.size(250.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressDona(
                progress = porcentaje,
                modifier = Modifier.fillMaxSize()
            )
            
            // Texto en el centro de la dona
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(porcentaje * 100).toInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "de la meta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de conexión Bluetooth
        Button(
            onClick = {
                if (!hasPermissions) {
                    Toast.makeText(context, "Se necesitan permisos de Bluetooth", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                
                scope.launch {
                    if (!isConnected) {
                        // Verificar si Bluetooth está disponible
                        val isAvailable = withContext(Dispatchers.IO) {
                            bluetoothManager.isBluetoothAvailable()
                        }
                        
                        if (!isAvailable) {
                            Toast.makeText(context, "Bluetooth no está habilitado", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        
                        // TODO: Reemplazar con la dirección MAC real de tu Arduino
                        val macAddress = "00:00:00:00:00:00" // Cambiar por la MAC de tu Arduino
                        val connected = withContext(Dispatchers.IO) {
                            bluetoothManager.connectToDevice(macAddress)
                        }
                        
                        isConnected = connected
                        if (connected) {
                            Toast.makeText(context, "Conectado al Arduino", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al conectar. Verifica la dirección MAC", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        withContext(Dispatchers.IO) {
                            bluetoothManager.disconnect()
                        }
                        isConnected = false
                        Toast.makeText(context, "Desconectado", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            enabled = hasPermissions,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = "Bluetooth",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isConnected) "Desconectar Arduino" else "Conectar Arduino")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Botón para simular consumo
        Button(
            onClick = {
                simularConsumo = !simularConsumo
                if (!simularConsumo) {
                    Toast.makeText(context, "Simulación detenida", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Simulación iniciada: +0.00002 A/s", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
        ) {
            Text(if (simularConsumo) "Detener Simulación" else "Iniciar Simulación")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Botón para ajustar la meta diaria
        Button(
            onClick = {
                showMetaDialog = true
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ajustar Meta Diaria")
        }
        
        if (!hasPermissions) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Esperando permisos de Bluetooth...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Información del consumo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Consumo actual: ${String.format("%.2f", consumoActual)}A",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Meta: ${metaConsumo}A",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            if (isConnected) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Conectado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (simularConsumo) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Simulando: +0.00002 A/s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        
        // Diálogo para ajustar la meta diaria
        if (showMetaDialog) {
            MetaDialog(
                metaActual = metaConsumo,
                onDismiss = { showMetaDialog = false },
                onConfirm = { nuevaMeta ->
                    if (nuevaMeta > 0) {
                        metaConsumo = nuevaMeta
                        Toast.makeText(context, "Meta actualizada a ${String.format("%.2f", nuevaMeta)}A", Toast.LENGTH_SHORT).show()
                    }
                    showMetaDialog = false
                }
            )
        }
    }
}

/**
 * Diálogo para ajustar la meta diaria de consumo
 */
@Composable
fun MetaDialog(
    metaActual: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var metaTexto by remember { mutableStateOf(String.format("%.2f", metaActual)) }
    
    // Actualizar el texto cuando cambia la meta actual
    LaunchedEffect(metaActual) {
        metaTexto = String.format("%.2f", metaActual)
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ajustar Meta Diaria",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Ingresa la nueva meta de consumo en amperios (A)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = metaTexto,
                    onValueChange = { nuevoTexto ->
                        // Permitir solo números y un punto decimal
                        if (nuevoTexto.matches(Regex("^\\d*\\.?\\d*$"))) {
                            metaTexto = nuevoTexto
                        }
                    },
                    label = { Text("Meta (A)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Meta actual: ${String.format("%.2f", metaActual)}A",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val nuevaMeta = metaTexto.toFloatOrNull() ?: 0f
                    onConfirm(nuevaMeta)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Componente de barra de progreso circular tipo dona
 */
@Composable
fun CircularProgressDona(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeWidth: Float = 20f
) {
    Canvas(modifier = modifier) {
        val canvasSize = size.minDimension
        val strokeWidthPx = strokeWidth * density
        
        // Fondo del círculo
        drawArc(
            color = backgroundColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(
                (size.width - canvasSize) / 2,
                (size.height - canvasSize) / 2
            ),
            size = Size(canvasSize, canvasSize),
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round
            )
        )
        
        // Progreso del círculo
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = Offset(
                (size.width - canvasSize) / 2,
                (size.height - canvasSize) / 2
            ),
            size = Size(canvasSize, canvasSize),
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round
            )
        )
    }
}

