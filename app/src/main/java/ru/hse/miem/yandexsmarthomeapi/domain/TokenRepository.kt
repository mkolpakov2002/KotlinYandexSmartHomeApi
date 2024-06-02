package ru.hse.miem.yandexsmarthomeapi.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import ru.hse.miem.yandexsmarthomeapi.App

class TokenRepository private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveTokenAndUrl(token: String, url: String) {
        sharedPreferences.edit {
            putString("token", token)
            putString("url", url)
        }
    }

    fun getToken(): String? = sharedPreferences.getString("token", null)

    fun getUrl(): String? = sharedPreferences.getString("url", "https://api.iot.yandex.net")

    companion object {
        @Volatile
        private var instance: TokenRepository? = null

        fun getInstance(): TokenRepository {
            return instance ?: synchronized(this) {
                instance ?: TokenRepository(App.appContext).also { instance = it }
            }
        }
    }
}