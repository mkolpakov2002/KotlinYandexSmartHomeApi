package ru.hse.miem.yandexsmarthomeapi.domain

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexApiResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexDeviceGroupResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexDeviceStateResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexErrorModelResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageDeviceCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageDeviceCapabilitiesStateResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageGroupCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageGroupCapabilitiesStateResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexUserInfoResponse


/**
 * Класс YandexSmartHomeClient предоставляет функционал для управления
 * устройствами умного дома через API Яндекса.
 *
 * @property endpoint Хост для всех запросов к API.
 * @property bearerToken Токен для авторизации в API.
 * @property config Конфигурация клиента с настройками таймаутов и других параметров.
 */
class YandexSmartHomeClient private constructor(
    private var endpoint: String,
    private var bearerToken: String,
    private val config: YandexSmartHomeConfig = YandexSmartHomeConfig()
) : YandexSmartHomeApi {

    companion object {
        @Volatile
        private var INSTANCE: YandexSmartHomeClient? = null

        /**
         * Возвращает экземпляр YandexSmartHomeClient, создавая его при необходимости.
         *
         * @param endpoint Хост для всех запросов к API.
         * @param bearerToken Токен для авторизации в API.
         * @param config Конфигурация клиента (опционально).
         * @return Экземпляр YandexSmartHomeClient.
         */
        fun getInstance(endpoint: String, bearerToken: String, config: YandexSmartHomeConfig = YandexSmartHomeConfig()): YandexSmartHomeClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: YandexSmartHomeClient(endpoint, bearerToken, config).also { INSTANCE = it }
            }
        }
    }

    private var client: HttpClient = createHttpClient()

    /**
     * Создает и настраивает HTTP-клиент для работы с API.
     *
     * @return Настроенный HttpClient.
     */
    private fun createHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
            defaultRequest {
                header(HttpHeaders.Authorization, "Bearer $bearerToken")
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = config.requestTimeoutMillis
                connectTimeoutMillis = config.connectTimeoutMillis
            }
            HttpResponseValidator {
                validateResponse { response ->
                    if (!response.status.isSuccess()) {
                        val errorBody = response.bodyAsText()
                        throw YandexApiException(response.status, errorBody)
                    }
                }
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }

    /**
     * Обновляет настройки клиента.
     *
     * @param endpoint Новый хост для запросов к API.
     * @param bearerToken Новый токен для авторизации.
     */
    fun updateClient(endpoint: String, bearerToken: String) {
        this.endpoint = endpoint
        this.bearerToken = bearerToken
        this.client = createHttpClient()
    }

    /**
     * Обрабатывает HTTP-ответ и преобразует его в соответствующий объект YandexApiResponse.
     *
     * @param response HTTP-ответ от API.
     * @return Объект YandexApiResponse.
     */
    private suspend inline fun <reified T : YandexResponse> handleResponse(response: HttpResponse): YandexApiResponse {
        return try {
            val data = response.body<T>()
//            logger.info("Received response: $data")
            when (data) {
                is YandexManageDeviceCapabilitiesStateResponse -> handleCapabilityResponse(data)
                is YandexManageGroupCapabilitiesStateResponse -> handleCapabilityResponse(data)
                is YandexUserInfoResponse -> YandexApiResponse.SuccessUserInfo(data)
                is YandexDeviceStateResponse -> YandexApiResponse.SuccessDeviceState(data)
                is YandexDeviceGroupResponse -> YandexApiResponse.SuccessDeviceGroup(data)
                else -> throw UnknownResponseTypeException("Unknown response type: ${T::class.simpleName}")
            }
        } catch (e: Exception) {
//            logger.error("Error handling response", e)
            YandexApiResponse.Error(YandexErrorModelResponse("error", "", "Exception: ${e.message}"))
        }
    }

    /**
     * Обрабатывает ответ, связанный с управлением возможностями устройств.
     *
     * @param response Ответ от API.
     * @return Объект YandexApiResponse.
     */
    private fun <T : YandexResponse> handleCapabilityResponse(response: T): YandexApiResponse {
        val errors = when (response) {
            is YandexManageDeviceCapabilitiesStateResponse -> checkForErrorsInCapabilities(response.devices)
            is YandexManageGroupCapabilitiesStateResponse -> checkForErrorsInCapabilities(response.devices)
            else -> emptyList()
        }
        return if (errors.isNotEmpty()) {
            YandexApiResponse.Error(errors.first())
        } else {
            when (response) {
                is YandexManageDeviceCapabilitiesStateResponse -> YandexApiResponse.SuccessManageDeviceCapabilitiesState(response)
                is YandexManageGroupCapabilitiesStateResponse -> YandexApiResponse.SuccessManageGroupCapabilitiesState(response)
                else -> throw UnknownResponseTypeException("Unknown capability response type: ${response::class.simpleName}")
            }
        }
    }

    /**
     * Получает информацию о пользователе.
     *
     * @return Объект YandexApiResponse с информацией о пользователе.
     */
    override suspend fun getUserInfo(): YandexApiResponse {
        return try {
            val response = client.get("$endpoint/v1.0/user/info")
            handleResponse<YandexUserInfoResponse>(response)
        } catch (e: Exception) {
            logAndReturnError("getUserInfo", e)
        }
    }

    /**
     * Получает состояние устройства.
     *
     * @param deviceId ID устройства.
     * @return Объект YandexApiResponse с состоянием устройства.
     */
    override suspend fun getDeviceState(deviceId: String): YandexApiResponse {
        return try {
            val response = client.get("$endpoint/v1.0/devices/$deviceId")
            handleResponse<YandexDeviceStateResponse>(response)
        } catch (e: Exception) {
            logAndReturnError("getDeviceState", e)
        }
    }

    /**
     * Управляет состоянием возможностей устройства.
     *
     * @param request Запрос на изменение состояния возможностей устройства.
     * @return Объект YandexApiResponse с результатом операции.
     */
    override suspend fun manageDeviceCapabilitiesState(request: YandexManageDeviceCapabilitiesStateRequest): YandexApiResponse {
        return try {
            val response = client.post("$endpoint/v1.0/devices/actions") {
                setBody(request)
            }
            handleResponse<YandexManageDeviceCapabilitiesStateResponse>(response)
        } catch (e: Exception) {
            logAndReturnError("manageDeviceCapabilitiesState", e)
        }
    }

    /**
     * Управляет состоянием возможностей группы устройств.
     *
     * @param groupId ID группы устройств.
     * @param request Запрос на изменение состояния возможностей группы устройств.
     * @return Объект YandexApiResponse с результатом операции.
     */
    override suspend fun manageGroupCapabilitiesState(
        groupId: String,
        request: YandexManageGroupCapabilitiesStateRequest
    ): YandexApiResponse {
        return try {
            val response = client.post("$endpoint/v1.0/groups/$groupId/actions") {
                setBody(request)
            }
            handleResponse<YandexManageGroupCapabilitiesStateResponse>(response)
        } catch (e: Exception) {
            logAndReturnError("manageGroupCapabilitiesState", e)
        }
    }

    /**
     * Получает информацию о группе устройств.
     *
     * @param groupId ID группы устройств.
     * @return Объект YandexApiResponse с информацией о группе устройств.
     */
    override suspend fun getDeviceGroup(groupId: String): YandexApiResponse {
        return try {
            val response = client.get("$endpoint/v1.0/groups/$groupId")
            handleResponse<YandexDeviceGroupResponse>(response)
        } catch (e: Exception) {
            logAndReturnError("getDeviceGroup", e)
        }
    }

    /**
     * Логирует ошибку и возвращает объект ошибки.
     *
     * @param method Имя метода, в котором произошла ошибка.
     * @param e Исключение.
     * @return Объект ошибки.
     */
    private fun logAndReturnError(method: String, e: Exception): YandexApiResponse.Error {
        val error = YandexErrorModelResponse("error", "", "Exception in $method: ${e.message}")
//        logger.error(error.toString(), e)
        return YandexApiResponse.Error(error)
    }

    /**
     * Проверяет наличие ошибок в ответе о возможностях устройств.
     *
     * @param devices Список устройств для проверки.
     * @return Список ошибок, если они есть.
     */
    private fun checkForErrorsInCapabilities(devices: List<JsonObject>): List<YandexErrorModelResponse> {
        return devices.flatMap { device ->
            device.jsonObject["capabilities"]?.jsonArray?.mapNotNull { capability ->
                val state = capability.jsonObject["state"]?.jsonObject
                val actionResult = state?.get("action_result")?.jsonObject
                if (actionResult != null && actionResult["status"]?.jsonPrimitive?.content == "ERROR") {
                    val errorCode = actionResult["error_code"]?.jsonPrimitive?.content
                    val errorMessage = actionResult["error_message"]?.jsonPrimitive?.content
                    YandexErrorModelResponse("error", "", "$errorCode: $errorMessage")
                } else null
            } ?: emptyList()
        }
    }
}

/**
 * Конфигурация для YandexSmartHomeClient.
 *
 * @property requestTimeoutMillis Таймаут для запросов в миллисекундах.
 * @property connectTimeoutMillis Таймаут для установки соединения в миллисекундах.
 */
data class YandexSmartHomeConfig(
    val requestTimeoutMillis: Long = 30000,
    val connectTimeoutMillis: Long = 10000
)

/**
 * Исключение, которое выбрасывается при ошибках API Яндекса.
 *
 * @property statusCode HTTP-статус ответа.
 * @property errorBody Тело ответа с ошибкой.
 */
class YandexApiException(val statusCode: HttpStatusCode, val errorBody: String) : Exception("API error: $statusCode, body: $errorBody")

/**
 * Исключение, которое выбрасывается при получении неизвестного типа ответа.
 *
 * @property message Сообщение об ошибке.
 */
class UnknownResponseTypeException(message: String) : Exception(message)