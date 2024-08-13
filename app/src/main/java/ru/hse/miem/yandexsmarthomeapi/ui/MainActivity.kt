package ru.hse.miem.yandexsmarthomeapi.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import ru.hse.miem.yandexsmarthomeapi.domain.YandexSmartHomeClient
import ru.hse.miem.yandexsmarthomeapi.ui.device_list.DeviceListViewModel
import ru.hse.miem.yandexsmarthomeapi.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = loadToken()
        val client = YandexSmartHomeClient.getInstance(
            "https://api.iot.yandex.net",
            token ?: "default_bearer_token"
        )
        setContent {
            val navController = rememberNavController()
            AppTheme {
                Surface(tonalElevation = 5.dp) {
                    AppNavHost(navController, client)
                }
            }
        }
    }

    private fun loadToken(): String? {
        val settings: Settings = Settings()
        return settings["yandex_token"]
    }
}

class DeviceListViewModelFactory(private val client: YandexSmartHomeClient) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceListViewModel(client) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}