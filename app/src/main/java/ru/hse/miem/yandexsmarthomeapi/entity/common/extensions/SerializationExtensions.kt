package ru.hse.miem.yandexsmarthomeapi.entity.common.extensions

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.entity.api.*
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceActionsObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceActionsResultObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.GroupObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.HouseholdObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.MeasurementUnitWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.QuasarInfo
import ru.hse.miem.yandexsmarthomeapi.entity.common.RoomObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.ScenarioObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.SmartHomeInfo
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.*
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.*

// Функции для YandexManageDeviceCapabilitiesStateRequest

fun YandexManageDeviceCapabilitiesStateRequest.toJson(): JsonObject {
    return buildJsonObject {
        putJsonArray("devices") {
            devices.forEach { deviceJson ->
                add(deviceJson)
            }
        }
    }
}

fun JsonObject.toYandexManageDeviceCapabilitiesStateRequest(): YandexManageDeviceCapabilitiesStateRequest {
    val devicesArray = this["devices"]?.jsonArray
        ?: throw IllegalArgumentException("Missing 'devices' field in JSON")
    return YandexManageDeviceCapabilitiesStateRequest(devicesArray.map { it.jsonObject })
}

// Функции для YandexManageGroupCapabilitiesStateRequest

fun YandexManageGroupCapabilitiesStateRequest.toJson(): JsonObject {
    return buildJsonObject {
        putJsonArray("actions") {
            actions.forEach { actionJson ->
                add(actionJson)
            }
        }
    }
}

fun JsonObject.toYandexManageGroupCapabilitiesStateRequest(): YandexManageGroupCapabilitiesStateRequest {
    val actionsArray = this["actions"]?.jsonArray
        ?: throw IllegalArgumentException("Missing 'actions' field in JSON")
    return YandexManageGroupCapabilitiesStateRequest(actionsArray.map { it.jsonObject })
}

// Вспомогательные функции для создания запросов

fun createDeviceCapabilitiesStateRequest(devices: List<DeviceActionsObject>): YandexManageDeviceCapabilitiesStateRequest {
    val deviceJsonList = devices.map { device ->
        buildJsonObject {
            put("id", device.id)
            putJsonArray("capabilities") {
                device.actions.forEach { capability ->
                    add(capability.toJson())
                }
            }
        }
    }
    return YandexManageDeviceCapabilitiesStateRequest(deviceJsonList)
}

fun createGroupCapabilitiesStateRequest(actions: List<CapabilityObject>): YandexManageGroupCapabilitiesStateRequest {
    val actionJsonList = actions.map { action ->
        action.toJson()
    }
    return YandexManageGroupCapabilitiesStateRequest(actionJsonList)
}

// Вспомогательная функция для преобразования CapabilityObject в JSON
//fun CapabilityObject.toJson(): JsonObject {
//    return buildJsonObject {
//        put("type", type.type.code())
//        put("state", buildJsonObject {
//            put("instance", state.instance.toString())
//            put("value", Json.encodeToJsonElement(state.value))
//        })
//    }
//}

// GroupCapabilityObject
fun GroupCapabilityObject.toJson(): JsonObject {
    return buildJsonObject {
        put("type", type.toJson())
        put("retrievable", retrievable)
        parameters?.let { put("parameters", it.toJson()) }
        state?.let { put("state", it.toJson()) }
    }
}

fun JsonObject.toGroupCapabilityObject(): GroupCapabilityObject {
    val type = this["type"]?.jsonPrimitive?.contentOrNull ?: throw IllegalArgumentException("Missing capability type")
    return GroupCapabilityObject(
        type = this["type"]?.jsonObject?.toCapabilityTypeWrapper()
            ?: throw IllegalArgumentException("Missing type"),
        retrievable = this["retrievable"]?.jsonPrimitive?.boolean ?: false,
        parameters = this["parameters"]?.jsonObject?.toCapabilityParameterObject(type),
        state = this["state"]?.jsonObject?.toCapabilityStateObjectData()
    )
}

// CapabilityTypeWrapper
fun CapabilityTypeWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("type", type.code())
    }
}

fun JsonObject.toCapabilityTypeWrapper(): CapabilityTypeWrapper {
    return CapabilityTypeWrapper(
        type = this["type"]?.jsonPrimitive?.content?.let {
            CapabilityType.entries.find { type -> type.code == it }?.codifiedEnum()
        } ?: throw IllegalArgumentException("Invalid capability type")
    )
}

// DeviceCapabilityObject
fun DeviceCapabilityObject.toJson(): JsonObject {
    return buildJsonObject {
        put("type", type.toJson())
        put("reportable", reportable)
        put("retrievable", retrievable)
        put("parameters", parameters.toJson())
        state?.let { put("state", it.toJson()) }
        put("lastUpdated", lastUpdated)
    }
}

fun JsonObject.toDeviceCapabilityObject(): DeviceCapabilityObject {
    val type = when (val typeElement = this["type"]) {
        is JsonPrimitive -> typeElement.content
        is JsonObject -> typeElement["type"]?.jsonPrimitive?.content
        else -> null
    } ?: throw IllegalArgumentException("Missing or invalid capability type")

    return DeviceCapabilityObject(
        type = CapabilityTypeWrapper(type.codifiedEnum()),
        reportable = this["reportable"]?.jsonPrimitive?.booleanOrNull ?: false,
        retrievable = this["retrievable"]?.jsonPrimitive?.booleanOrNull ?: false,
        parameters = when (val params = this["parameters"]) {
            is JsonObject -> when (type) {
                "devices.capabilities.color_setting" -> params.toColorSettingCapabilityParameterObject()
                "devices.capabilities.on_off" -> params.toOnOffCapabilityParameterObject()
                "devices.capabilities.mode" -> params.toModeCapabilityParameterObject()
                "devices.capabilities.range" -> params.toRangeCapabilityParameterObject()
                "devices.capabilities.toggle" -> params.toToggleCapabilityParameterObject()
                "devices.capabilities.video_stream" -> params.toVideoStreamCapabilityParameterObject()
                else -> UnknownCapabilityParameterObject(this)
            }
            else -> throw IllegalArgumentException("Invalid parameters format")
        },
        state = this["state"]?.jsonObject?.toCapabilityStateObjectData(),
        lastUpdated = this["last_updated"]?.jsonPrimitive?.floatOrNull ?: 0f
    )
}

// CapabilityObject
fun CapabilityObject.toJson(): JsonObject {
    return buildJsonObject {
        put("type", type.toJson())
        state?.let { put("state", it.toJson()) }
    }
}

fun JsonObject.toCapabilityObject(): CapabilityObject {
    return CapabilityObject(
        type = this["type"]?.jsonObject?.toCapabilityTypeWrapper()
            ?: throw IllegalArgumentException("Missing type"),
        state = this["state"]?.jsonObject?.toCapabilityStateObjectData()
    )
}

// CapabilityActionResultObject
fun CapabilityActionResultObject.toJson(): JsonObject {
    return buildJsonObject {
        put("type", type.toJson())
        put("state", (state as? CapabilityStateObjectData)?.toJson() ?: JsonObject(emptyMap()))
    }
}

fun JsonObject.toCapabilityActionResultObject(): CapabilityActionResultObject {
    return CapabilityActionResultObject(
        type = this["type"]?.jsonObject?.toCapabilityTypeWrapper()
            ?: throw IllegalArgumentException("Missing type"),
        state = this["state"]?.jsonObject?.toCapabilityStateObjectData()
            ?: throw IllegalArgumentException("Missing state")
    )
}

// CapabilityParameterObject
fun CapabilityParameterObject.toJson(): JsonObject {
    return when (this) {
        is ColorSettingCapabilityParameterObject -> this.toJson()
        is OnOffCapabilityParameterObject -> this.toJson()
        is ModeCapabilityParameterObject -> this.toJson()
        is RangeCapabilityParameterObject -> this.toJson()
        is ToggleCapabilityParameterObject -> this.toJson()
        is VideoStreamCapabilityParameterObject -> this.toJson()
        is UnknownCapabilityParameterObject -> this.data
    }
}

fun JsonObject.toCapabilityParameterObject(type: String): CapabilityParameterObject {
    return when (type) {
        "devices.capabilities.on_off" -> this.toOnOffCapabilityParameterObject()
        "devices.capabilities.color_setting" -> this.toColorSettingCapabilityParameterObject()
        "devices.capabilities.mode" -> this.toModeCapabilityParameterObject()
        "devices.capabilities.range" -> this.toRangeCapabilityParameterObject()
        "devices.capabilities.toggle" -> this.toToggleCapabilityParameterObject()
        "devices.capabilities.video_stream" -> this.toVideoStreamCapabilityParameterObject()
        else -> UnknownCapabilityParameterObject(this)
    }
}

// ColorSettingCapabilityParameterObject
fun ColorSettingCapabilityParameterObject.toJson(): JsonObject {
    return buildJsonObject {
        colorModel?.let { put("color_model", it.toJson()) }
        temperatureK?.let { put("temperature_k", it.toJson()) }
        colorScene?.let { put("color_scene", it.toJson()) }
    }
}

fun JsonObject.toColorSettingCapabilityParameterObject(): ColorSettingCapabilityParameterObject {
    return ColorSettingCapabilityParameterObject(
        colorModel = this["color_model"]?.let {
            when (it) {
                is JsonPrimitive -> it.contentOrNull?.let { ColorModelWrapper(it.codifiedEnum()) }
                is JsonObject -> it["color_model"]?.jsonPrimitive?.contentOrNull?.let { model ->
                    ColorModelWrapper(model.codifiedEnum())
                }
                else -> null
            }
        },
        temperatureK = this["temperature_k"]?.toTemperatureK(),
        colorScene = this["color_scene"]?.toColorScene()
    )
}

// OnOffCapabilityParameterObject
fun OnOffCapabilityParameterObject.toJson(): JsonObject {
    return buildJsonObject {
        put("split", split)
    }
}

fun JsonObject.toOnOffCapabilityParameterObject(): OnOffCapabilityParameterObject {
    return OnOffCapabilityParameterObject(
        split = this["split"]?.jsonPrimitive?.booleanOrNull ?: false
    )
}

// ModeCapabilityParameterObject
fun ModeCapabilityParameterObject.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toJson())
        putJsonArray("modes") {
            modes.forEach { add(it.toJson()) }
        }
    }
}

fun JsonObject.toModeCapabilityParameterObject(): ModeCapabilityParameterObject {
    return ModeCapabilityParameterObject(
        instance = this["instance"]?.let { jsonElement ->
            when (jsonElement) {
                is JsonObject -> jsonElement.toModeCapabilityInstanceWrapper()
                is JsonPrimitive -> ModeCapabilityInstanceWrapper(jsonElement.content.codifiedEnum())
                else -> throw IllegalArgumentException("Expected JsonObject or JsonPrimitive in 'instance', but found ${jsonElement::class.simpleName}")
            }
        } ?: throw IllegalArgumentException("Missing instance"),
        modes = this["modes"]?.jsonArray?.map {
            when (val modeElement = it) {
                is JsonObject -> modeElement.toModeObject()
                is JsonPrimitive -> ModeObject(ModeCapabilityModeWrapper(modeElement.content.codifiedEnum()))
                else -> throw IllegalArgumentException("Expected JsonObject or JsonPrimitive in 'modes', but found ${modeElement::class.simpleName}")
            }
        } ?: throw IllegalArgumentException("Missing modes")
    )
}


// RangeCapabilityParameterObject
fun RangeCapabilityParameterObject.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toJson())
        unit?.let { put("unit", it.toJson()) }
        put("random_access", randomAccess)
        range?.let { put("range", it.toJson()) }
        looped?.let { put("looped", it) }
    }
}

fun JsonObject.toRangeCapabilityParameterObject(): RangeCapabilityParameterObject {
    return RangeCapabilityParameterObject(
        instance = this["instance"]?.toRangeCapabilityWrapper()
            ?: throw IllegalArgumentException("Missing instance"),
        unit = this["unit"]?.toMeasurementUnitWrapper(),
        randomAccess = this["random_access"]?.jsonPrimitive?.boolean ?: false,
        range = this["range"]?.jsonObject?.toRange(),
        looped = this["looped"]?.jsonPrimitive?.booleanOrNull
    )
}

// ToggleCapabilityParameterObject
fun ToggleCapabilityParameterObject.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toJson())
    }
}

fun JsonObject.toToggleCapabilityParameterObject(): ToggleCapabilityParameterObject {
    return ToggleCapabilityParameterObject(
        instance = this["instance"]?.toToggleCapabilityWrapper()
            ?: throw IllegalArgumentException("Missing instance")
    )
}

// ColorModelWrapper
fun ColorModelWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("colorModel", colorModel.code())
    }
}

fun JsonObject.toColorModelWrapper(): ColorModelWrapper {
    return ColorModelWrapper(
        colorModel = this["colorModel"]?.jsonPrimitive?.content?.let {
            ColorModel.entries.find { model -> model.code == it }?.codifiedEnum()
        } ?: throw IllegalArgumentException("Invalid color model")
    )
}

// TemperatureK
fun TemperatureK.toJson(): JsonObject {
    return buildJsonObject {
        put("min", min)
        put("max", max)
    }
}

fun JsonElement.toTemperatureK(): TemperatureK? {
    return when (this) {
        is JsonObject -> TemperatureK(
            min = this["min"]?.jsonPrimitive?.intOrNull ?: return null,
            max = this["max"]?.jsonPrimitive?.intOrNull ?: return null
        )
        is JsonPrimitive -> null // или обработать, если возможно
        else -> null
    }
}

// ColorScene
fun ColorScene.toJson(): JsonObject {
    return buildJsonObject {
        putJsonArray("scenes") {
            scenes.forEach { add(it.toJson()) }
        }
    }
}

fun JsonElement.toColorScene(): ColorScene? {
    return when (this) {
        is JsonObject -> {
            val scenes = this["scenes"]?.jsonArray?.mapNotNull { sceneElement ->
                when (sceneElement) {
                    is JsonObject -> {
                        val id = when (val idElement = sceneElement["id"]) {
                            is JsonPrimitive -> idElement.contentOrNull
                            is JsonObject -> idElement["id"]?.jsonPrimitive?.contentOrNull
                            else -> null
                        }
                        id?.let { Scene(SceneObjectWrapper(it.uppercase().codifiedEnum())) }
                    }
                    is JsonPrimitive -> sceneElement.contentOrNull?.let {
                        Scene(SceneObjectWrapper(it.uppercase().codifiedEnum()))
                    }
                    else -> null
                }
            }
            if (scenes != null && scenes.isNotEmpty()) ColorScene(scenes) else null
        }
        else -> null
    }
}

// Scene
fun Scene.toJson(): JsonObject {
    return buildJsonObject {
        put("id", id.toJson())
    }
}

fun JsonObject.toScene(): Scene {
    return Scene(
        id = this["id"]?.jsonObject?.toSceneObjectWrapper()
            ?: throw IllegalArgumentException("Missing id")
    )
}

// SceneObjectWrapper
fun SceneObjectWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("scene", scene.code())
    }
}

fun JsonObject.toSceneObjectWrapper(): SceneObjectWrapper {
    return SceneObjectWrapper(
        scene = this["scene"]?.jsonPrimitive?.content?.let {
            SceneObject.entries.find { scene -> scene.code == it }?.codifiedEnum()
        } ?: throw IllegalArgumentException("Invalid scene")
    )
}

// ModeObject
fun ModeObject.toJson(): JsonObject {
    return buildJsonObject {
        put("value", value.toJson())
    }
}

fun JsonObject.toModeObject(): ModeObject {
    val jsonElement = this["value"]
    return ModeObject(
        value = jsonElement?.toModeCapabilityModeWrapper() ?: throw IllegalArgumentException("Missing or invalid value")
    )
}

// ModeCapabilityModeWrapper
fun ModeCapabilityModeWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("mode", mode.code())
    }
}

fun JsonElement.toModeCapabilityModeWrapper(): ModeCapabilityModeWrapper {
    val modeString = if (this is JsonPrimitive && this.isString) {
        this.content
    } else {
        (this as JsonObject)["mode"]?.jsonPrimitive?.content
    } ?: throw IllegalArgumentException("Missing or invalid mode")

    return ModeCapabilityModeWrapper(
        mode = modeString.codifiedEnum<ModeCapabilityMode>()
    )
}

// ModeCapabilityInstanceWrapper
fun ModeCapabilityInstanceWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("mode", mode.code())
    }
}

fun JsonObject.toModeCapabilityInstanceWrapper(): ModeCapabilityInstanceWrapper {
    return ModeCapabilityInstanceWrapper(
        mode = this["mode"]?.jsonPrimitive?.content?.let {
            ModeCapability.entries.find { mode -> mode.code == it }?.codifiedEnum()
        } ?: throw IllegalArgumentException("Invalid mode capability")
    )
}

// RangeCapabilityWrapper
fun RangeCapabilityWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("range", range.code())
    }
}

fun JsonElement.toRangeCapabilityWrapper(): RangeCapabilityWrapper {
    return RangeCapabilityWrapper(
        range = if (this is JsonPrimitive && this.isString) {
            this.content
        } else {
            (this as JsonObject)["range"]?.jsonPrimitive?.content
        }?.codifiedEnum<RangeCapability>() ?: throw IllegalArgumentException("Missing or invalid range")
    )
}

// Range
fun Range.toJson(): JsonObject {
    return buildJsonObject {
        put("min", min)
        put("max", max)
        put("precision", precision)
    }
}

fun JsonObject.toRange(): Range {
    return Range(
        min = this["min"]?.jsonPrimitive?.float ?: throw IllegalArgumentException("Missing min"),
        max = this["max"]?.jsonPrimitive?.float ?: throw IllegalArgumentException("Missing max"),
        precision = this["precision"]?.jsonPrimitive?.float ?: throw IllegalArgumentException("Missing precision")
    )
}

// ToggleCapabilityWrapper
fun ToggleCapabilityWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("toggle", toggle.code())
    }
}

fun JsonElement.toToggleCapabilityWrapper(): ToggleCapabilityWrapper {
    return ToggleCapabilityWrapper(
        toggle = if (this is JsonPrimitive && this.isString) {
            this.content.codifiedEnum()
        } else if(this is JsonObject) {
            this["toggle"]?.jsonPrimitive?.content?.codifiedEnum()
                ?: throw IllegalArgumentException("Missing or invalid toggle")
        } else {
            throw IllegalArgumentException("Missing or invalid toggle")
        }
    )
}

// StateResultObject
fun StateResultObject.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", (instance as? CapabilityStateObjectInstance)?.toJson() ?: JsonObject(emptyMap()))
        put("action_result", actionResult.toJson())
    }
}

fun JsonObject.toStateResultObject(): StateResultObject {
    return StateResultObject(
        instance = this["instance"]?.jsonObject?.toCapabilityStateObjectInstance()
            ?: throw IllegalArgumentException("Missing instance"),
        actionResult = this["action_result"]?.jsonObject?.toActionResult()
            ?: throw IllegalArgumentException("Missing action_result")
    )
}

// CapabilityStateObjectData
fun CapabilityStateObjectData.toJson(): JsonObject {
    return when (this) {
        is OnOffCapabilityStateObjectData -> this.toJson()
        is ColorSettingCapabilityStateObjectData -> this.toJson()
        is ModeCapabilityStateObjectData -> this.toJson()
        is RangeCapabilityStateObjectData -> this.toJson()
        is ToggleCapabilityStateObjectData -> this.toJson()
        is VideoStreamCapabilityStateObjectData -> this.toJson()
    }
}

fun JsonObject.toCapabilityStateObjectData(): CapabilityStateObjectData {
    return when (val instance = this["instance"]?.jsonPrimitive?.content) {
        "on" -> this.toOnOffCapabilityStateObjectData()
        "rgb", "hsv", "temperature_k", "scene" -> this.toColorSettingCapabilityStateObjectData()
        "thermostat", "fan_speed", "swing", "work_speed" -> this.toModeCapabilityStateObjectData()
        "brightness", "channel", "humidity", "open", "temperature", "volume" -> this.toRangeCapabilityStateObjectData()
        "backlight", "controls_locked", "ionization", "keep_warm", "mute", "oscillation", "pause" -> this.toToggleCapabilityStateObjectData()
        "get_stream" -> this.toVideoStreamCapabilityStateObjectData()
        else -> throw IllegalArgumentException("Unknown capability state instance: $instance")
    }
}

// OnOffCapabilityStateObjectData
fun OnOffCapabilityStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toJson())
        put("value", value.toJson())
    }
}

fun JsonObject.toOnOffCapabilityStateObjectData(): OnOffCapabilityStateObjectData {
    return OnOffCapabilityStateObjectData(
        instance = this["instance"]?.toOnOffCapabilityStateObjectInstanceWrapper()
            ?: throw IllegalArgumentException("Missing instance"),
        value = this["value"]?.toOnOffCapabilityStateObjectValue()
            ?: throw IllegalArgumentException("Missing value")
    )
}

// ColorSettingCapabilityStateObjectData
fun ColorSettingCapabilityStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toJson())
        put("value", value.toJson())
    }
}

fun JsonObject.toColorSettingCapabilityStateObjectData(): ColorSettingCapabilityStateObjectData {
    return ColorSettingCapabilityStateObjectData(
        instance = this["instance"]?.jsonObject?.toColorSettingCapabilityStateObjectInstanceWrapper()
            ?: throw IllegalArgumentException("Missing instance"),
        value = this["value"]?.jsonObject?.toColorSettingCapabilityStateObjectValue()
            ?: throw IllegalArgumentException("Missing value")
    )
}

// ModeCapabilityStateObjectData
fun ModeCapabilityStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toJson())
        put("value", value.toJson())
    }
}

fun JsonObject.toModeCapabilityStateObjectData(): ModeCapabilityStateObjectData {
    return ModeCapabilityStateObjectData(
        instance = this["instance"]?.jsonObject?.toModeCapabilityInstanceWrapper()
            ?: throw IllegalArgumentException("Missing instance"),
        value = this["value"]?.jsonPrimitive?.toModeCapabilityModeWrapper()
            ?: throw IllegalArgumentException("Missing value")
    )
}

// RangeCapabilityStateObjectData
fun RangeCapabilityStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toJson())
        put("value", value.toJson())
        relative?.let { put("relative", it) }
    }
}

fun JsonObject.toRangeCapabilityStateObjectData(): RangeCapabilityStateObjectData {
    return RangeCapabilityStateObjectData(
        instance = this["instance"]?.toRangeCapabilityWrapper()
            ?: throw IllegalArgumentException("Missing instance"),
        value = this["value"]?.jsonObject?.toRangeCapabilityStateObjectDataValue()
            ?: throw IllegalArgumentException("Missing value"),
        relative = this["relative"]?.jsonPrimitive?.booleanOrNull
    )
}

// ToggleCapabilityStateObjectData
fun ToggleCapabilityStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toJson())
        put("value", value.toJson())
    }
}

fun JsonObject.toToggleCapabilityStateObjectData(): ToggleCapabilityStateObjectData {
    return ToggleCapabilityStateObjectData(
        instance = this["instance"]?.jsonObject?.toToggleCapabilityWrapper()
            ?: throw IllegalArgumentException("Missing instance"),
        value = this["value"]?.jsonObject?.toToggleCapabilityStateObjectDataValue()
            ?: throw IllegalArgumentException("Missing value")
    )
}

// VideoStreamCapabilityStateObjectData
fun VideoStreamCapabilityStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toJson())
        put("value", value.toJson())
    }
}

fun JsonObject.toVideoStreamCapabilityStateObjectData(): VideoStreamCapabilityStateObjectData {
    return VideoStreamCapabilityStateObjectData(
        instance = this["instance"]?.jsonObject?.toVideoStreamCapabilityStateObjectInstanceWrapper()
            ?: throw IllegalArgumentException("Missing instance"),
        value = this["value"]?.jsonObject?.toCapabilityStateObjectValue()
            ?: throw IllegalArgumentException("Missing value")
    )
}

// OnOffCapabilityStateObjectValue
fun OnOffCapabilityStateObjectValue.toJson(): JsonObject {
    return buildJsonObject {
        put("value", value)
    }
}

fun JsonElement.toOnOffCapabilityStateObjectValue(): OnOffCapabilityStateObjectValue {
    return OnOffCapabilityStateObjectValue(
        value = when (this) {
            is JsonPrimitive -> this.boolean
            else -> throw IllegalArgumentException("Invalid OnOffCapabilityStateObjectValue")
        }
    )
}

// ColorSettingCapabilityStateObjectValue
fun ColorSettingCapabilityStateObjectValue.toJson(): JsonObject {
    return when (this) {
        is ColorSettingCapabilityStateObjectValueRGB -> this.toJson()
        is ColorSettingCapabilityStateObjectValueObjectScene -> this.toJson()
        is ColorSettingCapabilityStateObjectValueObjectHSV -> this.toJson()
    }
}

fun JsonObject.toColorSettingCapabilityStateObjectValue(): ColorSettingCapabilityStateObjectValue {
    return when {
        this.containsKey("value") && this["value"] is JsonPrimitive -> ColorSettingCapabilityStateObjectValueRGB(this["value"]!!.jsonPrimitive.int)
        this.containsKey("value") && this["value"] is JsonObject -> ColorSettingCapabilityStateObjectValueObjectScene(this["value"]!!.jsonObject.toSceneObjectWrapper())
        this.containsKey("h") && this.containsKey("s") && this.containsKey("v") -> ColorSettingCapabilityStateObjectValueObjectHSV(this.toHSVObject())
        else -> throw IllegalArgumentException("Invalid ColorSettingCapabilityStateObjectValue")
    }
}

// ColorSettingCapabilityStateObjectValueRGB
fun ColorSettingCapabilityStateObjectValueRGB.toJson(): JsonObject {
    return buildJsonObject {
        put("value", value)
    }
}

// ColorSettingCapabilityStateObjectValueObjectScene
fun ColorSettingCapabilityStateObjectValueObjectScene.toJson(): JsonObject {
    return buildJsonObject {
        put("value", value.toJson())
    }
}

// ColorSettingCapabilityStateObjectValueObjectHSV
fun ColorSettingCapabilityStateObjectValueObjectHSV.toJson(): JsonObject {
    return value.toJson()
}

// HSVObject
fun HSVObject.toJson(): JsonObject {
    return buildJsonObject {
        put("h", h)
        put("s", s)
        put("v", v)
    }
}

fun JsonObject.toHSVObject(): HSVObject {
    return HSVObject(
        h = this["h"]?.jsonPrimitive?.int ?: throw IllegalArgumentException("Missing h"),
        s = this["s"]?.jsonPrimitive?.int ?: throw IllegalArgumentException("Missing s"),
        v = this["v"]?.jsonPrimitive?.int ?: throw IllegalArgumentException("Missing v")
    )
}

// RangeCapabilityStateObjectDataValue
fun RangeCapabilityStateObjectDataValue.toJson(): JsonObject {
    return buildJsonObject {
        put("value", value)
    }
}

fun JsonObject.toRangeCapabilityStateObjectDataValue(): RangeCapabilityStateObjectDataValue {
    return RangeCapabilityStateObjectDataValue(
        value = this["value"]?.jsonPrimitive?.float ?: throw IllegalArgumentException("Missing value")
    )
}

// ToggleCapabilityStateObjectDataValue
fun ToggleCapabilityStateObjectDataValue.toJson(): JsonObject {
    return buildJsonObject {
        put("value", value)
    }
}

fun JsonObject.toToggleCapabilityStateObjectDataValue(): ToggleCapabilityStateObjectDataValue {
    return ToggleCapabilityStateObjectDataValue(
        value = this["value"]?.jsonPrimitive?.boolean ?: throw IllegalArgumentException("Missing value")
    )
}

// VideoStreamCapabilityStateObjectRequestValue
fun VideoStreamCapabilityStateObjectRequestValue.toJson(): JsonObject {
    return buildJsonObject {
        putJsonArray("protocols") {
            protocols.forEach { add(it.toJson()) }
        }
    }
}

fun JsonObject.toVideoStreamCapabilityStateObjectRequestValue(): VideoStreamCapabilityStateObjectRequestValue {
    return VideoStreamCapabilityStateObjectRequestValue(
        protocols = this["protocols"]?.jsonArray?.map { it.jsonObject.toVideoStreamProtocolWrapper() }
            ?: throw IllegalArgumentException("Missing protocols")
    )
}

// VideoStreamCapabilityStateObjectResponseValue
fun VideoStreamCapabilityStateObjectResponseValue.toJson(): JsonObject {
    return buildJsonObject {
        put("stream_url", streamUrl)
        put("protocol", protocol.toJson())
    }
}

fun JsonObject.toVideoStreamCapabilityStateObjectResponseValue(): VideoStreamCapabilityStateObjectResponseValue {
    return VideoStreamCapabilityStateObjectResponseValue(
        streamUrl = this["stream_url"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing stream_url"),
        protocol = this["protocol"]?.jsonObject?.toVideoStreamProtocolWrapper() ?: throw IllegalArgumentException("Missing protocol")
    )
}

// VideoStreamProtocolWrapper
fun VideoStreamProtocolWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("protocol", protocol.code())
    }
}

fun JsonObject.toVideoStreamProtocolWrapper(): VideoStreamProtocolWrapper {
    return VideoStreamProtocolWrapper(
        protocol = this["protocol"]?.jsonPrimitive?.content?.let {
            VideoStreamProtocol.entries.find { protocol -> protocol.code == it }?.codifiedEnum()
        } ?: throw IllegalArgumentException("Invalid video stream protocol")
    )
}

// VideoStreamCapabilityStateObjectInstanceWrapper
fun VideoStreamCapabilityStateObjectInstanceWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("videoStream", videoStream.code())
    }
}

fun JsonObject.toVideoStreamCapabilityStateObjectInstanceWrapper(): VideoStreamCapabilityStateObjectInstanceWrapper {
    return VideoStreamCapabilityStateObjectInstanceWrapper(
        videoStream = this["videoStream"]?.jsonPrimitive?.content?.let {
            VideoStreamCapabilityStateObjectInstance.entries.find { instance -> instance.code == it }?.codifiedEnum()
        } ?: throw IllegalArgumentException("Invalid video stream capability state object instance")
    )
}

// ActionResult
fun ActionResult.toJson(): JsonObject {
    return buildJsonObject {
        put("status", status.toJson())
        errorCode?.let { put("error_code", it.toJson()) }
        errorMessage?.let { put("error_message", it) }
    }
}

fun JsonObject.toActionResult(): ActionResult {
    return ActionResult(
        status = this["status"]?.jsonObject?.toStatusWrapper() ?: throw IllegalArgumentException("Missing status"),
        errorCode = this["error_code"]?.jsonObject?.toErrorCodeWrapper(),
        errorMessage = this["error_message"]?.jsonPrimitive?.contentOrNull
    )
}

// StatusWrapper
fun StatusWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("status", status.code())
    }
}

fun JsonObject.toStatusWrapper(): StatusWrapper {
    return StatusWrapper(
        status = this["status"]?.jsonPrimitive?.content?.let {
            Status.entries.find { status -> status.code == it }?.codifiedEnum()
        } ?: throw IllegalArgumentException("Invalid status")
    )
}

// ErrorCodeWrapper
fun ErrorCodeWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("errorCode", errorCode.code())
    }
}

fun JsonObject.toErrorCodeWrapper(): ErrorCodeWrapper {
    return ErrorCodeWrapper(
        errorCode = this["errorCode"]?.jsonPrimitive?.content?.let {
            ErrorCode.entries.find { errorCode -> errorCode.code == it }?.codifiedEnum()
        } ?: throw IllegalArgumentException("Invalid error code")
    )
}

// VideoStreamCapabilityParameterObject в JSON
fun VideoStreamCapabilityParameterObject.toJson(): JsonObject {
    return buildJsonObject {
        putJsonArray("protocols") {
            protocols.forEach { add(it.toJson()) }
        }
    }
}

// toVideoStreamCapabilityParameterObject()
fun JsonObject.toVideoStreamCapabilityParameterObject(): VideoStreamCapabilityParameterObject {
    return VideoStreamCapabilityParameterObject(
        protocols = this["protocols"]?.jsonArray?.map {
            when (val jsonElement = it) {
                is JsonObject -> {
                    jsonElement.toVideoStreamProtocolWrapper()
                }
                is JsonPrimitive -> {
                    VideoStreamProtocolWrapper(jsonElement.content.codifiedEnum())
                }
                else -> throw IllegalArgumentException("Invalid protocol type")
            }
        } ?: throw IllegalArgumentException("Missing protocols")
    )
}


// MeasurementUnitWrapper в JSON
fun MeasurementUnitWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("unit", unit.code())
    }
}

// MeasurementUnitWrapper()
fun JsonElement.toMeasurementUnitWrapper(): MeasurementUnitWrapper {
    return MeasurementUnitWrapper(
        unit = when (this) {
            is JsonPrimitive -> {
                this.content.codifiedEnum<MeasurementUnit>()
            }
            is JsonObject -> {
                this["unit"]?.jsonPrimitive?.content?.codifiedEnum<MeasurementUnit>() ?: throw IllegalArgumentException("Invalid measurement unit")
            }
            else -> {
                throw IllegalArgumentException("Invalid measurement unit")
            }
        }
    )
}

fun CapabilityStateObjectInstance.toJson(): JsonObject {
    return when (this) {
        is OnOffCapabilityStateObjectInstanceWrapper -> this.toJson()
        is ColorSettingCapabilityStateObjectInstanceWrapper -> this.toJson()
        is ModeCapabilityInstanceWrapper -> this.toJson()
        is RangeCapabilityWrapper -> this.toJson()
        is ToggleCapabilityWrapper -> this.toJson()
        is VideoStreamCapabilityStateObjectInstanceWrapper -> this.toJson()
    }
}

fun JsonObject.toCapabilityStateObjectInstance(): CapabilityStateObjectInstance {
    return when (val instanceType = this["instance"]?.jsonPrimitive?.content) {
        "on" -> this.toOnOffCapabilityStateObjectInstanceWrapper()
        "rgb", "hsv", "temperature_k", "scene" -> this.toColorSettingCapabilityStateObjectInstanceWrapper()
        "thermostat", "fan_speed", "swing", "work_speed" -> this.toModeCapabilityInstanceWrapper()
        "brightness", "channel", "humidity", "open", "temperature", "volume" -> this.toRangeCapabilityWrapper()
        "backlight", "controls_locked", "ionization", "keep_warm", "mute", "oscillation", "pause" -> this.toToggleCapabilityWrapper()
        "get_stream" -> this.toVideoStreamCapabilityStateObjectInstanceWrapper()
        else -> throw IllegalArgumentException("Unknown instance type: $instanceType")
    }
}

fun OnOffCapabilityStateObjectInstanceWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", onOff.code())
    }
}

fun JsonElement.toOnOffCapabilityStateObjectInstanceWrapper(): OnOffCapabilityStateObjectInstanceWrapper {
    return OnOffCapabilityStateObjectInstanceWrapper(
        onOff = when (this) {
            is JsonPrimitive -> {
                this.content.codifiedEnum<OnOffCapabilityStateObjectInstance>()
            }
            is JsonObject -> {
                this["instance"]?.jsonPrimitive?.content?.codifiedEnum<OnOffCapabilityStateObjectInstance>() ?: throw IllegalArgumentException("Invalid OnOff instance")
            }
            else -> {
                throw IllegalArgumentException("Invalid OnOff instance")
            }
        }
    )
}

fun ColorSettingCapabilityStateObjectInstanceWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", colorSetting.code())
    }
}

fun JsonObject.toColorSettingCapabilityStateObjectInstanceWrapper(): ColorSettingCapabilityStateObjectInstanceWrapper {
    return ColorSettingCapabilityStateObjectInstanceWrapper(
        colorSetting = this["instance"]?.jsonPrimitive?.content?.let { instanceContent ->
            ColorSettingCapabilityStateObjectInstance.entries.find { it.code == instanceContent }?.codifiedEnum()
        } ?: throw IllegalArgumentException("Invalid ColorSetting instance")
    )
}

fun CapabilityStateObjectValue.toJson(): JsonObject {
    return when (this) {
        is OnOffCapabilityStateObjectValue -> this.toJson()
        is ColorSettingCapabilityStateObjectValue -> this.toJson()
        is ModeCapabilityModeWrapper -> this.toJson()
        is RangeCapabilityStateObjectDataValue -> this.toJson()
        is ToggleCapabilityStateObjectDataValue -> this.toJson()
        is VideoStreamCapabilityStateObjectRequestValue -> this.toJson()
        is VideoStreamCapabilityStateObjectResponseValue -> this.toJson()
    }
}

fun JsonObject.toCapabilityStateObjectValue(): CapabilityStateObjectValue {
    return when {
        this.containsKey("value") && this["value"] is JsonPrimitive -> {
            when (this["value"]?.jsonPrimitive?.content) {
                "true", "false" -> this.toOnOffCapabilityStateObjectValue()
                else -> this.toRangeCapabilityStateObjectDataValue()
            }
        }
        this.containsKey("value") && this["value"] is JsonObject -> this.toColorSettingCapabilityStateObjectValue()
        this.containsKey("mode") -> this.jsonPrimitive.toModeCapabilityModeWrapper()
        this.containsKey("protocols") -> this.toVideoStreamCapabilityStateObjectRequestValue()
        this.containsKey("stream_url") -> this.toVideoStreamCapabilityStateObjectResponseValue()
        else -> throw IllegalArgumentException("Unknown CapabilityStateObjectValue type")
    }
}

fun JsonElement.toDeviceObject(): DeviceObject {
    val jsonObject = when (this) {
        is JsonObject -> this
        is JsonPrimitive -> Json.parseToJsonElement(content).jsonObject
        is JsonArray -> throw IllegalArgumentException("Expected JsonObject or JsonPrimitive, but got JsonArray")
        JsonNull -> throw IllegalArgumentException("Expected JsonObject or JsonPrimitive, but got JsonNull")
    }

    return DeviceObject(
        id = jsonObject["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing id"),
        name = jsonObject["name"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing name"),
        aliases = jsonObject["aliases"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }?.toMutableList() ?: mutableListOf(),
        type = jsonObject["type"]?.let { typeJson ->
            when (typeJson) {
                is JsonPrimitive -> DeviceTypeWrapper(typeJson.content.codifiedEnum())
                is JsonObject -> typeJson.toDeviceTypeWrapper()
                else -> throw IllegalArgumentException("Invalid type format")
            }
        } ?: throw IllegalArgumentException("Missing type"),
        externalId = jsonObject["external_id"]?.jsonPrimitive?.contentOrNull ?: "",
        skillId = jsonObject["skill_id"]?.jsonPrimitive?.contentOrNull ?: "",
        householdId = jsonObject["household_id"]?.jsonPrimitive?.contentOrNull ?: "",
        room = jsonObject["room"]?.jsonPrimitive?.contentOrNull,
        groups = jsonObject["groups"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }?.toMutableList() ?: mutableListOf(),
        capabilities = jsonObject["capabilities"]?.jsonArray?.mapNotNull {
            when (it) {
                is JsonObject -> it.toDeviceCapabilityObject()
                else -> null
            }
        }?.toMutableList() ?: mutableListOf(),
        properties = jsonObject["properties"]?.jsonArray?.mapNotNull {
            when (it) {
                is JsonObject -> it.toDevicePropertyObject()
                else -> null
            }
        }?.toMutableList() ?: mutableListOf(),
        quasarInfo = jsonObject["quasar_info"]?.let {
            when (it) {
                is JsonObject -> it.toQuasarInfo()
                else -> null
            }
        }
    )
}

fun JsonObject.toDeviceTypeWrapper(): DeviceTypeWrapper {
    val typeCode = this["type"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing type code")
    return DeviceTypeWrapper(typeCode.codifiedEnum())
}

fun JsonObject.toYandexDeviceStateResponse(): YandexDeviceStateResponse {
    return YandexDeviceStateResponse(
        status = this["status"]?.jsonPrimitive?.content ?: "ok",
        requestId = this["request_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing request_id"),
        id = this["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing id"),
        name = this["name"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing name"),
        aliases = this["aliases"]?.jsonArray?.map { it.jsonPrimitive.content } ?: listOf(),
        type = JsonPrimitive(this["type"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing type")),
        state = JsonPrimitive(this["state"]?.jsonPrimitive?.content ?: ""),
        groups = this["groups"]?.jsonArray?.map { it.jsonPrimitive.content } ?: listOf(),
        room = this["room"]?.jsonPrimitive?.content,
        externalId = this["external_id"]?.jsonPrimitive?.content ?: "",
        skillId = this["skill_id"]?.jsonPrimitive?.content ?: "",
        capabilities = this["capabilities"]?.jsonArray?.map { it.jsonObject } ?: listOf(),
        properties = this["properties"]?.jsonArray?.map { it.jsonObject } ?: listOf(),
        quasarInfo = this["quasar_info"]?.jsonObject
    )
}

fun JsonObject.toYandexManageDeviceCapabilitiesStateResponse(): YandexManageDeviceCapabilitiesStateResponse {
    return YandexManageDeviceCapabilitiesStateResponse(
        status = this["status"]?.jsonPrimitive?.content ?: "ok",
        requestId = this["request_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing request_id"),
        devices = this["payload"]?.jsonObject?.get("devices")?.jsonArray?.map { it.jsonObject }
            ?: throw IllegalArgumentException("Missing devices")
    )
}

fun JsonObject.toQuasarInfo(): QuasarInfo {
    return QuasarInfo(
        deviceId = this["device_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing device_id in QuasarInfo"),
        platform = this["platform"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing platform in QuasarInfo")
    )
}

fun JsonObject.toDevicePropertyObject(): DevicePropertyObject {
    return DevicePropertyObject(
        type = PropertyTypeWrapper((this["type"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing property type")).codifiedEnum()),
        reportable = this["reportable"]?.jsonPrimitive?.booleanOrNull ?: false,
        retrievable = this["retrievable"]?.jsonPrimitive?.booleanOrNull ?: false,
        parameters = this["parameters"]?.jsonObject?.toPropertyParameterObject()
            ?: throw IllegalArgumentException("Missing parameters"),
        state = this["state"]?.jsonObject?.toPropertyStateObjectData(),
        lastUpdated = this["last_updated"]?.jsonPrimitive?.floatOrNull ?: 0f
    )
}

fun JsonObject.toPropertyParameterObject(): PropertyParameterObject {
    val type = this["type"]?.jsonPrimitive?.content?.let { PropertyTypeWrapper(it.codifiedEnum()) }
        ?: throw IllegalArgumentException("Missing type in PropertyParameterObject")

    return when (type.type.knownOrNull()) {
        PropertyType.FLOAT -> toFloatPropertyParameterObject()
        PropertyType.EVENT -> toEventPropertyParameterObject()
        null -> throw IllegalArgumentException("Unknown property type")
    }
}

fun JsonObject.toFloatPropertyParameterObject(): FloatPropertyParameterObject {
    return FloatPropertyParameterObject(
        instance = this["instance"]?.jsonPrimitive?.content?.let { PropertyFunctionWrapper(it.codifiedEnum()) }
            ?: throw IllegalArgumentException("Missing instance in FloatPropertyParameterObject"),
        unit = this["unit"]?.jsonPrimitive?.content?.let { MeasurementUnitWrapper(it.codifiedEnum()) }
            ?: throw IllegalArgumentException("Missing unit in FloatPropertyParameterObject")
    )
}

fun JsonObject.toEventPropertyParameterObject(): EventPropertyParameterObject {
    return EventPropertyParameterObject(
        instance = this["instance"]?.jsonPrimitive?.content?.let { PropertyFunctionWrapper(it.codifiedEnum()) }
            ?: throw IllegalArgumentException("Missing instance in EventPropertyParameterObject"),
        events = this["events"]?.jsonArray?.map { it.jsonObject.toEventObject() }
            ?: throw IllegalArgumentException("Missing events in EventPropertyParameterObject")
    )
}

fun JsonObject.toEventObject(): EventObject {
    return EventObject(
        value = this["value"]?.jsonPrimitive?.content?.let { EventObjectValueWrapper(it.codifiedEnum()) }
            ?: throw IllegalArgumentException("Missing value in EventObject")
    )
}

fun JsonObject.toPropertyStateObjectData(): PropertyStateObjectData {
    val type = this["type"]?.jsonPrimitive?.content?.let { PropertyTypeWrapper(it.codifiedEnum()) }
        ?: throw IllegalArgumentException("Missing type in PropertyStateObjectData")

    return when (type.type.knownOrNull()) {
        PropertyType.FLOAT -> toFloatPropertyStateObjectData()
        PropertyType.EVENT -> toEventPropertyStateObjectData()
        null -> throw IllegalArgumentException("Unknown property type")
    }
}

fun JsonObject.toFloatPropertyStateObjectData(): FloatPropertyStateObjectData {
    return FloatPropertyStateObjectData(
        state = FloatPropertyState(
            propertyFunction = this["function"]?.jsonPrimitive?.content?.let { PropertyFunctionWrapper(it.codifiedEnum()) }
                ?: throw IllegalArgumentException("Missing function in FloatPropertyState"),
            propertyValue = FloatObjectValue(
                this["value"]?.jsonPrimitive?.float
                    ?: throw IllegalArgumentException("Missing value in FloatPropertyState")
            )
        )
    )
}

fun JsonObject.toEventPropertyStateObjectData(): EventPropertyStateObjectData {
    return EventPropertyStateObjectData(
        state = EventPropertyState(
            propertyFunction = this["function"]?.jsonPrimitive?.content?.let { PropertyFunctionWrapper(it.codifiedEnum()) }
                ?: throw IllegalArgumentException("Missing function in EventPropertyState"),
            propertyValue = this["value"]?.jsonObject?.toEventObject()
                ?: throw IllegalArgumentException("Missing value in EventPropertyState")
        )
    )
}

fun DeviceObject.toJson(): JsonObject {
    return buildJsonObject {
        put("id", id)
        put("name", name)
        putJsonArray("aliases") {
            aliases.forEach { add(JsonPrimitive(it)) }
        }
        put("type", type.toJson())
        put("external_id", externalId)
        put("skill_id", skillId)
        put("household_id", householdId)
        room?.let { put("room", it) }
        putJsonArray("groups") {
            groups.forEach { add(JsonPrimitive(it)) }
        }
        putJsonArray("capabilities") {
            capabilities.forEach { add(it.toJson()) }
        }
        putJsonArray("properties") {
            properties.forEach { add(it.toJson()) }
        }
        quasarInfo?.let { put("quasar_info", it.toJson()) }
    }
}

fun YandexDeviceStateResponse.toJson(): JsonObject {
    return buildJsonObject {
        put("status", status)
        put("request_id", requestId)
        put("id", id)
        put("name", name)
        putJsonArray("aliases") {
            aliases.forEach { add(JsonPrimitive(it)) }
        }
        put("type", type)
        put("state", state)
        putJsonArray("groups") {
            groups.forEach { add(JsonPrimitive(it)) }
        }
        room?.let { put("room", it) }
        put("external_id", externalId)
        put("skill_id", skillId)
        putJsonArray("capabilities") {
            capabilities.forEach { add(it) }
        }
        putJsonArray("properties") {
            properties.forEach { add(it) }
        }
        quasarInfo?.let { put("quasar_info", it) }
    }
}

fun DeviceTypeWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("type", type.code())
    }
}

fun DevicePropertyObject.toJson(): JsonObject {
    return buildJsonObject {
        put("type", type.toJson())
        put("reportable", reportable)
        put("retrievable", retrievable)
        put("parameters", parameters.toJson())
        state?.let { put("state", it.toJson()) }
        put("last_updated", lastUpdated)
    }
}

fun QuasarInfo.toJson(): JsonObject {
    return buildJsonObject {
        put("device_id", deviceId)
        put("platform", platform)
    }
}

fun CapabilityState.toJson(): JsonObject {
    return when (this) {
        is StateResultObject -> this.toJson()
        is CapabilityStateObjectData -> this.toJson()
        else -> throw IllegalArgumentException("Unknown CapabilityState type")
    }
}

// For PropertyTypeWrapper
fun PropertyTypeWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("type", type.code())
    }
}

// For PropertyParameterObject
fun PropertyParameterObject.toJson(): JsonObject {
    return when (this) {
        is FloatPropertyParameterObject -> this.toJson()
        is EventPropertyParameterObject -> this.toJson()
    }
}

fun FloatPropertyParameterObject.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toJson())
        put("unit", unit.toJson())
    }
}

fun EventPropertyParameterObject.toJson(): JsonObject {
    return buildJsonObject {
        put("instance", instance.toJson())
        putJsonArray("events") {
            events.forEach { add(it.toJson()) }
        }
    }
}

// For PropertyStateObjectData
fun PropertyStateObjectData.toJson(): JsonObject {
    return when (this) {
        is FloatPropertyStateObjectData -> this.toJson()
        is EventPropertyStateObjectData -> this.toJson()
    }
}

fun FloatPropertyStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("type", PropertyTypeWrapper(PropertyType.FLOAT.codifiedEnum()).toJson())
        put("state", state.toJson())
    }
}

fun EventPropertyStateObjectData.toJson(): JsonObject {
    return buildJsonObject {
        put("type", PropertyTypeWrapper(PropertyType.EVENT.codifiedEnum()).toJson())
        put("state", state.toJson())
    }
}

// Helper functions
fun FloatPropertyState.toJson(): JsonObject {
    return buildJsonObject {
        put("function", propertyFunction.toJson())
        put("value", propertyValue.toJson())
    }
}

fun EventPropertyState.toJson(): JsonObject {
    return buildJsonObject {
        put("function", propertyFunction.toJson())
        put("value", propertyValue.toJson())
    }
}

fun PropertyFunctionWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("function", function.code())
    }
}

fun FloatObjectValue.toJson(): JsonObject {
    return buildJsonObject {
        put("value", value)
    }
}

fun EventObject.toJson(): JsonObject {
    return buildJsonObject {
        put("value", value.toJson())
    }
}

fun EventObjectValueWrapper.toJson(): JsonObject {
    return buildJsonObject {
        put("value", value.code())
    }
}

fun YandexManageDeviceCapabilitiesStateResponse.toJson(): JsonObject {
    return buildJsonObject {
        put("status", status)
        put("request_id", requestId)
        putJsonArray("devices") {
            devices.forEach { add(it) }
        }
    }
}

fun YandexUserInfoResponse.toSmartHomeInfo(): SmartHomeInfo {
    return SmartHomeInfo(
        rooms = this.rooms.map { it.toRoomObject() }.toMutableList(),
        groups = this.groups.map { it.toGroupObject() }.toMutableList(),
        devices = this.devices.map { it.toDeviceObject() }.toMutableList(),
        scenarios = this.scenarios.map { it.toScenarioObject() }.toMutableList(),
        households = this.households.map { it.toHouseholdObject() }.toMutableList()
    )
}

fun DeviceObject.toDeviceActionsObject(): DeviceActionsObject {
    return DeviceActionsObject(
        id = this.id,
        actions = this.capabilities.map { it.toCapabilityObject() }.toMutableList()
    )
}

fun DeviceActionsObject.toJson(): JsonObject {
    return buildJsonObject {
        put("id", id)
        putJsonArray("capabilities") {
            actions.forEach { add(it.toJson()) }
        }
    }
}

fun JsonObject.toRoomObject(): RoomObject {
    return RoomObject(
        id = this["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing id"),
        name = this["name"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing name"),
        devices = this["devices"]?.jsonArray?.map { it.jsonPrimitive.content }?.toMutableList() ?: mutableListOf(),
        householdId = this["household_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing household_id")
    )
}

fun JsonObject.toGroupObject(): GroupObject {
    return GroupObject(
        id = this["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing id"),
        name = this["name"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing name"),
        aliases = this["aliases"]?.jsonArray?.map { it.jsonPrimitive.content }?.toMutableList() ?: mutableListOf(),
        type = this["type"]?.jsonPrimitive?.content?.let { DeviceTypeWrapper(it.codifiedEnum()) } ?: throw IllegalArgumentException("Missing type"),
        capabilities = this["capabilities"]?.jsonArray?.map { it.jsonObject.toGroupCapabilityObject() }?.toMutableList() ?: mutableListOf(),
        devices = this["devices"]?.jsonArray?.map { it.jsonPrimitive.content }?.toMutableList() ?: mutableListOf(),
        householdId = this["household_id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing household_id")
    )
}

fun JsonObject.toScenarioObject(): ScenarioObject {
    return ScenarioObject(
        id = this["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing id"),
        name = this["name"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing name"),
        isActive = this["is_active"]?.jsonPrimitive?.boolean ?: false
    )
}

fun JsonObject.toHouseholdObject(): HouseholdObject {
    return HouseholdObject(
        id = this["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing id"),
        name = this["name"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing name"),
        type = this["type"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing type")
    )
}

fun DeviceCapabilityObject.toCapabilityObject(): CapabilityObject {
    return CapabilityObject(
        type = this.type,
        state = this.state
    )
}

fun JsonObject.toDeviceActionsResultObject(): DeviceActionsResultObject {
    return DeviceActionsResultObject(
        id = this["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing id"),
        capabilities = this["capabilities"]?.jsonArray?.mapNotNull { it.toCapabilityActionResultObject() }?.toMutableList()
            ?: mutableListOf()
    )
}

fun JsonObject.toDeviceActionsObject(): DeviceActionsObject {
    return DeviceActionsObject(
        id = this["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing id"),
        actions = this["capabilities"]?.jsonArray?.mapNotNull { it.toCapabilityObject() }?.toMutableList()
            ?: mutableListOf()
    )
}

fun JsonElement.asJsonObjectOrNull(): JsonObject? {
    return when (this) {
        is JsonObject -> this
        else -> null
    }
}

fun JsonElement.toDeviceCapabilityObject(): DeviceCapabilityObject? {
    val jsonObject = when (this) {
        is JsonObject -> this
        is JsonPrimitive -> return null // Пропускаем примитивные значения
        else -> return null
    }

    val type = this["type"]?.jsonPrimitive?.contentOrNull ?: throw IllegalArgumentException("Missing capability type")
    val parameters = jsonObject["parameters"]?.jsonObject?.toCapabilityParameterObject(type)
    val state = jsonObject["state"]?.takeIf { it !is JsonNull }?.jsonObject?.toCapabilityStateObjectData()
    return parameters?.let {
        DeviceCapabilityObject(
            type = CapabilityTypeWrapper(type.codifiedEnum()),
            reportable = jsonObject["reportable"]?.jsonPrimitive?.boolean ?: false,
            retrievable = jsonObject["retrievable"]?.jsonPrimitive?.boolean ?: false,
            parameters = it,
            state = state,
            lastUpdated = jsonObject["last_updated"]?.jsonPrimitive?.float ?: 0.0f
        )
    }
}

fun JsonElement.toCapabilityActionResultObject(): CapabilityActionResultObject? {
    val jsonObject = when (this) {
        is JsonObject -> this
        is JsonPrimitive -> return null // Пропускаем примитивные значения
        else -> return null
    }

    return CapabilityActionResultObject(
        type = jsonObject["type"]?.jsonPrimitive?.content?.let { CapabilityTypeWrapper(it.codifiedEnum()) }
            ?: throw IllegalArgumentException("Missing type"),
        state = jsonObject["state"]?.jsonObject?.toCapabilityStateObjectData()
            ?: throw IllegalArgumentException("Missing state")
    )
}

fun JsonElement.toCapabilityObject(): CapabilityObject? {
    val jsonObject = when (this) {
        is JsonObject -> this
        is JsonPrimitive -> return null // Пропускаем примитивные значения
        else -> return null
    }

    return CapabilityObject(
        type = jsonObject["type"]?.jsonPrimitive?.content?.let { CapabilityTypeWrapper(it.codifiedEnum()) }
            ?: throw IllegalArgumentException("Missing type"),
        state = jsonObject["state"]?.jsonObject?.toCapabilityStateObjectData()
    )
}