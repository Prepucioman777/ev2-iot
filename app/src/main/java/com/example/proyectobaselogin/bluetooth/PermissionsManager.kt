package com.example.proyectobaselogin.bluetooth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Manager para solicitar permisos de Bluetooth en tiempo de ejecución
 */
object PermissionsManager {
    
    /**
     * Verifica si todos los permisos necesarios están concedidos
     */
    fun hasAllPermissions(context: Context): Boolean {
        val permissions = getRequiredPermissions()
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * Obtiene la lista de permisos requeridos según la versión de Android
     */
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 (API 31) y superior
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            // Android 11 (API 30) e inferior
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }
    
    /**
     * Composable que maneja la solicitud de permisos
     * @param onPermissionsGranted Callback que se ejecuta cuando todos los permisos son concedidos
     * @param onPermissionsDenied Callback que se ejecuta cuando los permisos son denegados
     */
    @Composable
    fun RequestBluetoothPermissions(
        onPermissionsGranted: () -> Unit,
        onPermissionsDenied: () -> Unit = {}
    ) {
        val context = LocalContext.current
        val permissions = remember { getRequiredPermissions() }
        
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsMap ->
            val allGranted = permissionsMap.values.all { it }
            if (allGranted) {
                onPermissionsGranted()
            } else {
                onPermissionsDenied()
            }
        }
        
        LaunchedEffect(Unit) {
            if (!hasAllPermissions(context)) {
                launcher.launch(permissions)
            } else {
                onPermissionsGranted()
            }
        }
    }
}

