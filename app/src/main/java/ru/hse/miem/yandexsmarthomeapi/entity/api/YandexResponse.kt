package ru.hse.miem.yandexsmarthomeapi.entity.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Базовый интерфейс для ответов от Яндекс API
 */
@Serializable
sealed interface YandexResponse {
    val status: String
    @SerialName("request_id")
    val requestId: String
}

/**
 * Sealed класс для различных типов ответов от Яндекс API
 */
@Serializable
sealed class YandexApiResponse {
    @Serializable
    data class SuccessUserInfo(val data: YandexUserInfoResponse) : YandexApiResponse()
    @Serializable
    data class SuccessDeviceState(val data: YandexDeviceStateResponse) : YandexApiResponse()
    @Serializable
    data class SuccessDeviceGroup(val data: YandexDeviceGroupResponse) : YandexApiResponse()
    @Serializable
    data class SuccessManageDeviceCapabilitiesState(val data: YandexManageDeviceCapabilitiesStateResponse) : YandexApiResponse()
    @Serializable
    data class SuccessManageGroupCapabilitiesState(val data: YandexManageGroupCapabilitiesStateResponse) : YandexApiResponse()
    @Serializable
    data class Error(val error: YandexErrorModelResponse) : YandexApiResponse()
}

/**
 * Модель ответа с ошибкой от Яндекс API
 */
@Serializable
data class YandexErrorModelResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val error: String? = null
): YandexResponse

/**
 * Модель ответа с информацией о пользователе от Яндекс API
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-user-info.html#format-response
 */
@Serializable
data class YandexUserInfoResponse(
    @SerialName("request_id") override val requestId: String,
    @SerialName("status") override val status: String,
    @SerialName("rooms") val rooms: List<JsonObject>, // RoomObject
    @SerialName("groups") val groups: List<JsonObject>, // GroupObject
    @SerialName("devices") val devices: List<JsonObject>, // DeviceObject
    @SerialName("scenarios") val scenarios: List<JsonObject>, // ScenarioObject
    @SerialName("households") val households: List<JsonObject> // HouseholdObject
): YandexResponse

/**
 * Модель ответа с информацией о состоянии устройства от Яндекс API
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-device-info.html
 */
@Serializable
data class YandexDeviceStateResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val id: String,
    val name: String,
    val aliases: List<String>,
    val type: JsonPrimitive, // DeviceType
    @SerialName("state") val state: JsonPrimitive, // DeviceState
    val groups: List<String>,
    val room: String?,
    @SerialName("external_id") val externalId: String,
    @SerialName("skill_id") val skillId: String,
    val capabilities: List<JsonObject>, // CapabilityObject
    val properties: List<JsonObject>, // PropertyObject
    val quasarInfo: JsonObject? = null
) : YandexResponse

/**
 * Модель ответа на запрос управления возможностями устройства от Яндекс API
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-capabilities.html#output-structure
 */
@Serializable
data class YandexManageDeviceCapabilitiesStateResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    @SerialName("devices") val devices: List<JsonObject> // DeviceActionsResultObject
) : YandexResponse

/**
 * Модель ответа с информацией о группе устройств от Яндекс API
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-group-device-info.html#output-structure
 */
@Serializable
data class YandexDeviceGroupResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val id: String,
    val name: String,
    val aliases: List<String>,
    val type: JsonObject, // DeviceType
    val state: JsonObject, // DeviceState
    val capabilities: List<JsonObject>, // CapabilityObject
    val devices: List<JsonObject> // GroupDeviceInfoObject
): YandexResponse

/**
 * Модель ответа на запрос управления возможностями группы устройств от Яндекс API
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-group-capabilities.html#output-structure
 */
@Serializable
data class YandexManageGroupCapabilitiesStateResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val devices: List<JsonObject> // DeviceActionsResultObject
): YandexResponse