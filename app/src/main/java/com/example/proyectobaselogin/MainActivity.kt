package com.example.proyectobaselogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.interfazlogin.vistas.LoginScreen
import com.example.interfazlogin.vistas.RegisterScreen
import com.example.proyectobaselogin.ui.theme.ProyectoBaseLoginTheme
import com.example.proyectobaselogin.vistas.interfazhome
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //iniciando la autenticación de firebase
            auth = Firebase.auth
            ProyectoBaseLoginTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

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
                            composable("home" )
                            { interfazhome(navController) }
                        }

                    }
                }
            }
        }





    }



}


