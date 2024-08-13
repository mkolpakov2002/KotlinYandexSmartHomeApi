package ru.hse.miem.yandexsmarthomeapi.entity.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Запрос на управление состояниями возможностей устройств
 *
 * @property devices Список устройств и их возможностей для управления
 * Каждый элемент списка - это JsonObject, представляющий DeviceActionsObject
 * {
 *   "id": String,
 *   "capabilities": [CapabilityObject]
 * }
 */
@Serializable
data class YandexManageDeviceCapabilitiesStateRequest(
    @SerialName("devices")
    val devices: List<JsonObject>
)

/**
 * Запрос на управление состояниями возможностей группы устройств
 *
 * @property actions Список действий для управления возможностями группы
 * Каждый элемент списка - это JsonObject, представляющий CapabilityObject
 * {
 *   "type": String,
 *   "state": {
 *     "instance": String,
 *     "value": Any
 *   }
 * }
 */
@Serializable
data class YandexManageGroupCapabilitiesStateRequest(
    @SerialName("actions")
    val actions: List<JsonObject>
)