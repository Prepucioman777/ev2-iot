package com.example.interfazlogin.vistas


import android.R
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth


@Composable
fun RegisterScreen(navController: NavController, auth: FirebaseAuth) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var showPass by rememberSaveable { mutableStateOf(false) }
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Correo
                OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (mín. 6)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showPass) "Ocultar" else "Mostrar"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Confirmación
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            imageVector = if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showConfirm) "Ocultar" else "Mostrar"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Registrar
            Button(
                onClick = {
                    validarRegistro(email, password, confirm, auth, context, exitoso = {
                        // navegar al login para iniciar sesion
                        navController.popBackStack()
                        navController.navigate("login")
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Crear cuenta")
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = { navController.popBackStack(); navController.navigate("login") }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

private fun ColumnScope.validarRegistro(
    email: String,
    password: String,
    confirm: String,
    auth: FirebaseAuth,
    context: Context,
    exitoso : () -> Unit
) {
    // validar campos vacios
    if (password.isBlank() || email.isBlank() || confirm.isBlank()){
        Toast.makeText(context, "rellena los campos", Toast.LENGTH_SHORT).show()

    }
    //validar el formato del correo
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        Toast.makeText(context, "correo invalido", Toast.LENGTH_SHORT).show()
    }

    if (password.length < 6){
        Toast.makeText(context, "la contraseña es demasiado corta", Toast.LENGTH_SHORT).show()

    }

    if (password != confirm){
        Toast.makeText(context, "las contraseñas no coinciden", Toast.LENGTH_SHORT).show()

    }

    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if(task.isSuccessful){
        Toast.makeText(context, " registro exitoso", Toast.LENGTH_SHORT).show()
        exitoso()

        } else {
            Toast.makeText(context, " error al registrarse ", Toast.LENGTH_SHORT).show()
        }
    }


}
