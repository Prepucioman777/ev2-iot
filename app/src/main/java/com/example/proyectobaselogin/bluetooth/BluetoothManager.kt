package com.example.proyectobaselogin.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

/**
 * Gestor de conexión Bluetooth para comunicarse con dispositivos Arduino
 */
class BluetoothManager(private val context: Context) {
    private val TAG = "BluetoothManager"
    
    // UUID estándar para Serial Port Profile (SPP)
    private val UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var isConnectedFlag = false
    
    init {
        bluetoothAdapter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager
            bluetoothManager?.adapter
        } else {
            @Suppress("DEPRECATION")
            BluetoothAdapter.getDefaultAdapter()
        }
    }
    
    /**
     * Verifica si Bluetooth está disponible y habilitado
     */
    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter != null && bluetoothAdapter?.isEnabled == true
    }
    
    /**
     * Conecta a un dispositivo Bluetooth por dirección MAC
     */
    suspend fun connectToDevice(address: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!isBluetoothAvailable()) {
                Log.e(TAG, "Bluetooth no está disponible o habilitado")
                return@withContext false
            }
            
            val device = bluetoothAdapter?.getRemoteDevice(address)
            if (device == null) {
                Log.e(TAG, "No se pudo obtener el dispositivo con dirección: $address")
                return@withContext false
            }
            
            // Cerrar conexión anterior si existe
            disconnect()
            
            // Crear socket RFCOMM
            bluetoothSocket = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                device.createRfcommSocketToServiceRecord(UUID_SPP)
            } else {
                @Suppress("DEPRECATION")
                device.createRfcommSocketToServiceRecord(UUID_SPP)
            }
            
            bluetoothSocket?.let { socket ->
                // Cancelar descubrimiento si está en curso
                bluetoothAdapter?.cancelDiscovery()
                
                // Conectar al dispositivo
                socket.connect()
                
                // Obtener streams de entrada y salida
                inputStream = socket.inputStream
                outputStream = socket.outputStream
                
                isConnectedFlag = true
                Log.d(TAG, "Conectado exitosamente a $address")
                return@withContext true
            } ?: run {
                Log.e(TAG, "No se pudo crear el socket Bluetooth")
                return@withContext false
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error al conectar: ${e.message}", e)
            disconnect()
            return@withContext false
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado: ${e.message}", e)
            disconnect()
            return@withContext false
        }
    }
    
    /**
     * Desconecta del dispositivo Bluetooth
     */
    fun disconnect() {
        try {
            isConnectedFlag = false
            
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
            
            inputStream = null
            outputStream = null
            bluetoothSocket = null
            
            Log.d(TAG, "Desconectado del dispositivo Bluetooth")
        } catch (e: IOException) {
            Log.e(TAG, "Error al desconectar: ${e.message}", e)
        }
    }
    
    /**
     * Verifica si está conectado
     */
    fun isConnected(): Boolean {
        return isConnectedFlag && bluetoothSocket?.isConnected == true
    }
    
    /**
     * Lee datos del dispositivo Bluetooth
     */
    suspend fun readData(): String? = withContext(Dispatchers.IO) {
        try {
            if (!isConnected()) {
                return@withContext null
            }
            
            val buffer = ByteArray(1024)
            val bytes = inputStream?.read(buffer)
            
            if (bytes != null && bytes > 0) {
                val data = String(buffer, 0, bytes)
                Log.d(TAG, "Datos recibidos: $data")
                return@withContext data.trim()
            }
            
            return@withContext null
        } catch (e: IOException) {
            Log.e(TAG, "Error al leer datos: ${e.message}", e)
            if (!isConnected()) {
                isConnectedFlag = false
            }
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado al leer: ${e.message}", e)
            return@withContext null
        }
    }
    
    /**
     * Escribe datos al dispositivo Bluetooth
     */
    suspend fun writeData(data: String): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!isConnected()) {
                Log.e(TAG, "No hay conexión activa")
                return@withContext false
            }
            
            val bytes = data.toByteArray()
            outputStream?.write(bytes)
            outputStream?.flush()
            
            Log.d(TAG, "Datos enviados: $data")
            return@withContext true
        } catch (e: IOException) {
            Log.e(TAG, "Error al escribir datos: ${e.message}", e)
            if (!isConnected()) {
                isConnectedFlag = false
            }
            return@withContext false
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado al escribir: ${e.message}", e)
            return@withContext false
        }
    }
    
    /**
     * Obtiene la lista de dispositivos Bluetooth emparejados
     */
    fun getPairedDevices(): Set<BluetoothDevice> {
        return try {
            if (!isBluetoothAvailable()) {
                emptySet()
            } else {
                @Suppress("DEPRECATION")
                bluetoothAdapter?.bondedDevices ?: emptySet()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener dispositivos emparejados: ${e.message}", e)
            emptySet()
        }
    }
}

