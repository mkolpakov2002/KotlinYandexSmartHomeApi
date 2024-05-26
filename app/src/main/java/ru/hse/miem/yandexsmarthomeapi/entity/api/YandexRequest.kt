package ru.hse.miem.yandexsmarthomeapi.entity.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

//JsonObject = DeviceActionsObject
@Serializable
data class YandexManageDeviceCapabilitiesStateRequest(
    val devices: List<JsonObject>
)

//JsonObject = CapabilityObject
@Serializable
data class YandexManageGroupCapabilitiesStateRequest(
     @SerialName("actions")
    val actions: List<JsonObject>
)