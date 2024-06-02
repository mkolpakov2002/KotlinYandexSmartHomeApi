package ru.hse.miem.yandexsmarthomeapi.domain

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
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
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.Status

/**
 * Класс YandexSmartHomeClient предоставляет функционал для управления
 * устройствами умного дома через API Яндекса.
 *
 * @property endpoint Хост для всех запросов к API.
 * @property bearerToken Токен для авторизации в API.
 */
class YandexSmartHomeClient private constructor(
    private var endpoint: String,
    private var bearerToken: String
) : YandexSmartHomeApi {

    companion object {
        @Volatile
        private var INSTANCE: YandexSmartHomeClient? = null

        fun getInstance(endpoint: String, bearerToken: String): YandexSmartHomeClient {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: YandexSmartHomeClient(endpoint, bearerToken).also { INSTANCE = it }
            }
        }
    }

    private var client: HttpClient = createHttpClient()

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
            HttpResponseValidator {
                validateResponse { response ->
                    if (!response.status.isSuccess()) {
                        val errorResponse = try {
                            response.body<YandexErrorModelResponse>()
                        } catch (e: Exception) {
                            YandexErrorModelResponse(
                                Status.ERROR.code,
                                "",
                                e.message ?: "Unknown error"
                            )
                        }
                        logger {
                            error(errorResponse.toString())
                        }
                    }
                }
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }

    fun updateClient(endpoint: String, bearerToken: String) {
        this.endpoint = endpoint
        this.bearerToken = bearerToken
        this.client = createHttpClient()
    }

    private suspend inline fun <reified T : YandexResponse> handleResponse(response: HttpResponse): YandexApiResponse {
        return try {
            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> {
                    val data = response.body<T>()
                    val errors = when (data) {
                        is YandexManageDeviceCapabilitiesStateResponse -> checkForErrorsInCapabilities(data.devices)
                        is YandexManageGroupCapabilitiesStateResponse -> checkForErrorsInCapabilities(data.devices)
                        else -> emptyList()
                    }
                    if (errors.isNotEmpty()) {
                        YandexApiResponse.Error(errors.first())
                    } else {
                        when (data) {
                            is YandexUserInfoResponse -> YandexApiResponse.SuccessUserInfo(data)
                            is YandexDeviceStateResponse -> YandexApiResponse.SuccessDeviceState(data)
                            is YandexDeviceGroupResponse -> YandexApiResponse.SuccessDeviceGroup(data)
                            is YandexManageDeviceCapabilitiesStateResponse -> YandexApiResponse.SuccessManageDeviceCapabilitiesState(data)
                            is YandexManageGroupCapabilitiesStateResponse -> YandexApiResponse.SuccessManageGroupCapabilitiesState(data)
                            else -> YandexApiResponse.Error(YandexErrorModelResponse(data.status, data.requestId, "Unknown data type"))
                        }
                    }
                }
                else -> {
                    val errorResponse = try {
                        response.body<YandexErrorModelResponse>()
                    } catch (e: Exception) {
                        YandexErrorModelResponse("error", "", "Exception: ${e.message}")
                    }
                    YandexApiResponse.Error(errorResponse)
                }
            }
        } catch (e: Exception) {
            YandexApiResponse.Error(YandexErrorModelResponse("error", "", "Exception: ${e.message}"))
        }
    }

    override suspend fun getUserInfo(): YandexApiResponse {
        return try {
            val response = client.get("$endpoint/v1.0/user/info")
            handleResponse<YandexUserInfoResponse>(response)
        } catch (e: Exception) {
            logAndReturnError("getUserInfo", e)
        }
    }

    override suspend fun getDeviceState(deviceId: String): YandexApiResponse {
        return try {
            val response = client.get("$endpoint/v1.0/devices/$deviceId")
            handleResponse<YandexDeviceStateResponse>(response)
        } catch (e: Exception) {
            logAndReturnError("getDeviceState", e)
        }
    }

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
        val error = YandexErrorModelResponse("error", "", "Exception: ${e.message}")
        logger{ error("$method - $error") }
        return YandexApiResponse.Error(error)
    }

    private fun checkForErrorsInCapabilities(devices: List<JsonObject>): List<YandexErrorModelResponse> {
        val errors = mutableListOf<YandexErrorModelResponse>()
        devices.forEach { device ->
            device.jsonObject["capabilities"]?.jsonArray?.forEach { capability ->
                val state = capability.jsonObject["state"]?.jsonObject
                val actionResult = state?.get("action_result")?.jsonObject
                if (actionResult != null) {
                    val status = actionResult["status"]?.jsonPrimitive?.content
                    if (status == "ERROR") {
                        val errorCode = actionResult["error_code"]?.jsonPrimitive?.content
                        val errorMessage = actionResult["error_message"]?.jsonPrimitive?.content
                        errors.add(YandexErrorModelResponse("error", "", "$errorCode: $errorMessage"))
                    }
                }
            }
        }
        return errors
    }
}