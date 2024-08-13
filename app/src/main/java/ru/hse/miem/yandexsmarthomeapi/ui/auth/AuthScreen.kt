package ru.hse.miem.yandexsmarthomeapi.ui.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.musfickjamil.snackify.Snackify
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.authsdk.YandexAuthToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.hse.miem.yandexsmarthomeapi.R
import ru.hse.miem.yandexsmarthomeapi.ui.theme.AppTheme
import java.util.UUID

@Composable
fun AuthScreen(navController: NavHostController) {
    val context = LocalContext.current
//    val sdk = remember { YandexAuthSdk.create(YandexAuthOptions(context)) }
    val scope = rememberCoroutineScope()
    var showExitDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

//    val launcher = rememberLauncherForActivityResult(sdk.contract) { result ->
//        handleYandexAuthResult(result, navController, snackbarHostState, scope)
//    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val screenWidth = maxWidth

            Image(
                painter = painterResource(id = R.drawable.ya_icon),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Добро пожаловать в Умный дом",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(32.dp))

                ElevatedButton(
                    onClick = { navController.navigate("manual_auth_screen") },
                    modifier = Modifier.width(screenWidth * 0.8f)
                ) {
                    Text("Авторизация вручную")
                }

                ElevatedButton(
                    onClick = {
//                        val loginOptions = YandexAuthLoginOptions()
//                        launcher.launch(loginOptions)
                    },
                    modifier = Modifier.width(screenWidth * 0.8f)
                ) {
                    Text("Авторизация с Яндекс")
                }

                OutlinedButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier.width(screenWidth * 0.8f)
                ) {
                    Text("Выход")
                }
            }
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Подтверждение") },
                text = { Text("Вы уверены, что хотите выйти?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            scope.launch {
                                logout()
                                snackbarHostState.showSnackbar("Выход выполнен успешно")
                                navController.navigate("auth_screen") {
                                    popUpTo("auth_screen") { inclusive = true }
                                }
                            }
                        }
                    ) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

//private fun handleYandexAuthResult(
//    result: YandexAuthResult,
//    navController: NavHostController,
//    snackbarHostState: SnackbarHostState,
//    scope: CoroutineScope
//) {
//    when (result) {
//        is YandexAuthResult.Success -> onSuccessAuth(result.token, navController, snackbarHostState, scope)
//        is YandexAuthResult.Failure -> onProcessError(result.exception, snackbarHostState, scope)
//        YandexAuthResult.Cancelled -> onCancelled(snackbarHostState, scope)
//    }
//}

private fun onSuccessAuth(
    token: YandexAuthToken,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    scope.launch {
        saveToken(token.value)
        snackbarHostState.showSnackbar("Авторизация успешна")
        navController.navigate("device_list") {
            popUpTo("auth_screen") { inclusive = true }
        }
    }
}

private suspend fun saveToken(token: String) {
    val settings: Settings = Settings()
    settings["yandex_token"] = token
}

private fun onProcessError(
    exception: Exception,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    scope.launch {
        snackbarHostState.showSnackbar("Ошибка авторизации: ${exception.message}")
    }
}

private fun onCancelled(
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    scope.launch {
        snackbarHostState.showSnackbar("Авторизация отменена пользователем")
    }
}

private suspend fun logout() {
    val settings: Settings = Settings()
    settings.remove("yandex_token")
}

@Preview
@Composable
fun AuthScreenPreview() {
    AppTheme {
        AuthScreen(
            navController = rememberNavController()
        )
    }
}