package ru.hse.miem.yandexsmarthomeapi.entity.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import ru.hse.miem.yandexsmarthomeapi.entity.common.*
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyObject

/**
 * Базовый интерфейс для ответов от Яндекс API
 */
@Serializable(with = YandexResponseSerializer::class)
sealed interface YandexResponse {
    val status: String
    @SerialName("request_id")
    val requestId: String
}

/**
 * Sealed класс для различных типов ответов от Яндекс API
 */
@Serializable(with = YandexApiResponseSerializer::class)
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
@Serializable(with = YandexErrorModelResponseSerializer::class)
data class YandexErrorModelResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val error: String? = null
): YandexResponse

/**
 * Модель ответа с информацией о пользователе от Яндекс API
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-user-info.html#format-response
 */
@Serializable(with = YandexUserInfoResponseSerializer::class)
data class YandexUserInfoResponse(
    @SerialName("request_id") override val requestId: String,
    @SerialName("status") override val status: String,
    @SerialName("rooms") val rooms: List<RoomObject>,
    @SerialName("groups") val groups: List<GroupObject>,
    @SerialName("devices") val devices: List<DeviceObject>,
    @SerialName("scenarios") val scenarios: List<ScenarioObject>,
    @SerialName("households") val households: List<HouseholdObject>
): YandexResponse

/**
 * Модель ответа с информацией о состоянии устройства от Яндекс API
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-device-info.html
 */
@Serializable(with = YandexDeviceStateResponseSerializer::class)
data class YandexDeviceStateResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val id: String,
    val name: String,
    val aliases: List<String>,
    val type: DeviceType,
    @SerialName("state") val state: DeviceState,
    val groups: List<String>,
    val room: String?,
    @SerialName("external_id") val externalId: String,
    @SerialName("skill_id") val skillId: String,
    val capabilities: List<CapabilityObject>,
    val properties: List<PropertyObject>,
    val quasarInfo: QuasarInfo? = null
) : YandexResponse

/**
 * Модель ответа на запрос управления возможностями устройства от Яндекс API
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-capabilities.html#output-structure
 */
@Serializable(with = YandexManageDeviceCapabilitiesStateResponseSerializer::class)
data class YandexManageDeviceCapabilitiesStateResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    @SerialName("devices") val devices: List<DeviceActionsResultObject>
) : YandexResponse

/**
 * Модель ответа с информацией о группе устройств от Яндекс API
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-group-device-info.html#output-structure
 */
@Serializable(with = YandexDeviceGroupResponseSerializer::class)
data class YandexDeviceGroupResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val id: String,
    val name: String,
    val aliases: List<String>,
    val type: DeviceType,
    val state: DeviceState,
    val capabilities: List<CapabilityObject>,
    val devices: List<GroupDeviceInfoObject>
): YandexResponse

/**
 * Модель ответа на запрос управления возможностями группы устройств от Яндекс API
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-group-capabilities.html#output-structure
 */
@Serializable(with = YandexManageGroupCapabilitiesStateResponseSerializer::class)
data class YandexManageGroupCapabilitiesStateResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val devices: List<DeviceActionsResultObject>
): YandexResponse