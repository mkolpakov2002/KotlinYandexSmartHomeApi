package ru.hse.miem.yandexsmarthomeapi.entity.api

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceActionsObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceActionsResultObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceState
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceType
import ru.hse.miem.yandexsmarthomeapi.entity.common.GroupDeviceInfoObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.GroupObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.HouseholdObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.QuasarInfo
import ru.hse.miem.yandexsmarthomeapi.entity.common.RoomObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.ScenarioObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyObject

object YandexResponseSerializer : KSerializer<YandexResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("YandexResponse") {
        element<String>("status")
        element<String>("request_id")
    }

    override fun serialize(encoder: Encoder, value: YandexResponse) {
        val jsonElement = when (value) {
            is YandexUserInfoResponse -> Json.encodeToJsonElement(YandexUserInfoResponseSerializer, value)
            is YandexDeviceStateResponse -> Json.encodeToJsonElement(YandexDeviceStateResponseSerializer, value)
            is YandexManageDeviceCapabilitiesStateResponse -> Json.encodeToJsonElement(YandexManageDeviceCapabilitiesStateResponseSerializer, value)
            is YandexDeviceGroupResponse -> Json.encodeToJsonElement(YandexDeviceGroupResponseSerializer, value)
            is YandexManageGroupCapabilitiesStateResponse -> Json.encodeToJsonElement(YandexManageGroupCapabilitiesStateResponseSerializer, value)
            is YandexErrorModelResponse -> Json.encodeToJsonElement(YandexErrorModelResponseSerializer, value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): YandexResponse {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when {
            "rooms" in jsonObject -> Json.decodeFromJsonElement(YandexUserInfoResponseSerializer, jsonElement)
            "capabilities" in jsonObject -> Json.decodeFromJsonElement(YandexDeviceStateResponseSerializer, jsonElement)
            "devices" in jsonObject && "capabilities" !in jsonObject -> Json.decodeFromJsonElement(YandexManageDeviceCapabilitiesStateResponseSerializer, jsonElement)
            "devices" in jsonObject && "capabilities" in jsonObject -> Json.decodeFromJsonElement(YandexDeviceGroupResponseSerializer, jsonElement)
            "error" in jsonObject -> Json.decodeFromJsonElement(YandexErrorModelResponseSerializer, jsonElement)
            else -> throw IllegalArgumentException("Unknown YandexResponse type")
        }
    }
}

object YandexApiResponseSerializer : KSerializer<YandexApiResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("YandexApiResponse")

    override fun serialize(encoder: Encoder, value: YandexApiResponse) {
        val jsonElement = when (value) {
            is YandexApiResponse.SuccessUserInfo -> Json.encodeToJsonElement(YandexUserInfoResponseSerializer, value.data)
            is YandexApiResponse.SuccessDeviceState -> Json.encodeToJsonElement(YandexDeviceStateResponseSerializer, value.data)
            is YandexApiResponse.SuccessDeviceGroup -> Json.encodeToJsonElement(YandexDeviceGroupResponseSerializer, value.data)
            is YandexApiResponse.SuccessManageDeviceCapabilitiesState -> Json.encodeToJsonElement(YandexManageDeviceCapabilitiesStateResponseSerializer, value.data)
            is YandexApiResponse.SuccessManageGroupCapabilitiesState -> Json.encodeToJsonElement(YandexManageGroupCapabilitiesStateResponseSerializer, value.data)
            is YandexApiResponse.Error -> Json.encodeToJsonElement(YandexErrorModelResponseSerializer, value.error)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): YandexApiResponse {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when {
            "rooms" in jsonObject -> YandexApiResponse.SuccessUserInfo(Json.decodeFromJsonElement(YandexUserInfoResponseSerializer, jsonElement))
            "capabilities" in jsonObject -> YandexApiResponse.SuccessDeviceState(Json.decodeFromJsonElement(YandexDeviceStateResponseSerializer, jsonElement))
            "devices" in jsonObject && "capabilities" !in jsonObject -> YandexApiResponse.SuccessManageDeviceCapabilitiesState(Json.decodeFromJsonElement(YandexManageDeviceCapabilitiesStateResponseSerializer, jsonElement))
            "devices" in jsonObject && "capabilities" in jsonObject -> YandexApiResponse.SuccessDeviceGroup(Json.decodeFromJsonElement(YandexDeviceGroupResponseSerializer, jsonElement))
            "error" in jsonObject -> YandexApiResponse.Error(Json.decodeFromJsonElement(YandexErrorModelResponseSerializer, jsonElement))
            else -> throw IllegalArgumentException("Unknown YandexApiResponse type")
        }
    }
}

object YandexErrorModelResponseSerializer : KSerializer<YandexErrorModelResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("YandexErrorModelResponse") {
        element<String>("status")
        element<String>("request_id")
        element<String?>("error")
    }

    override fun serialize(encoder: Encoder, value: YandexErrorModelResponse) {
        val jsonElement = buildJsonObject {
            put("status", value.status)
            put("request_id", value.requestId)
            value.error?.let { put("error", it) }
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): YandexErrorModelResponse {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return YandexErrorModelResponse(
            status = jsonObject["status"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing status"),
            requestId = jsonObject["request_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing request_id"),
            error = jsonObject["error"]?.jsonPrimitive?.contentOrNull
        )
    }
}

object YandexUserInfoResponseSerializer : KSerializer<YandexUserInfoResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("YandexUserInfoResponse") {
        element<String>("request_id")
        element<String>("status")
        element<List<RoomObject>>("rooms")
        element<List<GroupObject>>("groups")
        element<List<DeviceObject>>("devices")
        element<List<ScenarioObject>>("scenarios")
        element<List<HouseholdObject>>("households")
    }

    override fun serialize(encoder: Encoder, value: YandexUserInfoResponse) {
        val jsonElement = buildJsonObject {
            put("request_id", value.requestId)
            put("status", value.status)
            put("rooms", Json.encodeToJsonElement(value.rooms))
            put("groups", Json.encodeToJsonElement(value.groups))
            put("devices", Json.encodeToJsonElement(value.devices))
            put("scenarios", Json.encodeToJsonElement(value.scenarios))
            put("households", Json.encodeToJsonElement(value.households))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): YandexUserInfoResponse {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return YandexUserInfoResponse(
            requestId = jsonObject["request_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing request_id"),
            status = jsonObject["status"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing status"),
            rooms = Json.decodeFromJsonElement(jsonObject["rooms"]!!),
            groups = Json.decodeFromJsonElement(jsonObject["groups"]!!),
            devices = Json.decodeFromJsonElement(jsonObject["devices"]!!),
            scenarios = Json.decodeFromJsonElement(jsonObject["scenarios"]!!),
            households = Json.decodeFromJsonElement(jsonObject["households"]!!)
        )
    }
}

object YandexDeviceStateResponseSerializer : KSerializer<YandexDeviceStateResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("YandexDeviceStateResponse") {
        element<String>("status")
        element<String>("request_id")
        element<String>("id")
        element<String>("name")
        element<List<String>>("aliases")
        element<DeviceType>("type")
        element<DeviceState>("state")
        element<List<String>>("groups")
        element<String?>("room")
        element<String>("external_id")
        element<String>("skill_id")
        element<List<CapabilityObject>>("capabilities")
        element<List<PropertyObject>>("properties")
        element<QuasarInfo?>("quasarInfo")
    }

    override fun serialize(encoder: Encoder, value: YandexDeviceStateResponse) {
        val jsonElement = buildJsonObject {
            put("status", value.status)
            put("request_id", value.requestId)
            put("id", value.id)
            put("name", value.name)
            put("aliases", Json.encodeToJsonElement(value.aliases))
            put("type", Json.encodeToJsonElement(value.type))
            put("state", Json.encodeToJsonElement(value.state))
            put("groups", Json.encodeToJsonElement(value.groups))
            value.room?.let { put("room", it) }
            put("external_id", value.externalId)
            put("skill_id", value.skillId)
            put("capabilities", Json.encodeToJsonElement(value.capabilities))
            put("properties", Json.encodeToJsonElement(value.properties))
            value.quasarInfo?.let { put("quasarInfo", Json.encodeToJsonElement(it)) }
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): YandexDeviceStateResponse {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return YandexDeviceStateResponse(
            status = jsonObject["status"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing status"),
            requestId = jsonObject["request_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing request_id"),
            id = jsonObject["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing id"),
            name = jsonObject["name"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing name"),
            aliases = Json.decodeFromJsonElement(jsonObject["aliases"]!!),
            type = Json.decodeFromJsonElement(jsonObject["type"]!!),
            state = Json.decodeFromJsonElement(jsonObject["state"]!!),
            groups = Json.decodeFromJsonElement(jsonObject["groups"]!!),
            room = jsonObject["room"]?.jsonPrimitive?.contentOrNull,
            externalId = jsonObject["external_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing external_id"),
            skillId = jsonObject["skill_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing skill_id"),
            capabilities = Json.decodeFromJsonElement(jsonObject["capabilities"]!!),
            properties = Json.decodeFromJsonElement(jsonObject["properties"]!!),
            quasarInfo = jsonObject["quasarInfo"]?.let { Json.decodeFromJsonElement(it) }
        )
    }
}

object YandexManageDeviceCapabilitiesStateResponseSerializer : KSerializer<YandexManageDeviceCapabilitiesStateResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("YandexManageDeviceCapabilitiesStateResponse") {
        element<String>("status")
        element<String>("request_id")
        element<List<DeviceActionsResultObject>>("devices")
    }

    override fun serialize(encoder: Encoder, value: YandexManageDeviceCapabilitiesStateResponse) {
        val jsonElement = buildJsonObject {
            put("status", value.status)
            put("request_id", value.requestId)
            put("devices", Json.encodeToJsonElement(value.devices))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): YandexManageDeviceCapabilitiesStateResponse {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return YandexManageDeviceCapabilitiesStateResponse(
            status = jsonObject["status"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing status"),
            requestId = jsonObject["request_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing request_id"),
            devices = Json.decodeFromJsonElement(jsonObject["devices"]!!)
        )
    }
}

object YandexDeviceGroupResponseSerializer : KSerializer<YandexDeviceGroupResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("YandexDeviceGroupResponse") {
        element<String>("status")
        element<String>("request_id")
        element<String>("id")
        element<String>("name")
        element<List<String>>("aliases")
        element<DeviceType>("type")
        element<DeviceState>("state")
        element<List<CapabilityObject>>("capabilities")
        element<List<GroupDeviceInfoObject>>("devices")
    }

    override fun serialize(encoder: Encoder, value: YandexDeviceGroupResponse) {
        val jsonElement = buildJsonObject {
            put("status", value.status)
            put("request_id", value.requestId)
            put("id", value.id)
            put("name", value.name)
            put("aliases", Json.encodeToJsonElement(value.aliases))
            put("type", Json.encodeToJsonElement(value.type))
            put("state", Json.encodeToJsonElement(value.state))
            put("capabilities", Json.encodeToJsonElement(value.capabilities))
            put("devices", Json.encodeToJsonElement(value.devices))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): YandexDeviceGroupResponse {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return YandexDeviceGroupResponse(
            status = jsonObject["status"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing status"),
            requestId = jsonObject["request_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing request_id"),
            id = jsonObject["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing id"),
            name = jsonObject["name"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing name"),
            aliases = Json.decodeFromJsonElement(jsonObject["aliases"]!!),
            type = Json.decodeFromJsonElement(jsonObject["type"]!!),
            state = Json.decodeFromJsonElement(jsonObject["state"]!!),
            capabilities = Json.decodeFromJsonElement(jsonObject["capabilities"]!!),
            devices = Json.decodeFromJsonElement(jsonObject["devices"]!!)
        )
    }
}

object YandexManageGroupCapabilitiesStateResponseSerializer : KSerializer<YandexManageGroupCapabilitiesStateResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("YandexManageGroupCapabilitiesStateResponse") {
        element<String>("status")
        element<String>("request_id")
        element<List<DeviceActionsResultObject>>("devices")
    }

    override fun serialize(encoder: Encoder, value: YandexManageGroupCapabilitiesStateResponse) {
        val jsonElement = buildJsonObject {
            put("status", value.status)
            put("request_id", value.requestId)
            put("devices", Json.encodeToJsonElement(value.devices))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): YandexManageGroupCapabilitiesStateResponse {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return YandexManageGroupCapabilitiesStateResponse(
            status = jsonObject["status"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing status"),
            requestId = jsonObject["request_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing request_id"),
            devices = Json.decodeFromJsonElement(jsonObject["devices"]!!)
        )
    }
}

object YandexManageDeviceCapabilitiesStateRequestSerializer : KSerializer<YandexManageDeviceCapabilitiesStateRequest> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("YandexManageDeviceCapabilitiesStateRequest") {
        element<List<DeviceActionsObject>>("devices")
    }

    override fun serialize(encoder: Encoder, value: YandexManageDeviceCapabilitiesStateRequest) {
        val jsonElement = buildJsonObject {
            put("devices", Json.encodeToJsonElement(value.devices))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): YandexManageDeviceCapabilitiesStateRequest {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return YandexManageDeviceCapabilitiesStateRequest(
            devices = Json.decodeFromJsonElement(jsonObject["devices"]!!)
        )
    }
}

object YandexManageGroupCapabilitiesStateRequestSerializer : KSerializer<YandexManageGroupCapabilitiesStateRequest> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("YandexManageGroupCapabilitiesStateRequest") {
        element<List<CapabilityObject>>("actions")
    }

    override fun serialize(encoder: Encoder, value: YandexManageGroupCapabilitiesStateRequest) {
        val jsonElement = buildJsonObject {
            put("actions", Json.encodeToJsonElement(value.actions))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): YandexManageGroupCapabilitiesStateRequest {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return YandexManageGroupCapabilitiesStateRequest(
            actions = Json.decodeFromJsonElement(jsonObject["actions"]!!)
        )
    }
}