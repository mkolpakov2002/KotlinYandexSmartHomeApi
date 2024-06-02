package ru.hse.miem.yandexsmarthomeapi.entity.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexDeviceStateResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageDeviceCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageGroupCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexUserInfoResponse
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.DeviceCapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.GroupCapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.SceneObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.SceneObjectWrapper

fun DeviceCapabilityObject.toYandexCapabilityActionJsonObject(): JsonObject {
    val capabilityType = type.type.code()
    val state = state?.toJson(type)

    return buildJsonObject {
        put("type", capabilityType)
        state?.let { put("state", it) }
    }
}

fun List<GroupCapabilityObject>.toYandexManageGroupCapabilitiesStateRequest(): YandexManageGroupCapabilitiesStateRequest {
    val actions = map { it.toYandexCapabilityActionJsonObject() }
    return YandexManageGroupCapabilitiesStateRequest(actions)
}

fun List<DeviceObject>.toYandexManageDeviceCapabilitiesStateRequest(): YandexManageDeviceCapabilitiesStateRequest {
    val devices = map { (it.toDeviceActionsObject() ).toJson() }
    return YandexManageDeviceCapabilitiesStateRequest(devices)
}

fun GroupCapabilityObject.toYandexCapabilityActionJsonObject(): JsonObject {
    val capabilityType = type.type.code()
    val state = state?.toJson(type)

    return buildJsonObject {
        put("type", capabilityType)
        state?.let { put("state", it) }
    }
}

fun YandexUserInfoResponse.toJson(): String {
    val responseJson = buildJsonObject {
        put("status", status)
        put("request_id", requestId)
        putJsonArray("rooms") {
            rooms.forEach { room ->
                add(room)
            }
        }
        putJsonArray("groups") {
            groups.forEach { group ->
                add(group)
            }
        }
        putJsonArray("devices") {
            devices.forEach { device ->
                add(device)
            }
        }
        putJsonArray("scenarios") {
            scenarios.forEach { scenario ->
                add(scenario)
            }
        }
        putJsonArray("households") {
            households.forEach { household ->
                add(household)
            }
        }
    }
    return json.encodeToString(JsonElement.serializer(), responseJson)
}

fun YandexUserInfoResponse.toSmartHomeInfo(): SmartHomeInfo {
    val roomObjects = rooms.map { roomJson ->
        RoomObject(
            id = roomJson["id"]?.jsonPrimitive?.content ?: "",
            name = roomJson["name"]?.jsonPrimitive?.content ?: "",
            devices = roomJson["devices"]?.jsonArray?.map { it.jsonPrimitive.content }
                ?.toMutableList()
                ?: mutableListOf(),
            householdId = roomJson["household_id"]?.jsonPrimitive?.content ?: ""
        )
    }.toMutableList()
    val groupObjects = groups.map { groupJson ->
        GroupObject(
            id = groupJson["id"]?.jsonPrimitive?.content ?: "",
            name = groupJson["name"]?.jsonPrimitive?.content ?: "",
            aliases = groupJson["aliases"]?.jsonArray?.map { it.jsonPrimitive.content }
                ?.toMutableList()
                ?: mutableListOf(),
            type = groupJson["type"]?.jsonPrimitive?.content?.let { DeviceTypeWrapper(it.codifiedEnum()) }
                ?: DeviceTypeWrapper(DeviceType.OTHER.codifiedEnum()),
            capabilities = groupJson["capabilities"]?.jsonArray?.map { it.jsonObject.toGroupCapabilityObject() }
                ?.toMutableList()
                ?: mutableListOf(),
            devices = groupJson["devices"]?.jsonArray?.map { it.jsonPrimitive.content }
                ?.toMutableList()
                ?: mutableListOf(),
            householdId = groupJson["household_id"]?.jsonPrimitive?.content ?: ""
        )
    }.toMutableList()
    val deviceObjects = devices.map { deviceJson ->
        deviceJson.toDeviceObject()
    }.toMutableList()
    val scenarioObjects = scenarios.map { scenarioJson ->
        ScenarioObject(
            id = scenarioJson["id"]?.jsonPrimitive?.content ?: "",
            name = scenarioJson["name"]?.jsonPrimitive?.content ?: "",
            isActive = scenarioJson["is_active"]?.jsonPrimitive?.boolean ?: false
        )
    }.toMutableList()
    val householdObjects = households.map { householdJson ->
        HouseholdObject(
            id = householdJson["id"]?.jsonPrimitive?.content ?: "",
            name = householdJson["name"]?.jsonPrimitive?.content ?: "",
            type = householdJson["type"]?.jsonPrimitive?.content ?: ""
        )
    }.toMutableList()
    return SmartHomeInfo(
        roomObjects,
        groupObjects,
        deviceObjects,
        scenarioObjects,
        householdObjects
    )
}

fun SmartHomeInfo.toYandexUserInfoResponse(): YandexUserInfoResponse {
    return YandexUserInfoResponse(
        rooms = rooms.map { it.toJson() },
        groups = groups.map { it.toJson() },
        devices = devices.map { it.toJson() },
        scenarios = scenarios.map { it.toJson() },
        households = households.map { it.toJson() },
        requestId = "",
        status = ""
    )
}

fun YandexDeviceStateResponse.toDeviceObject(): DeviceObject {
    return DeviceObject(
        id = id,
        name = name,
        aliases = aliases.toMutableList(),
        type = DeviceTypeWrapper(type.content.codifiedEnum()),
        externalId = externalId,
        skillId = skillId,
        householdId = "",
        room = room,
        groups = groups.toMutableList(),
        capabilities = capabilities.mapNotNull { it.toDeviceCapabilityObject() }.toMutableList(),
        properties = properties.mapNotNull { it.toDevicePropertyObject() }.toMutableList(),
        quasarInfo = quasarInfo?.toQuasarInfo()
    )
}