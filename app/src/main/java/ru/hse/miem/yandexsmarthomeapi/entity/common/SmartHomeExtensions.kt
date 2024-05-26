package ru.hse.miem.yandexsmarthomeapi.entity.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.Capability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorModelWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorScene
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueInteger
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueObjectHSV
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueObjectScene
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.DeviceCapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.GroupCapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.HSVObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityMode
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityModeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.Range
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectDataValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.Scene
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.SceneObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.SceneObjectWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.TemperatureK
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityStateObjectDataValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamCapabilityParameterObjectStreamProtocolWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.DevicePropertyObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.EventObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.EventObjectValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.EventObjectValueWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.EventPropertyParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.EventPropertyState
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.EventPropertyStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.FloatObjectValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.FloatPropertyParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.FloatPropertyState
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.FloatPropertyStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.MeasurementUnit
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.Property
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyFunction
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyFunctionWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyType
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyTypeWrapper

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
    encodeDefaults = true
}

fun DeviceObject.toDeviceActionsObject(): DeviceActionsObject {
    return DeviceActionsObject(
        id = this.id,
        actions = this.capabilities.map { capability ->
            CapabilityObject(
                type = capability.type,
                state = capability.state
            )
        }.toMutableList()
    )
}

fun DeviceActionsObject.toJson(): JsonObject {
    return buildJsonObject {
        put("id", id)
        putJsonArray("actions") {
            actions.forEach {
                add(it.toJson())
            }
        }
    }
}

fun JsonObject.toSmartHomeInfo(): SmartHomeInfo {

    val rooms = jsonObject["rooms"]?.jsonArray?.map { it.jsonObject } ?: emptyList()
    val groups = jsonObject["groups"]?.jsonArray?.map { it.jsonObject } ?: emptyList()
    val devices = jsonObject["devices"]?.jsonArray?.map { it.jsonObject } ?: emptyList()
    val scenarios = jsonObject["scenarios"]?.jsonArray?.map { it.jsonObject } ?: emptyList()
    val households = jsonObject["households"]?.jsonArray?.map { it.jsonObject } ?: emptyList()

    val roomObjects = rooms.map { roomJson ->
        RoomObject(
            id = roomJson["id"]?.jsonPrimitive?.content ?: "",
            name = roomJson["name"]?.jsonPrimitive?.content ?: "",
            devices = roomJson["devices"]?.jsonArray?.map { it.jsonPrimitive.content }
                ?.toMutableList() ?: mutableListOf(),
            householdId = roomJson["household_id"]?.jsonPrimitive?.content ?: ""
        )
    }.toMutableList()
    val groupObjects = groups.map { groupJson ->
        GroupObject(
            id = groupJson["id"]?.jsonPrimitive?.content ?: "",
            name = groupJson["name"]?.jsonPrimitive?.content ?: "",
            aliases = groupJson["aliases"]?.jsonArray?.map { it.jsonPrimitive.content }?.toMutableList()
                ?: mutableListOf(),
            type = groupJson["type"]?.jsonPrimitive?.content?.let { DeviceTypeWrapper(it.codifiedEnum()) }
                ?: DeviceTypeWrapper(DeviceType.OTHER.codifiedEnum()),
            capabilities = groupJson["capabilities"]?.jsonArray?.map { it.jsonObject.toGroupCapabilityObject() }?.toMutableList()
                ?: mutableListOf(),
            devices = groupJson["devices"]?.jsonArray?.map { it.jsonPrimitive.content }?.toMutableList()
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
    return SmartHomeInfo(roomObjects, groupObjects, deviceObjects, scenarioObjects, householdObjects)
}

fun SmartHomeInfo.toJson(): JsonObject {
    val roomsJson = buildJsonArray {
        rooms.forEach { room ->
            add(room.toJson())
        }
    }
    val groupsJson = buildJsonArray {
        groups.forEach { group ->
            add(group.toJson())
        }
    }
    val devicesJson = buildJsonArray {
        devices.forEach { device ->
            add(device.toJson())
        }
    }
    val scenariosJson = buildJsonArray {
        scenarios.forEach { scenario ->
            add(scenario.toJson())
        }
    }
    val householdsJson = buildJsonArray {
        households.forEach { household ->
            add(household.toJson())
        }
    }
    return buildJsonObject {
        put("rooms", roomsJson)
        put("groups", groupsJson)
        put("devices", devicesJson)
        put("scenarios", scenariosJson)
        put("households", householdsJson)
    }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class)
fun DeviceObject.toJson(): JsonObject {
    return buildJsonObject {
        put("id", id)
        put("name", name)
        putJsonArray("aliases") {
            aliases.forEach { alias ->
                add(JsonPrimitive(alias))
            }
        }
        put("type", type.type.code())
        put("external_id", externalId)
        put("skill_id", skillId)
        put("household_id", householdId)
        put("room", room)
        putJsonArray("groups") {
            groups.forEach { group ->
                add(JsonPrimitive(group))
            }
        }
        put("capabilities", buildJsonArray {
            capabilities.forEach { capability ->
                add(capability.toJson())
            }
        })
        put("properties", buildJsonArray {
            properties.forEach { property ->
                add(property.toJson())
            }
        })
        quasarInfo?.let { put("quasar_info", it.toJson()) }
    }
}

fun JsonObject.toDeviceObject(): DeviceObject {
    return DeviceObject(
        id = this["id"]?.jsonPrimitive?.content ?: "",
        name = this["name"]?.jsonPrimitive?.content ?: "",
        aliases = this["aliases"]?.jsonArray?.map { it.jsonPrimitive.content }?.toMutableList()
            ?: mutableListOf(),
        type = this["type"]?.jsonPrimitive?.content?.let { DeviceTypeWrapper(it.codifiedEnum()) }
            ?: DeviceTypeWrapper(DeviceType.OTHER.codifiedEnum()),
        externalId = this["external_id"]?.jsonPrimitive?.content ?: "",
        skillId = this["skill_id"]?.jsonPrimitive?.content ?: "",
        householdId = this["household_id"]?.jsonPrimitive?.content ?: "",
        room = this["room"]?.jsonPrimitive?.contentOrNull,
        groups = this["groups"]?.jsonArray?.map { it.jsonPrimitive.content }?.toMutableList()
            ?: mutableListOf(),
        capabilities = this["capabilities"]?.jsonArray?.mapNotNull { it.jsonObject.toDeviceCapabilityObject() }?.toMutableList()
            ?: mutableListOf(),
        properties = this["properties"]?.jsonArray?.mapNotNull { it.jsonObject.toDevicePropertyObject() }?.toMutableList()
            ?: mutableListOf(),
        quasarInfo = this["quasar_info"]?.jsonObject?.toQuasarInfo()
    )
}

fun JsonObject?.toQuasarInfo(): QuasarInfo? {
    if (this == null) return null
    return QuasarInfo(
        deviceId = this["device_id"]?.jsonPrimitive?.content ?: "",
        platform = this["platform"]?.jsonPrimitive?.content ?: ""
    )
}

fun QuasarInfo.toJson(): JsonObject {
    return buildJsonObject {
        put("device_id", deviceId)
        put("platform", platform)
    }
}

fun RoomObject.toJson(): JsonObject {
    return buildJsonObject {
        put("id", id)
        put("name", name)
        putJsonArray("devices") {
            devices.forEach { deviceId ->
                add(JsonPrimitive(deviceId))
            }
        }
        put("household_id", householdId)
    }
}

fun GroupObject.toJson(): JsonObject {
    return buildJsonObject {
        put("id", id)
        put("name", name)
        putJsonArray("aliases") {
            aliases.forEach { alias ->
                add(JsonPrimitive(alias))
            }
        }
        put("type", type.type.code())
        putJsonArray("capabilities") {
            capabilities.forEach { capability ->
                add(capability.toJson())
            }
        }
        putJsonArray("devices") {
            devices.forEach { deviceId ->
                add(JsonPrimitive(deviceId))
            }
        }
        put("household_id", householdId)
    }
}

fun JsonObject.toDeviceCapabilityObject(): DeviceCapabilityObject? {
    val type = CapabilityTypeWrapper(this["type"]?.jsonPrimitive?.content!!.codifiedEnum())
    val parameters = this["parameters"]?.jsonObject?.toCapabilityParameterObject(type)
    val state = if (this["state"] is JsonNull)
        null
    else
        this["state"]?.jsonObject?.toCapabilityStateObjectData(type)
    return if (parameters != null) {
        DeviceCapabilityObject(
            type = type,
            reportable = this["reportable"]?.jsonPrimitive?.boolean ?: false,
            retrievable = this["retrievable"]?.jsonPrimitive?.boolean ?: false,
            parameters = parameters,
            state = state,
            lastUpdated = this["last_updated"]?.jsonPrimitive?.float ?: 0.0f
        )
    } else {
        null
    }
}

fun DeviceCapabilityObject.toJson(): JsonObject {
    val type = this.type
    val parametersJson = parameters.toJson(type)
    val stateJson = state?.toJson(type)

    return buildJsonObject {
        put("type", type.type.code())
        put("reportable", reportable)
        put("retrievable", retrievable)
        put("parameters", parametersJson)
        stateJson?.let { put("state", it) }
        put("last_updated", lastUpdated)
    }
}

fun JsonObject?.toColorSettingCapabilityParameterObject(): ColorSettingCapabilityParameterObject? {
    if (this == null) return null
    return ColorSettingCapabilityParameterObject(
        colorModel = this["color_model"]?.jsonPrimitive?.contentOrNull?.let { ColorModelWrapper(it.codifiedEnum()) },
        temperatureK = this["temperature_k"]?.jsonObject?.toTemperatureK(),
        colorScene = this["color_scene"]?.jsonObject?.toColorScene()
    )
}

fun JsonObject?.toTemperatureK(): TemperatureK? {
    if (this == null) return null
    return TemperatureK(
        min = this["min"]?.jsonPrimitive?.int ?: 0,
        max = this["max"]?.jsonPrimitive?.int ?: 0
    )
}

fun JsonObject?.toColorScene(): ColorScene? {
    if (this == null) return null
    return ColorScene(scenes = this["scenes"]?.jsonArray?.map { sceneJson ->
        Scene(id = SceneObjectWrapper(
            sceneJson.jsonObject["id"]?.jsonPrimitive?.content?.codifiedEnum()
                ?: SceneObject.ALARM.codifiedEnum()))
    } ?: emptyList())
}

fun JsonObject?.toOnOffCapabilityParameterObject(): OnOffCapabilityParameterObject? {
    if (this == null) return null
    return OnOffCapabilityParameterObject(
        split = this["split"]?.jsonPrimitive?.boolean ?: false
    )
}

fun JsonObject?.toModeCapabilityParameterObject(): ModeCapabilityParameterObject? {
    if (this == null) return null
    return ModeCapabilityParameterObject(
        instance = this["instance"]?.jsonPrimitive?.content?.let { ModeCapabilityInstanceWrapper(it.codifiedEnum()) }
            ?: ModeCapabilityInstanceWrapper(ModeCapability.THERMOSTAT.codifiedEnum()),
        modes = this["modes"]?.jsonArray?.map {
            ModeObject(value = ModeCapabilityModeWrapper(it.jsonObject["value"]?.jsonPrimitive?.content?.codifiedEnum()
                ?: ModeCapabilityMode.AUTO.codifiedEnum()))
        } ?: emptyList()
    )
}

fun JsonObject?.toRangeCapabilityParameterObject(): RangeCapabilityParameterObject? {
    if (this == null) return null
    return RangeCapabilityParameterObject(
        instance = this["instance"]?.jsonPrimitive?.content?.let { RangeCapabilityWrapper(it.codifiedEnum()) }
            ?: RangeCapabilityWrapper(RangeCapability.BRIGHTNESS.codifiedEnum()),
        randomAccess = this["random_access"]?.jsonPrimitive?.boolean ?: false,
        range = this["range"]?.jsonObject?.toRange(),
        looped = this["looped"]?.jsonPrimitive?.boolean
    )
}

fun JsonObject?.toRange(): Range? {
    if (this == null) return null
    return Range(
        min = this["min"]?.jsonPrimitive?.float ?: 0.0f,
        max = this["max"]?.jsonPrimitive?.float ?: 0.0f,
        precision = this["precision"]?.jsonPrimitive?.float ?: 0.0f
    )
}

fun JsonObject?.toToggleCapabilityParameterObject(): ToggleCapabilityParameterObject? {
    if (this == null) return null
    return ToggleCapabilityParameterObject(
        instance = this["instance"]?.jsonPrimitive?.content?.let { ToggleCapabilityWrapper(it.codifiedEnum()) }
            ?: ToggleCapabilityWrapper(ToggleCapability.BACKLIGHT.codifiedEnum())
    )
}

fun JsonObject?.toVideoStreamCapabilityParameterObject(): VideoStreamCapabilityParameterObject? {
    if (this == null) return null
    return VideoStreamCapabilityParameterObject(
        protocols = this["protocols"]?.jsonArray?.map {
            VideoStreamCapabilityParameterObjectStreamProtocolWrapper(it.jsonPrimitive.content.codifiedEnum())
        } ?: emptyList()
    )
}

fun JsonObject?.toCapabilityParameterObject(typeWrapper: CapabilityTypeWrapper): CapabilityParameterObject? {
    return when (typeWrapper) {
        CapabilityTypeWrapper(CapabilityType.COLOR_SETTING.codifiedEnum()) -> this.toColorSettingCapabilityParameterObject()
        CapabilityTypeWrapper(CapabilityType.ON_OFF.codifiedEnum()) -> this.toOnOffCapabilityParameterObject()
        CapabilityTypeWrapper(CapabilityType.MODE.codifiedEnum()) -> this.toModeCapabilityParameterObject()
        CapabilityTypeWrapper(CapabilityType.RANGE.codifiedEnum()) -> this.toRangeCapabilityParameterObject()
        CapabilityTypeWrapper(CapabilityType.TOGGLE.codifiedEnum()) -> this.toToggleCapabilityParameterObject()
        CapabilityTypeWrapper(CapabilityType.VIDEO_STREAM.codifiedEnum()) -> this.toVideoStreamCapabilityParameterObject()
        else -> error("Unsupported capability type")
    }
}

fun CapabilityParameterObject.toJson(typeWrapper: CapabilityTypeWrapper): JsonObject {
    return when (typeWrapper) {
        CapabilityTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType.COLOR_SETTING.codifiedEnum()) ->
            (this as? ColorSettingCapabilityParameterObject)?.toJson() ?: error("Invalid capability parameters type")
        CapabilityTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType.ON_OFF.codifiedEnum()) ->
            (this as? OnOffCapabilityParameterObject)?.toJson() ?: error("Invalid capability parameters type")
        CapabilityTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType.MODE.codifiedEnum()) ->
            (this as? ModeCapabilityParameterObject)?.toJson() ?: error("Invalid capability parameters type")
        CapabilityTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType.RANGE.codifiedEnum()) ->
            (this as? RangeCapabilityParameterObject)?.toJson() ?: error("Invalid capability parameters type")
        CapabilityTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType.TOGGLE.codifiedEnum()) ->
            (this as? ToggleCapabilityParameterObject)?.toJson() ?: error("Invalid capability parameters type")
        CapabilityTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType.VIDEO_STREAM.codifiedEnum()) ->
            (this as? VideoStreamCapabilityParameterObject)?.toJson() ?: error("Invalid capability parameters type")
        else -> error("Unsupported capability type")
    }
}
fun ColorSettingCapabilityParameterObject.toJson(): JsonObject = buildJsonObject {
    colorModel?.let { put("color_model", it.colorModel.code()) }
    temperatureK?.let { put("temperature_k", it.toJson()) }
    colorScene?.let { put("color_scene", it.toJson()) }
}

fun TemperatureK.toJson(): JsonObject = buildJsonObject {
    put("min", min)
    put("max", max)
}

fun ColorScene.toJson(): JsonObject = buildJsonObject {
    putJsonArray("scenes") {
        scenes.forEach { scene ->
            add(scene.toJson())
        }
    }
}

fun Scene.toJson(): JsonObject = buildJsonObject {
    put("id", id.scene.code())
}

fun OnOffCapabilityParameterObject.toJson(): JsonObject = buildJsonObject {
    put("split", split)
}

fun ModeCapabilityParameterObject.toJson(): JsonObject = buildJsonObject {
    put("instance", instance.mode.code())
    putJsonArray("modes") {
        modes.forEach { mode ->
            add(mode.toJson())
        }
    }
}

fun ModeObject.toJson(): JsonObject = buildJsonObject {
    put("value", value.mode.code())
}

fun RangeCapabilityParameterObject.toJson(): JsonObject = buildJsonObject {
    put("instance", instance.range.code())
    put("random_access", randomAccess)
    range?.let { put("range", it.toJson()) }
    put("looped", looped)
    put("unit", unit?.unit?.code())
}

fun Range.toJson(): JsonObject = buildJsonObject {
    put("min", min)
    put("max", max)
    put("precision", precision)
}

fun ToggleCapabilityParameterObject.toJson(): JsonObject = buildJsonObject {
    put("instance", instance.toggle.code())
}

fun VideoStreamCapabilityParameterObject.toJson(): JsonObject = buildJsonObject {
    putJsonArray("protocols") {
        protocols.forEach { protocol ->
            add(JsonPrimitive(protocol.streamProtocol.code()))
        }
    }
}

fun JsonObject.toCapabilityStateObjectData(typeWrapper: CapabilityTypeWrapper): CapabilityStateObjectData? {
    return when (typeWrapper) {
        CapabilityTypeWrapper(CapabilityType.ON_OFF.codifiedEnum()) -> toOnOffCapabilityStateObjectData()
        CapabilityTypeWrapper(CapabilityType.COLOR_SETTING.codifiedEnum()) -> toColorSettingCapabilityStateObjectData()
        CapabilityTypeWrapper(CapabilityType.RANGE.codifiedEnum()) -> toRangeCapabilityStateObjectData()
        CapabilityTypeWrapper(CapabilityType.MODE.codifiedEnum()) -> toModeCapabilityStateObjectData()
        CapabilityTypeWrapper(CapabilityType.TOGGLE.codifiedEnum()) -> toToggleCapabilityStateObjectData()
        else -> null
    }
}

fun JsonObject.toOnOffCapabilityStateObjectData(): OnOffCapabilityStateObjectData {
    return OnOffCapabilityStateObjectData(
        instance = this["instance"]?.jsonPrimitive?.content?.let { OnOffCapabilityStateObjectInstanceWrapper(it.codifiedEnum()) }
            ?: OnOffCapabilityStateObjectInstanceWrapper(OnOffCapabilityStateObjectInstance.ON.codifiedEnum()),
        value = OnOffCapabilityStateObjectValue(value = this["value"]?.jsonPrimitive?.boolean ?: false)
    )
}

fun JsonObject.toColorSettingCapabilityStateObjectData(): ColorSettingCapabilityStateObjectData {
    return ColorSettingCapabilityStateObjectData(
        instance = this["instance"]?.jsonPrimitive?.content?.let { ColorSettingCapabilityStateObjectInstanceWrapper(it.codifiedEnum()) }
            ?: ColorSettingCapabilityStateObjectInstanceWrapper(ColorSettingCapabilityStateObjectInstance.TEMPERATURE_K.codifiedEnum()),
        value = this["value"].toColorSettingCapabilityStateObjectValue()
    )
}

fun JsonElement?.toColorSettingCapabilityStateObjectValue(): ColorSettingCapabilityStateObjectValue {
    return when (this) {
        is JsonPrimitive -> ColorSettingCapabilityStateObjectValueInteger(value = this.int)
        is JsonObject -> when (this["type"]?.jsonPrimitive?.content) {
            "scene" -> ColorSettingCapabilityStateObjectValueObjectScene(value = SceneObjectWrapper(this["id"]?.jsonPrimitive?.content?.codifiedEnum() ?: SceneObject.ALARM.codifiedEnum()))
            else -> ColorSettingCapabilityStateObjectValueObjectHSV(value = HSVObject(
                h = this["h"]?.jsonPrimitive?.int ?: 0,
                s = this["s"]?.jsonPrimitive?.int ?: 0,
                v = this["v"]?.jsonPrimitive?.int ?: 0
            ))
        }
        else -> ColorSettingCapabilityStateObjectValueInteger(0)
    }
}

fun JsonObject.toRangeCapabilityStateObjectData(): RangeCapabilityStateObjectData {
    return RangeCapabilityStateObjectData(
        instance = this["instance"]?.jsonPrimitive?.content?.let { RangeCapabilityWrapper(it.codifiedEnum()) }
            ?: RangeCapabilityWrapper(RangeCapability.BRIGHTNESS.codifiedEnum()),
        value = RangeCapabilityStateObjectDataValue(value = this["value"]?.jsonPrimitive?.float ?: 0.0f),
        relative = this["relative"]?.jsonPrimitive?.boolean
    )
}

fun JsonObject.toModeCapabilityStateObjectData(): ModeCapabilityStateObjectData {
    return ModeCapabilityStateObjectData(
        instance = this["instance"]?.jsonPrimitive?.content?.let { ModeCapabilityInstanceWrapper(it.codifiedEnum()) }
            ?: ModeCapabilityInstanceWrapper(ModeCapability.THERMOSTAT.codifiedEnum()),
        value = this["value"]?.jsonPrimitive?.content?.let { ModeCapabilityModeWrapper(it.codifiedEnum()) }
            ?: ModeCapabilityModeWrapper(ModeCapabilityMode.AUTO.codifiedEnum())
    )
}

fun JsonObject.toToggleCapabilityStateObjectData(): ToggleCapabilityStateObjectData {
    return ToggleCapabilityStateObjectData(
        instance = this["instance"]?.jsonPrimitive?.content?.let { ToggleCapabilityWrapper(it.codifiedEnum()) }
            ?: ToggleCapabilityWrapper(ToggleCapability.BACKLIGHT.codifiedEnum()),
        value = ToggleCapabilityStateObjectDataValue(value = this["value"]?.jsonPrimitive?.boolean ?: false)
    )
}

fun CapabilityStateObjectData.toJson(typeWrapper: CapabilityTypeWrapper): JsonObject? {
    return when (typeWrapper) {
        CapabilityTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType.ON_OFF.codifiedEnum()) -> (this as? OnOffCapabilityStateObjectData)?.toJson()
        CapabilityTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType.COLOR_SETTING.codifiedEnum()) -> (this as? ColorSettingCapabilityStateObjectData)?.toJson()
        CapabilityTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType.RANGE.codifiedEnum()) -> (this as? RangeCapabilityStateObjectData)?.toJson()
        CapabilityTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType.MODE.codifiedEnum()) -> (this as? ModeCapabilityStateObjectData)?.toJson()
        CapabilityTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType.TOGGLE.codifiedEnum()) -> (this as? ToggleCapabilityStateObjectData)?.toJson()
        else -> null
    }
}

fun OnOffCapabilityStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.onOff.code())
        put("value", value.value)
    }
}

fun ColorSettingCapabilityStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.colorSetting.code())
        put("value", value.toJson())
    }
}

fun ColorSettingCapabilityStateObjectValue.toJson(): JsonElement = when (this) {
    is ColorSettingCapabilityStateObjectValueInteger -> JsonPrimitive(value)
    is ColorSettingCapabilityStateObjectValueObjectScene -> this@toJson.toJson()
    is ColorSettingCapabilityStateObjectValueObjectHSV -> this@toJson.toJson()
}

fun ColorSettingCapabilityStateObjectValueObjectScene.toJson(): JsonObject = buildJsonObject {
    put("type", "scene")
    put("id", value.scene.code())
}

fun ColorSettingCapabilityStateObjectValueObjectHSV.toJson(): JsonObject = buildJsonObject {
    put("type", "hsv")
    value.let {
        put("h", it.h)
        put("s", it.s)
        put("v", it.v)
    }
}

fun RangeCapabilityStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.range.code())
        put("value", value.value)
        relative?.let { put("relative", it) }
    }
}

fun ModeCapabilityStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.mode.code())
        put("value", value.mode.code())
    }
}

fun ToggleCapabilityStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toggle.code())
        put("value", value.value)
    }
}

fun JsonObject.toDevicePropertyObject(): DevicePropertyObject? {
    val type = PropertyTypeWrapper(this["type"]?.jsonPrimitive?.content!!.codifiedEnum())
    val parameters = this["parameters"]?.jsonObject?.toPropertyParameterObject(type)
    val state = if (this["state"] !is JsonNull) this["state"]!!.jsonObject.toPropertyStateObjectData(type) else null
    return if (parameters != null) {
        DevicePropertyObject(
            type = type,
            reportable = this["reportable"]?.jsonPrimitive?.boolean ?: false,
            retrievable = this["retrievable"]?.jsonPrimitive?.boolean ?: false,
            parameters = parameters,
            state = state,
            lastUpdated = this["last_updated"]?.jsonPrimitive?.float ?: 0.0f
        )
    } else {
        null
    }
}

fun DevicePropertyObject.toJson(): JsonObject {
    val type = this.type
    val parametersJson = parameters.toJson(type)
    val stateJson = state?.toJson(type)

    return buildJsonObject {
        put("type", type.type.code())
        put("reportable", reportable)
        put("retrievable", retrievable)
        put("parameters", parametersJson)
        stateJson?.let { put("state", it) } ?: put("state", JsonNull)
        put("last_updated", lastUpdated)
    }
}

fun JsonObject.toPropertyParameterObject(type: PropertyTypeWrapper): PropertyParameterObject {
    return when (type) {
        PropertyTypeWrapper(PropertyType.FLOAT.codifiedEnum()) -> toFloatPropertyParameterObject()
        PropertyTypeWrapper(PropertyType.EVENT.codifiedEnum()) -> toEventPropertyParameterObject()
        else -> error("Unsupported property type")
    }
}

fun JsonObject.toFloatPropertyParameterObject(): FloatPropertyParameterObject {
    return FloatPropertyParameterObject(
        instance = this["instance"]?.toPropertyFunctionWrapper() ?: PropertyFunctionWrapper(PropertyFunction.HUMIDITY.codifiedEnum()),
        unit = this["unit"]?.toMeasurementUnitWrapper() ?: MeasurementUnitWrapper(MeasurementUnit.PERCENT.codifiedEnum())
    )
}

fun JsonObject.toEventPropertyParameterObject(): EventPropertyParameterObject {
    return EventPropertyParameterObject(
        instance = this["instance"]?.toPropertyFunctionWrapper() ?: PropertyFunctionWrapper(PropertyFunction.BUTTON.codifiedEnum()),
        events = this["events"]?.toEventObjectList() ?: emptyList()
    )
}

fun JsonElement.toMeasurementUnitWrapper(): MeasurementUnitWrapper {
    return this.jsonPrimitive.content.let { MeasurementUnitWrapper(it.codifiedEnum()) }
}

fun JsonElement.toEventObjectList(): List<EventObject> {
    return this.jsonArray.map { it.toEventObject() }
}

fun JsonElement.toEventObject(): EventObject {
    return EventObject(
        value = this.jsonObject["value"]?.toEventObjectValueWrapper() ?: EventObjectValueWrapper(EventObjectValue.CLICK.codifiedEnum())
    )
}

fun PropertyParameterObject.toJson(type: PropertyTypeWrapper): JsonObject {
    return when (type) {
        PropertyTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyType.FLOAT.codifiedEnum()) -> (this as? FloatPropertyParameterObject)?.toJson() ?: error("Invalid property parameters type")
        PropertyTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyType.EVENT.codifiedEnum()) -> (this as? EventPropertyParameterObject)?.toJson() ?: error("Invalid property parameters type")
        else -> error("Unsupported property type")
    }
}

fun FloatPropertyParameterObject.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.function.code())
        put("unit", unit.unit.code())
    }
}

fun EventPropertyParameterObject.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.function.code())
        put("events", events.toJson())
    }
}

fun List<EventObject>.toJson(): JsonArray {
    return buildJsonArray {
        forEach { event ->
            add(event.toJson())
        }
    }
}

fun EventObject.toJson(): JsonObject {
    return buildJsonObject {
        put("value", value.value.code())
    }
}

fun JsonObject.toPropertyStateObjectData(type: PropertyTypeWrapper): PropertyStateObjectData? {
    return when (type) {
        PropertyTypeWrapper(PropertyType.FLOAT.codifiedEnum()) -> toFloatPropertyStateObjectData()
        PropertyTypeWrapper(PropertyType.EVENT.codifiedEnum()) -> toEventPropertyStateObjectData()
        else -> null
    }
}

fun JsonObject.toFloatPropertyStateObjectData(): FloatPropertyStateObjectData {
    return FloatPropertyStateObjectData(
        state = this["state"]?.jsonObject?.toFloatPropertyState() ?: FloatPropertyState(
            propertyFunction = PropertyFunctionWrapper(PropertyFunction.CUSTOM.codifiedEnum()),
            propertyValue = FloatObjectValue(0.0f)
        )
    )
}

fun JsonObject.toEventPropertyStateObjectData(): EventPropertyStateObjectData {
    return EventPropertyStateObjectData(
        state = this["state"]?.jsonObject?.toEventPropertyState() ?: EventPropertyState(
            propertyFunction = PropertyFunctionWrapper(PropertyFunction.CUSTOM.codifiedEnum()),
            propertyValue = EventObject(value = EventObjectValueWrapper(EventObjectValue.NORMAL.codifiedEnum()))
        )
    )
}

fun JsonObject.toFloatPropertyState(): FloatPropertyState {
    return FloatPropertyState(
        propertyFunction = this["function"]?.toPropertyFunctionWrapper() ?: PropertyFunctionWrapper(PropertyFunction.CUSTOM.codifiedEnum()),
        propertyValue = FloatObjectValue(value = this["value"]?.jsonPrimitive?.float ?: 0.0f)
    )
}

fun JsonObject.toEventPropertyState(): EventPropertyState {
    return EventPropertyState(
        propertyFunction = this["function"]?.toPropertyFunctionWrapper() ?: PropertyFunctionWrapper(PropertyFunction.CUSTOM.codifiedEnum()),
        propertyValue = EventObject(value = this["value"]?.toEventObjectValueWrapper() ?: EventObjectValueWrapper(EventObjectValue.NORMAL.codifiedEnum()))
    )
}

fun JsonElement.toPropertyFunctionWrapper(): PropertyFunctionWrapper {
    return this.jsonPrimitive.content.let { PropertyFunctionWrapper(it.codifiedEnum()) }
}

fun JsonElement.toEventObjectValueWrapper(): EventObjectValueWrapper {
    return this.jsonPrimitive.content.let { EventObjectValueWrapper(it.codifiedEnum()) }
}

fun PropertyStateObjectData.toJson(type: PropertyTypeWrapper): JsonObject? {
    return when (type) {
        PropertyTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyType.FLOAT.codifiedEnum()) -> (this as? FloatPropertyStateObjectData)?.toJson()
        PropertyTypeWrapper(ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyType.EVENT.codifiedEnum()) -> (this as? EventPropertyStateObjectData)?.toJson()
        else -> null
    }
}

fun FloatPropertyStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("state", state.toJson())
    }
}

fun EventPropertyStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("state", state.toJson())
    }
}

fun FloatPropertyState.toJson(): JsonObject {
    return buildJsonObject {
        put("function", propertyFunction.function.code())
        put("value", propertyValue.value)
    }
}

fun EventPropertyState.toJson(): JsonObject {
    return buildJsonObject {
        put("function", propertyFunction.function.code())
        put("value", propertyValue.value.value.code())
    }
}

fun JsonObject.toGroupCapabilityObject(): GroupCapabilityObject {
    val type = CapabilityTypeWrapper(this["type"]?.jsonPrimitive?.content!!.codifiedEnum())
    val parameters = this["parameters"]?.jsonObject!!.toCapabilityParameterObject(type)
    return GroupCapabilityObject(
        type = type,
        retrievable = this["retrievable"]?.jsonPrimitive?.boolean ?: false,
        parameters = parameters,
        state = this["state"]?.jsonObject?.toCapabilityStateObjectData(type)
    )
}

fun GroupCapabilityObject.toJson(): JsonObject {
    val parametersJson = parameters?.toJson(type)
    val stateJson = state?.toJson(type)

    return buildJsonObject {
        put("type", type.type.code())
        put("retrievable", retrievable)
        put("parameters", parametersJson?: JsonNull)
        stateJson?.let { put("state", it) }
    }
}

fun ScenarioObject.toJson(): JsonObject {
    return buildJsonObject {
        put("id", id)
        put("name", name)
        put("is_active", isActive)
    }
}

fun HouseholdObject.toJson(): JsonObject {
    return buildJsonObject {
        put("id", id)
        put("name", name)
        put("type", type)
    }
}

fun Capability.toJson(): JsonObject {
    return when (this) {
        is DeviceCapabilityObject -> this.toJson()
        is CapabilityObject -> buildJsonObject {
            put("type", type.type.code())
            state?.run { toJson(type)?.let { put("state", it) } }
        }
    }
}

fun JsonObject.toCapabilityObject(type: CapabilityTypeWrapper): CapabilityObject {
    return CapabilityObject(
        type = type,
        state = this["state"]?.jsonObject?.toCapabilityStateObjectData(type),
    )
}

fun JsonObject.toCapability(): Capability {
    val type = CapabilityTypeWrapper(this["type"]?.jsonPrimitive?.content!!.codifiedEnum())

    return when {
        "reportable" in this -> toDeviceCapabilityObject() ?: toCapabilityObject(type)
        else -> toCapabilityObject(type)
    }
}

fun Property.toJson(): JsonObject {
    return when (this) {
        is DevicePropertyObject -> this.toJson()
        is PropertyObject -> buildJsonObject {
            put("type", type.type.code())
            put("retrievable", retrievable)
            put("parameters", parameters.toJson(type))
            state?.run { toJson(type)?.let { put("state", it) } }
            put("last_updated", lastUpdated)
        }
    }
}

fun JsonObject.toPropertyObject(type: PropertyTypeWrapper): PropertyObject {
    return PropertyObject(
        type = type,
        retrievable = this["retrievable"]?.jsonPrimitive?.boolean ?: false,
        parameters = this["parameters"]?.jsonObject?.toPropertyParameterObject(type)!!,
        state = this["state"]?.jsonObject?.toPropertyStateObjectData(type),
        lastUpdated = this["last_updated"]?.jsonPrimitive?.float ?: 0f
    )
}

fun JsonObject.toProperty(): Property {
    val type = PropertyTypeWrapper(this["type"]?.jsonPrimitive?.content!!.codifiedEnum())

    return when {
        "reportable" in this -> toDevicePropertyObject() ?: toPropertyObject(type)
        else -> toPropertyObject(type)
    }
}