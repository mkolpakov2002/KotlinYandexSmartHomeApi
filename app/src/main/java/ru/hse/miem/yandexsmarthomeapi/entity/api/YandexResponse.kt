package ru.hse.miem.yandexsmarthomeapi.entity.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable
sealed interface YandexResponse {
    val status: String
    @SerialName("request_id")
    val requestId: String
}

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

@Serializable
data class YandexErrorModelResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val error: String? = null
): YandexResponse

/**
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-user-info.html#format-response
 */
/**
 * RoomObject
 * GroupObject
 * DeviceObject
 * ScenarioObject
 * HouseholdObject
 */
@Serializable
data class YandexUserInfoResponse(
    @SerialName("request_id") override val requestId: String,
    @SerialName("status") override val status: String,
    @SerialName("rooms") val rooms: List<JsonObject>,
    @SerialName("groups") val groups: List<JsonObject>,
    @SerialName("devices") val devices: List<JsonObject>,
    @SerialName("scenarios") val scenarios: List<JsonObject>,
    @SerialName("households") val households: List<JsonObject>
): YandexResponse

/**
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-device-info.html
 * type: DeviceType,
 * state: DeviceState,
 * capabilities: List<CapabilityObject>,
 * properties: List<PropertyObject>
 */
@Serializable
data class YandexDeviceStateResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val id: String,
    val name: String,
    val aliases: List<String>,
    val type: JsonPrimitive,
    @SerialName("state") val state: JsonPrimitive,
    val groups: List<String>,
    val room: String?,
    @SerialName("external_id") val externalId: String,
    @SerialName("skill_id") val skillId: String,
    val capabilities: List<JsonObject>,
    val properties: List<JsonObject>,
    val quasarInfo: JsonObject? = null
) : YandexResponse

/**
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-capabilities.html#output-structure
 * DeviceActionsResultObject
 */
@Serializable
data class YandexManageDeviceCapabilitiesStateResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    @SerialName("devices") val devices: List<JsonObject>
) : YandexResponse

/**
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-group-device-info.html#output-structure
 *     val type: DeviceType,
 *     val state: DeviceState,
 *     val capabilities: List<CapabilityObject>,
 *     val devices: List<GroupDeviceInfoObject>
 */
@Serializable
data class YandexDeviceGroupResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val id: String,
    val name: String,
    val aliases: List<String>,
    val type: JsonObject,
    val state: JsonObject,
    val capabilities: List<JsonObject>,
    val devices: List<JsonObject>
): YandexResponse

/**
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/platform-group-capabilities.html#output-structure
 * devices: List<DeviceActionsResultObject>
 */
@Serializable
data class YandexManageGroupCapabilitiesStateResponse(
    @SerialName("status") override val status: String,
    @SerialName("request_id") override val requestId: String,
    val devices: List<JsonObject>
): YandexResponse