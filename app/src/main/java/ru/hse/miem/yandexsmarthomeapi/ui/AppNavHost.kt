package ru.hse.miem.yandexsmarthomeapi.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.hse.miem.yandexsmarthomeapi.domain.YandexSmartHomeClient
import ru.hse.miem.yandexsmarthomeapi.ui.auth.AuthScreen
import ru.hse.miem.yandexsmarthomeapi.ui.auth.ManualAuthScreen
import ru.hse.miem.yandexsmarthomeapi.ui.device_detail.DeviceDetailScreen
import ru.hse.miem.yandexsmarthomeapi.ui.device_list.DeviceListScreen
import ru.hse.miem.yandexsmarthomeapi.ui.device_list.DeviceListViewModel
import ru.hse.miem.yandexsmarthomeapi.ui.theme.AppTheme

@Composable
fun AppNavHost(navController: NavHostController, client: YandexSmartHomeClient) {
    val viewModel: DeviceListViewModel = viewModel(factory = DeviceListViewModelFactory(client))

    NavHost(navController = navController, startDestination = "auth_screen") {
        composable("auth_screen") {
            AuthScreen(navController)
        }
        composable("manual_auth_screen") {
            ManualAuthScreen(navController)
        }
        composable("device_list") {
            DeviceListScreen(navController, viewModel)
        }
        composable("device_detail/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId")
            DeviceDetailScreen(deviceId ?: "", viewModel, onDismiss = { navController.popBackStack() })
        }
    }
}