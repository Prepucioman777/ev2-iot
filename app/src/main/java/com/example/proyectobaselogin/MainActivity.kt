package com.example.proyectobaselogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectobaselogin.vistas.LoginScreen
import com.example.proyectobaselogin.vistas.RegisterScreen
import com.example.proyectobaselogin.ui.theme.ProyectoBaseLoginTheme
import com.example.proyectobaselogin.vistas.interfazhome
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //iniciando la autenticación de firebase
        auth = Firebase.auth
        enableEdgeToEdge()
        setContent {
            ProyectoBaseLoginTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(navController, auth)
                    }
                    composable("register") {
                        RegisterScreen(navController, auth)
                    }
                    composable("home") {
                        interfazhome(navController)
                    }
                }
            }
        }





    }



}


