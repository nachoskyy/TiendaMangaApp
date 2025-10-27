package com.example.tiendamangaapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    var emailTouched by remember { mutableStateOf(false) }
    var passTouched by remember { mutableStateOf(false) }
    var triedSubmit by remember { mutableStateOf(false) }

    val allowedDomains = listOf("gmail.com", "duocuc.cl", "outlook.com")

    fun isValidEmail(value: String): Boolean {
        val parts = value.split("@")
        if (parts.size != 2) return false
        val domain = parts[1].lowercase()
        return allowedDomains.contains(domain) && parts[0].isNotBlank()
    }

    val passRegex = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*().,?]).{6,}\$")

    val emailOk = isValidEmail(email)
    val passOk = passRegex.matches(pass)
    val formOk = emailOk && passOk

    fun tryLogin() {
        triedSubmit = true
        if (formOk) onLoginSuccess()
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(Modifier.padding(24.dp).fillMaxWidth(0.9f)) {
            Text("Inicia sesión", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("ej: usuario@gmail.com, @duocuc.cl, @outlook.com") },
                singleLine = true,
                isError = (triedSubmit || emailTouched) && !emailOk,
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    if ((triedSubmit || emailTouched) && !emailOk) {
                        Text(
                            "Usa un correo @gmail.com, @duocuc.cl o @outlook.com",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            LaunchedEffect(Unit) { }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Contraseña") },
                placeholder = { Text("mínimo 6 caracteres") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = (triedSubmit || passTouched) && !passOk,
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    if ((triedSubmit || passTouched) && !passOk) {
                        Text(
                            "Debe tener 6+ caracteres, 1 mayúscula, 1 número, y un caracter especial.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            DisposableEffect(Unit) {
                onDispose { }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { tryLogin() },
                enabled = formOk,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Entrar") }
        }
    }

    LaunchedEffect(email) { if (email.isNotEmpty()) emailTouched = true }
    LaunchedEffect(pass)  { if (pass.isNotEmpty()) passTouched = true }
}
