package com.example.proyectobaselogin.vistas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastForEducation
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/**
 * Pantalla principal con barra de navegación inferior funcional
 * Permite navegar entre tres secciones: Principal, Registros y Educación
 */
@Composable
fun interfazhome(navController: NavHostController? = null) {
    // Estado que guarda la sección seleccionada (0=Principal, 1=Registros, 2=Educación)
    var selectedDestination by rememberSaveable { mutableIntStateOf(Destination.PRINCIPAL.ordinal) }

    Scaffold(
        bottomBar = {
            // Barra de navegación inferior con tres secciones
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = { selectedDestination = index },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.descripcion
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            when(selectedDestination) {
                Destination.PRINCIPAL.ordinal -> {
                    PrincipalScreen()
                }
                Destination.REGISTROS.ordinal -> {
                    RegistrosScreen()
                }
                Destination.EDUCACION.ordinal -> {
                    EducacionScreen()
                }
            }
        }
    }
}


/**
 * Enum que define los destinos de la barra de navegación
 */
enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val descripcion: String
){
    PRINCIPAL("principal", "Principal", Icons.Default.EnergySavingsLeaf, "pagina principal"),
    REGISTROS("registros", "Registros", Icons.Default.GraphicEq, "registro de datos"),
    EDUCACION("educacion", "Educacion", Icons.Default.CastForEducation, "algunos datos educativos")
}

@Composable
fun interfazPreview(){
    interfazhome()
}
