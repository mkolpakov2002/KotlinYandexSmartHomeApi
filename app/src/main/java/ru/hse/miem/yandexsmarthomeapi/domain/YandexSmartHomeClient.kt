package ru.hse.miem.yandexsmarthomeapi.domain

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
class YandexSmartHomeClient(
    private val endpoint: String,
    private val bearerToken: String
) : YandexSmartHomeApi {

    /**
     * HTTP клиент для отправки запросов к API Яндекс Умного Дома.
     */
    var client = HttpClient(CIO) {
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
                        YandexErrorModelResponse(Status.ERROR.code, "", e.message?:"Unknown error")
                    }
                    throw ResponseException(response, errorResponse.error ?: "Unknown error")
                }
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    /**
     * Обрабатывает HTTP ответ сервера, конвертируя его в определенный тип API ответа.
     *
     * @param T Ожидаемый тип данных в успешном ответе.
     * @param response HTTP ответ от сервера.
     * @return Объект API ответа.
     */
    private suspend inline fun <reified T : YandexResponse> handleResponse(response: HttpResponse): YandexApiResponse {
        return when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.Created -> {
                val data = response.body<T>()
                when (data) {
                    is YandexUserInfoResponse -> YandexApiResponse.SuccessUserInfo(data)
                    is YandexDeviceStateResponse -> YandexApiResponse.SuccessDeviceState(data)
                    is YandexDeviceGroupResponse -> YandexApiResponse.SuccessDeviceGroup(data)
                    is YandexManageDeviceCapabilitiesStateResponse -> YandexApiResponse.SuccessManageDeviceCapabilitiesState(data)
                    is YandexManageGroupCapabilitiesStateResponse -> YandexApiResponse.SuccessManageGroupCapabilitiesState(data)
                    else -> YandexApiResponse.Error(YandexErrorModelResponse(data.status, data.requestId, "Unknown data type"))
                }
            }
            else -> YandexApiResponse.Error(response.body())
        }
    }

    /**
     * Получает полную информацию об умном доме пользователя.
     *
     * @return Ответ API с информацией о умном доме.
     */
    override suspend fun getUserInfo(): YandexApiResponse {
        val response = client.get("$endpoint/v1.0/user/info")
        return handleResponse<YandexUserInfoResponse>(response)
    }

    /**
     * Получает информацию о состоянии указанного устройства.
     *
     * @param deviceId Идентификатор устройства.
     * @return Ответ API с состоянием устройства.
     */
    override suspend fun getDeviceState(deviceId: String): YandexApiResponse {
        val response = client.get("$endpoint/v1.0/devices/$deviceId")
        return handleResponse<YandexDeviceStateResponse>(response)
    }

    /**
     * Управляет умениями указанных устройств.
     *
     * @param request Запрос на изменение состояния умений устройств.
     * @return Ответ API об успешности выполнения операции.
     */
    override suspend fun manageDeviceCapabilitiesState(request: YandexManageDeviceCapabilitiesStateRequest): YandexApiResponse {
        val response = client.post("$endpoint/v1.0/devices/actions") {
            setBody(request)
        }
        return handleResponse<YandexManageDeviceCapabilitiesStateResponse>(response)
    }

    /**
     * Управляет умениями устройств в указанной группе.
     *
     * @param groupId Идентификатор группы устройств.
     * @param request Запрос на изменение состояния умений группы устройств.
     * @return Ответ API об успешности выполнения операции.
     */
    override suspend fun manageGroupCapabilitiesState(groupId: String, request: YandexManageGroupCapabilitiesStateRequest): YandexApiResponse {
        val response = client.post("$endpoint/v1.0/groups/${groupId}/actions") {
            setBody(request)
        }
        return handleResponse<YandexManageGroupCapabilitiesStateResponse>(response)
    }

    /**
     * Получает информацию о состоянии указанной группы устройств.
     *
     * @param groupId Идентификатор группы устройств.
     * @return Ответ API с состоянием группы устройств.
     */
    override suspend fun getDeviceGroup(groupId: String): YandexApiResponse {
        val response = client.get("$endpoint/v1.0/groups/$groupId")
        return handleResponse<YandexDeviceGroupResponse>(response)
    }
}