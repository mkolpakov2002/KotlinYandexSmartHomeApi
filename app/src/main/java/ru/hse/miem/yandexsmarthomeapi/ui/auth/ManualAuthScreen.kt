package ru.hse.miem.yandexsmarthomeapi.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

@Composable
fun ManualAuthScreen(navController: NavHostController) {
    var baseUrl by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = baseUrl,
            onValueChange = { baseUrl = it },
            label = { Text("Base URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("Token") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (baseUrl.isNotBlank() && token.isNotBlank()) {
                    saveToken(token)
                    onManualAuthSuccess(navController)
                } else {
                    errorMessage = "Both fields are required"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

private fun saveToken(token: String) {
    val settings: Settings = Settings()
    settings["yandex_token"] = token
}

private fun onManualAuthSuccess(navController: NavHostController) {
    // Navigate to the next screen after successful manual authentication
    navController.navigate("device_list") {
        popUpTo("manual_auth_screen") { inclusive = true }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManualAuthScreen() {
    val navController = rememberNavController()
    MaterialTheme {
        ManualAuthScreen(navController)
    }
}