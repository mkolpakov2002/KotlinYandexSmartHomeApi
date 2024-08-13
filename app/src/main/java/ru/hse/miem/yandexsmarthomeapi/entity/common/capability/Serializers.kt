package ru.hse.miem.yandexsmarthomeapi.entity.common.capability

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.entity.common.MeasurementUnitWrapper

// Вспомогательная функция для создания JsonObject
fun buildJsonObject(block: JsonObjectBuilder.() -> Unit): JsonObject = JsonObject(JsonObjectBuilder().apply(block).content)

class JsonObjectBuilder {
    val content = mutableMapOf<String, JsonElement>()
    fun put(key: String, value: JsonElement) {
        content[key] = value
    }
    fun put(key: String, value: String) = put(key, JsonPrimitive(value))
    fun put(key: String, value: Number) = put(key, JsonPrimitive(value))
    fun put(key: String, value: Boolean) = put(key, JsonPrimitive(value))
    fun putJsonArray(key: String, block: JsonArrayBuilder.() -> Unit) {
        content[key] = JsonArray(JsonArrayBuilder().apply(block).content)
    }
}

class JsonArrayBuilder {
    val content = mutableListOf<JsonElement>()
    fun add(element: JsonElement) {
        content.add(element)
    }
}

object GroupCapabilityObjectSerializer : KSerializer<GroupCapabilityObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GroupCapabilityObject") {
        element<CapabilityTypeWrapper>("type")
        element<Boolean>("retrievable")
        element<CapabilityParameterObject?>("parameters")
        element<CapabilityStateObjectData?>("state")
    }

    override fun serialize(encoder: Encoder, value: GroupCapabilityObject) {
        val jsonObject = buildJsonObject {
            put("type", Json.encodeToJsonElement(value.type))
            put("retrievable", value.retrievable)
            value.parameters?.let { put("parameters", Json.encodeToJsonElement(it)) }
            value.state?.let { put("state", Json.encodeToJsonElement(it)) }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): GroupCapabilityObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return GroupCapabilityObject(
            type = Json.decodeFromJsonElement(jsonObject["type"]!!),
            retrievable = jsonObject["retrievable"]?.jsonPrimitive?.boolean ?: false,
            parameters = jsonObject["parameters"]?.let { Json.decodeFromJsonElement(it) },
            state = jsonObject["state"]?.let { Json.decodeFromJsonElement(it) }
        )
    }
}

object CapabilityTypeWrapperSerializer : KSerializer<CapabilityTypeWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CapabilityTypeWrapper") {
        element<String>("type")
    }

    override fun serialize(encoder: Encoder, value: CapabilityTypeWrapper) {
        val jsonObject = buildJsonObject {
            put("type", value.type.code())
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): CapabilityTypeWrapper {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return CapabilityTypeWrapper(
            type = jsonObject["type"]?.jsonPrimitive?.content?.let {
                CapabilityType.entries.find { type -> type.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid capability type")
        )
    }
}

object DeviceCapabilityObjectSerializer : KSerializer<DeviceCapabilityObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DeviceCapabilityObject") {
        element<CapabilityTypeWrapper>("type")
        element<Boolean>("reportable")
        element<Boolean>("retrievable")
        element<CapabilityParameterObject>("parameters")
        element<CapabilityStateObjectData?>("state")
        element<Float>("lastUpdated")
    }

    override fun serialize(encoder: Encoder, value: DeviceCapabilityObject) {
        val jsonObject = buildJsonObject {
            put("type", Json.encodeToJsonElement(value.type))
            put("reportable", value.reportable)
            put("retrievable", value.retrievable)
            put("parameters", Json.encodeToJsonElement(value.parameters))
            value.state?.let { put("state", Json.encodeToJsonElement(it)) }
            put("lastUpdated", value.lastUpdated)
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): DeviceCapabilityObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        val type = jsonObject["type"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing capability type")
        return DeviceCapabilityObject(
            type = Json.decodeFromJsonElement(jsonObject["type"]!!),
            reportable = jsonObject["reportable"]?.jsonPrimitive?.boolean ?: false,
            retrievable = jsonObject["retrievable"]?.jsonPrimitive?.boolean ?: false,
            parameters = Json.decodeFromJsonElement(jsonObject["parameters"]!!),
            state = jsonObject["state"]?.let { Json.decodeFromJsonElement(it) },
            lastUpdated = jsonObject["lastUpdated"]?.jsonPrimitive?.float ?: 0f
        )
    }
}

object CapabilityObjectSerializer : KSerializer<CapabilityObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CapabilityObject") {
        element<CapabilityTypeWrapper>("type")
        element<CapabilityStateObjectData?>("state")
    }

    override fun serialize(encoder: Encoder, value: CapabilityObject) {
        val jsonObject = buildJsonObject {
            put("type", Json.encodeToJsonElement(value.type))
            value.state?.let { put("state", Json.encodeToJsonElement(it)) }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): CapabilityObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return CapabilityObject(
            type = Json.decodeFromJsonElement(jsonObject["type"]!!),
            state = jsonObject["state"]?.let { Json.decodeFromJsonElement(it) }
        )
    }
}

object CapabilityActionResultObjectSerializer : KSerializer<CapabilityActionResultObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CapabilityActionResultObject") {
        element<CapabilityTypeWrapper>("type")
        element<CapabilityStateObjectData>("state")
    }

    override fun serialize(encoder: Encoder, value: CapabilityActionResultObject) {
        val jsonObject = buildJsonObject {
            put("type", Json.encodeToJsonElement(value.type))
            put("state", Json.encodeToJsonElement(value.state))
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): CapabilityActionResultObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return CapabilityActionResultObject(
            type = Json.decodeFromJsonElement(jsonObject["type"]!!),
            state = Json.decodeFromJsonElement(jsonObject["state"]!!)
        )
    }
}

object CapabilityParameterObjectSerializer : KSerializer<CapabilityParameterObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CapabilityParameterObject")

    override fun serialize(encoder: Encoder, value: CapabilityParameterObject) {
        val jsonElement = when (value) {
            is ColorSettingCapabilityParameterObject -> Json.encodeToJsonElement(ColorSettingCapabilityParameterObjectSerializer, value)
            is OnOffCapabilityParameterObject -> Json.encodeToJsonElement(OnOffCapabilityParameterObjectSerializer, value)
            is ModeCapabilityParameterObject -> Json.encodeToJsonElement(ModeCapabilityParameterObjectSerializer, value)
            is RangeCapabilityParameterObject -> Json.encodeToJsonElement(RangeCapabilityParameterObjectSerializer, value)
            is ToggleCapabilityParameterObject -> Json.encodeToJsonElement(ToggleCapabilityParameterObjectSerializer, value)
            is VideoStreamCapabilityParameterObject -> Json.encodeToJsonElement(VideoStreamCapabilityParameterObjectSerializer, value)
            is UnknownCapabilityParameterObject -> value.data
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): CapabilityParameterObject {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when (val type = jsonObject["type"]?.jsonPrimitive?.content) {
            "devices.capabilities.color_setting" -> Json.decodeFromJsonElement(ColorSettingCapabilityParameterObjectSerializer, jsonElement)
            "devices.capabilities.on_off" -> Json.decodeFromJsonElement(OnOffCapabilityParameterObjectSerializer, jsonElement)
            "devices.capabilities.mode" -> Json.decodeFromJsonElement(ModeCapabilityParameterObjectSerializer, jsonElement)
            "devices.capabilities.range" -> Json.decodeFromJsonElement(RangeCapabilityParameterObjectSerializer, jsonElement)
            "devices.capabilities.toggle" -> Json.decodeFromJsonElement(ToggleCapabilityParameterObjectSerializer, jsonElement)
            "devices.capabilities.video_stream" -> Json.decodeFromJsonElement(VideoStreamCapabilityParameterObjectSerializer, jsonElement)
            else -> UnknownCapabilityParameterObject(jsonObject)
        }
    }
}

object ColorSettingCapabilityParameterObjectSerializer : KSerializer<ColorSettingCapabilityParameterObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorSettingCapabilityParameterObject") {
        element<ColorModelWrapper?>("color_model")
        element<TemperatureK?>("temperature_k")
        element<ColorScene?>("color_scene")
    }

    override fun serialize(encoder: Encoder, value: ColorSettingCapabilityParameterObject) {
        val jsonObject = buildJsonObject {
            value.colorModel?.let { put("color_model", Json.encodeToJsonElement(it)) }
            value.temperatureK?.let { put("temperature_k", Json.encodeToJsonElement(it)) }
            value.colorScene?.let { put("color_scene", Json.encodeToJsonElement(it)) }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ColorSettingCapabilityParameterObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ColorSettingCapabilityParameterObject(
            colorModel = jsonObject["color_model"]?.let { Json.decodeFromJsonElement<ColorModelWrapper>(it) },
            temperatureK = jsonObject["temperature_k"]?.let { Json.decodeFromJsonElement<TemperatureK>(it) },
            colorScene = jsonObject["color_scene"]?.let { Json.decodeFromJsonElement<ColorScene>(it) }
        )
    }
}

object OnOffCapabilityParameterObjectSerializer : KSerializer<OnOffCapabilityParameterObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OnOffCapabilityParameterObject") {
        element<Boolean>("split")
    }

    override fun serialize(encoder: Encoder, value: OnOffCapabilityParameterObject) {
        val jsonObject = buildJsonObject {
            put("split", value.split)
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): OnOffCapabilityParameterObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return OnOffCapabilityParameterObject(
            split = jsonObject["split"]?.jsonPrimitive?.boolean ?: false
        )
    }
}

object ModeCapabilityParameterObjectSerializer : KSerializer<ModeCapabilityParameterObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ModeCapabilityParameterObject") {
        element<ModeCapabilityInstanceWrapper>("instance")
        element<List<ModeObject>>("modes")
    }

    override fun serialize(encoder: Encoder, value: ModeCapabilityParameterObject) {
        val jsonObject = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            putJsonArray("modes") {
                value.modes.forEach { add(Json.encodeToJsonElement(it)) }
            }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ModeCapabilityParameterObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ModeCapabilityParameterObject(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            modes = jsonObject["modes"]?.jsonArray?.map { Json.decodeFromJsonElement<ModeObject>(it) }
                ?: throw IllegalArgumentException("Missing modes")
        )
    }
}

object RangeCapabilityParameterObjectSerializer : KSerializer<RangeCapabilityParameterObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RangeCapabilityParameterObject") {
        element<RangeCapabilityWrapper>("instance")
        element<MeasurementUnitWrapper?>("unit")
        element<Boolean>("random_access")
        element<Range?>("range")
        element<Boolean?>("looped")
    }

    override fun serialize(encoder: Encoder, value: RangeCapabilityParameterObject) {
        val jsonObject = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            value.unit?.let { put("unit", Json.encodeToJsonElement(it)) }
            put("random_access", value.randomAccess)
            value.range?.let { put("range", Json.encodeToJsonElement(it)) }
            value.looped?.let { put("looped", it) }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): RangeCapabilityParameterObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return RangeCapabilityParameterObject(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            unit = jsonObject["unit"]?.let { Json.decodeFromJsonElement(it) },
            randomAccess = jsonObject["random_access"]?.jsonPrimitive?.boolean ?: false,
            range = jsonObject["range"]?.let { Json.decodeFromJsonElement(it) },
            looped = jsonObject["looped"]?.jsonPrimitive?.booleanOrNull
        )
    }
}

object ToggleCapabilityParameterObjectSerializer : KSerializer<ToggleCapabilityParameterObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ToggleCapabilityParameterObject") {
        element<ToggleCapabilityWrapper>("instance")
    }

    override fun serialize(encoder: Encoder, value: ToggleCapabilityParameterObject) {
        val jsonObject = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ToggleCapabilityParameterObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ToggleCapabilityParameterObject(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!)
        )
    }
}

object VideoStreamCapabilityParameterObjectSerializer : KSerializer<VideoStreamCapabilityParameterObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("VideoStreamCapabilityParameterObject") {
        element<List<VideoStreamProtocolWrapper>>("protocols")
    }

    override fun serialize(encoder: Encoder, value: VideoStreamCapabilityParameterObject) {
        val jsonObject = buildJsonObject {
            putJsonArray("protocols") {
                value.protocols.forEach { add(Json.encodeToJsonElement(it)) }
            }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): VideoStreamCapabilityParameterObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return VideoStreamCapabilityParameterObject(
            protocols = jsonObject["protocols"]?.jsonArray?.map { Json.decodeFromJsonElement<VideoStreamProtocolWrapper>(it) }
                ?: throw IllegalArgumentException("Missing protocols")
        )
    }
}

object ColorModelWrapperSerializer : KSerializer<ColorModelWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorModelWrapper") {
        element<String>("colorModel")
    }

    override fun serialize(encoder: Encoder, value: ColorModelWrapper) {
        val jsonObject = buildJsonObject {
            put("colorModel", value.colorModel.code())
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ColorModelWrapper {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ColorModelWrapper(
            colorModel = jsonObject["colorModel"]?.jsonPrimitive?.content?.let {
                ColorModel.entries.find { model -> model.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid color model")
        )
    }
}

object TemperatureKSerializer : KSerializer<TemperatureK> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TemperatureK") {
        element<Int>("min")
        element<Int>("max")
    }

    override fun serialize(encoder: Encoder, value: TemperatureK) {
        val jsonObject = buildJsonObject {
            put("min", value.min)
            put("max", value.max)
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): TemperatureK {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return TemperatureK(
            min = jsonObject["min"]?.jsonPrimitive?.int ?: throw IllegalArgumentException("Missing min"),
            max = jsonObject["max"]?.jsonPrimitive?.int ?: throw IllegalArgumentException("Missing max")
        )
    }
}

object ColorSceneSerializer : KSerializer<ColorScene> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorScene") {
        element<List<Scene>>("scenes")
    }

    override fun serialize(encoder: Encoder, value: ColorScene) {
        val jsonObject = buildJsonObject {
            putJsonArray("scenes") {
                value.scenes.forEach { add(Json.encodeToJsonElement(it)) }
            }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ColorScene {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ColorScene(
            scenes = jsonObject["scenes"]?.jsonArray?.map { Json.decodeFromJsonElement<Scene>(it) }
                ?: throw IllegalArgumentException("Missing scenes")
        )
    }
}

object SceneSerializer : KSerializer<Scene> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Scene") {
        element<SceneObjectWrapper>("id")
    }

    override fun serialize(encoder: Encoder, value: Scene) {
        val jsonObject = buildJsonObject {
            put("id", Json.encodeToJsonElement(value.id))
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): Scene {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return Scene(
            id = Json.decodeFromJsonElement(jsonObject["id"]!!)
        )
    }
}

object SceneObjectWrapperSerializer : KSerializer<SceneObjectWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SceneObjectWrapper") {
        element<String>("scene")
    }

    override fun serialize(encoder: Encoder, value: SceneObjectWrapper) {
        val jsonObject = buildJsonObject {
            put("scene", value.scene.code())
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): SceneObjectWrapper {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return SceneObjectWrapper(
            scene = jsonObject["scene"]?.jsonPrimitive?.content?.let {
                SceneObject.entries.find { scene -> scene.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid scene")
        )
    }
}

object ModeObjectSerializer : KSerializer<ModeObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ModeObject") {
        element<ModeCapabilityModeWrapper>("value")
    }

    override fun serialize(encoder: Encoder, value: ModeObject) {
        val jsonObject = buildJsonObject {
            put("value", Json.encodeToJsonElement(value.value))
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ModeObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ModeObject(
            value = Json.decodeFromJsonElement(jsonObject["value"]!!)
        )
    }
}

object ModeCapabilityModeWrapperSerializer : KSerializer<ModeCapabilityModeWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ModeCapabilityModeWrapper") {
        element<String>("mode")
    }

    override fun serialize(encoder: Encoder, value: ModeCapabilityModeWrapper) {
        val jsonObject = buildJsonObject {
            put("mode", value.mode.code())
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ModeCapabilityModeWrapper {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ModeCapabilityModeWrapper(
            mode = jsonObject["mode"]?.jsonPrimitive?.content?.let {
                ModeCapabilityMode.entries.find { mode -> mode.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid mode capability mode")
        )
    }
}

object ModeCapabilityInstanceWrapperSerializer : KSerializer<ModeCapabilityInstanceWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ModeCapabilityInstanceWrapper") {
        element<String>("mode")
    }

    override fun serialize(encoder: Encoder, value: ModeCapabilityInstanceWrapper) {
        val jsonObject = buildJsonObject {
            put("mode", value.mode.code())
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ModeCapabilityInstanceWrapper {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ModeCapabilityInstanceWrapper(
            mode = jsonObject["mode"]?.jsonPrimitive?.content?.let {
                ModeCapability.entries.find { mode -> mode.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid mode capability")
        )
    }
}

object RangeCapabilityWrapperSerializer : KSerializer<RangeCapabilityWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RangeCapabilityWrapper") {
        element<String>("range")
    }

    override fun serialize(encoder: Encoder, value: RangeCapabilityWrapper) {
        val jsonObject = buildJsonObject {
            put("range", value.range.code())
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): RangeCapabilityWrapper {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return RangeCapabilityWrapper(
            range = jsonObject["range"]?.jsonPrimitive?.content?.let {
                RangeCapability.entries.find { range -> range.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid range capability")
        )
    }
}

object RangeSerializer : KSerializer<Range> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Range") {
        element<Float>("min")
        element<Float>("max")
        element<Float>("precision")
    }

    override fun serialize(encoder: Encoder, value: Range) {
        val jsonObject = buildJsonObject {
            put("min", value.min)
            put("max", value.max)
            put("precision", value.precision)
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): Range {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return Range(
            min = jsonObject["min"]?.jsonPrimitive?.float ?: throw IllegalArgumentException("Missing min"),
            max = jsonObject["max"]?.jsonPrimitive?.float ?: throw IllegalArgumentException("Missing max"),
            precision = jsonObject["precision"]?.jsonPrimitive?.float ?: throw IllegalArgumentException("Missing precision")
        )
    }
}

object ToggleCapabilityWrapperSerializer : KSerializer<ToggleCapabilityWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ToggleCapabilityWrapper") {
        element<String>("toggle")
    }

    override fun serialize(encoder: Encoder, value: ToggleCapabilityWrapper) {
        val jsonObject = buildJsonObject {
            put("toggle", value.toggle.code())
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ToggleCapabilityWrapper {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ToggleCapabilityWrapper(
            toggle = jsonObject["toggle"]?.jsonPrimitive?.content?.let {
                ToggleCapability.entries.find { toggle -> toggle.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid toggle capability")
        )
    }
}

object StateResultObjectSerializer : KSerializer<StateResultObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("StateResultObject") {
        element<CapabilityStateObjectInstance>("instance")
        element<ActionResult>("action_result")
    }

    override fun serialize(encoder: Encoder, value: StateResultObject) {
        val jsonObject = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("action_result", Json.encodeToJsonElement(value.actionResult))
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): StateResultObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return StateResultObject(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            actionResult = Json.decodeFromJsonElement(jsonObject["action_result"]!!)
        )
    }
}

object CapabilityStateObjectDataSerializer : KSerializer<CapabilityStateObjectData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CapabilityStateObjectData")

    override fun serialize(encoder: Encoder, value: CapabilityStateObjectData) {
        val jsonElement = when (value) {
            is OnOffCapabilityStateObjectData -> Json.encodeToJsonElement(OnOffCapabilityStateObjectDataSerializer, value)
            is ColorSettingCapabilityStateObjectData -> Json.encodeToJsonElement(ColorSettingCapabilityStateObjectDataSerializer, value)
            is ModeCapabilityStateObjectData -> Json.encodeToJsonElement(ModeCapabilityStateObjectDataSerializer, value)
            is RangeCapabilityStateObjectData -> Json.encodeToJsonElement(RangeCapabilityStateObjectDataSerializer, value)
            is ToggleCapabilityStateObjectData -> Json.encodeToJsonElement(ToggleCapabilityStateObjectDataSerializer, value)
            is VideoStreamCapabilityStateObjectData -> Json.encodeToJsonElement(VideoStreamCapabilityStateObjectDataSerializer, value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): CapabilityStateObjectData {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when (val instance = jsonObject["instance"]?.jsonPrimitive?.content) {
            "on" -> Json.decodeFromJsonElement(OnOffCapabilityStateObjectDataSerializer, jsonElement)
            "rgb", "hsv", "temperature_k", "scene" -> Json.decodeFromJsonElement(ColorSettingCapabilityStateObjectDataSerializer, jsonElement)
            "thermostat", "fan_speed", "swing", "work_speed" -> Json.decodeFromJsonElement(ModeCapabilityStateObjectDataSerializer, jsonElement)
            "brightness", "channel", "humidity", "open", "temperature", "volume" -> Json.decodeFromJsonElement(RangeCapabilityStateObjectDataSerializer, jsonElement)
            "backlight", "controls_locked", "ionization", "keep_warm", "mute", "oscillation", "pause" -> Json.decodeFromJsonElement(ToggleCapabilityStateObjectDataSerializer, jsonElement)
            "get_stream" -> Json.decodeFromJsonElement(VideoStreamCapabilityStateObjectDataSerializer, jsonElement)
            else -> throw IllegalArgumentException("Unknown capability state instance: $instance")
        }
    }
}

object OnOffCapabilityStateObjectDataSerializer : KSerializer<OnOffCapabilityStateObjectData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OnOffCapabilityStateObjectData") {
        element<OnOffCapabilityStateObjectInstanceWrapper>("instance")
        element<OnOffCapabilityStateObjectValue>("value")
    }

    override fun serialize(encoder: Encoder, value: OnOffCapabilityStateObjectData) {
        val jsonObject = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("value", Json.encodeToJsonElement(value.value))
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): OnOffCapabilityStateObjectData {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return OnOffCapabilityStateObjectData(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            value = Json.decodeFromJsonElement(jsonObject["value"]!!)
        )
    }
}

object ColorSettingCapabilityStateObjectDataSerializer : KSerializer<ColorSettingCapabilityStateObjectData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorSettingCapabilityStateObjectData") {
        element<ColorSettingCapabilityStateObjectInstanceWrapper>("instance")
        element<ColorSettingCapabilityStateObjectValue>("value")
    }

    override fun serialize(encoder: Encoder, value: ColorSettingCapabilityStateObjectData) {
        val jsonObject = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("value", Json.encodeToJsonElement(value.value))
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ColorSettingCapabilityStateObjectData {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ColorSettingCapabilityStateObjectData(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            value = Json.decodeFromJsonElement(jsonObject["value"]!!)
        )
    }
}

object ModeCapabilityStateObjectDataSerializer : KSerializer<ModeCapabilityStateObjectData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ModeCapabilityStateObjectData") {
        element<ModeCapabilityInstanceWrapper>("instance")
        element<ModeCapabilityModeWrapper>("value")
    }

    override fun serialize(encoder: Encoder, value: ModeCapabilityStateObjectData) {
        val jsonObject = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("value", Json.encodeToJsonElement(value.value))
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ModeCapabilityStateObjectData {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ModeCapabilityStateObjectData(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            value = Json.decodeFromJsonElement(jsonObject["value"]!!)
        )
    }
}

object RangeCapabilityStateObjectDataSerializer : KSerializer<RangeCapabilityStateObjectData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RangeCapabilityStateObjectData") {
        element<RangeCapabilityWrapper>("instance")
        element<RangeCapabilityStateObjectDataValue>("value")
        element<Boolean?>("relative")
    }

    override fun serialize(encoder: Encoder, value: RangeCapabilityStateObjectData) {
        val jsonObject = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("value", Json.encodeToJsonElement(value.value))
            value.relative?.let { put("relative", it) }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): RangeCapabilityStateObjectData {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return RangeCapabilityStateObjectData(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            value = Json.decodeFromJsonElement(jsonObject["value"]!!),
            relative = jsonObject["relative"]?.jsonPrimitive?.booleanOrNull
        )
    }
}

object ToggleCapabilityStateObjectDataSerializer : KSerializer<ToggleCapabilityStateObjectData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ToggleCapabilityStateObjectData") {
        element<ToggleCapabilityWrapper>("instance")
        element<ToggleCapabilityStateObjectDataValue>("value")
    }

    override fun serialize(encoder: Encoder, value: ToggleCapabilityStateObjectData) {
        val jsonObject = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("value", Json.encodeToJsonElement(value.value))
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ToggleCapabilityStateObjectData {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ToggleCapabilityStateObjectData(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            value = Json.decodeFromJsonElement(jsonObject["value"]!!)
        )
    }
}

object VideoStreamCapabilityStateObjectDataSerializer : KSerializer<VideoStreamCapabilityStateObjectData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("VideoStreamCapabilityStateObjectData") {
        element<VideoStreamCapabilityStateObjectInstanceWrapper>("instance")
        element<CapabilityStateObjectValue>("value")
    }

    override fun serialize(encoder: Encoder, value: VideoStreamCapabilityStateObjectData) {
        val jsonObject = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("value", Json.encodeToJsonElement(value.value))
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): VideoStreamCapabilityStateObjectData {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return VideoStreamCapabilityStateObjectData(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            value = Json.decodeFromJsonElement(jsonObject["value"]!!)
        )
    }
}

object OnOffCapabilityStateObjectValueSerializer : KSerializer<OnOffCapabilityStateObjectValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OnOffCapabilityStateObjectValue") {
        element<Boolean>("value")
    }

    override fun serialize(encoder: Encoder, value: OnOffCapabilityStateObjectValue) {
        val jsonObject = buildJsonObject {
            put("value", value.value)
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): OnOffCapabilityStateObjectValue {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return OnOffCapabilityStateObjectValue(
            value = jsonObject["value"]?.jsonPrimitive?.boolean ?: throw IllegalArgumentException("Missing value")
        )
    }
}

object ColorSettingCapabilityStateObjectValueSerializer : KSerializer<ColorSettingCapabilityStateObjectValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorSettingCapabilityStateObjectValue")

    override fun serialize(encoder: Encoder, value: ColorSettingCapabilityStateObjectValue) {
        val jsonObject = when (value) {
            is ColorSettingCapabilityStateObjectValueRGB -> buildJsonObject {
                put("value", value.value)
            }
            is ColorSettingCapabilityStateObjectValueObjectScene -> buildJsonObject {
                put("value", Json.encodeToJsonElement(value.value))
            }
            is ColorSettingCapabilityStateObjectValueObjectHSV -> buildJsonObject {
                put("value", Json.encodeToJsonElement(value.value))
            }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ColorSettingCapabilityStateObjectValue {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return when {
            jsonObject.containsKey("value") && jsonObject["value"] is JsonPrimitive ->
                ColorSettingCapabilityStateObjectValueRGB(jsonObject["value"]!!.jsonPrimitive.int)
            jsonObject.containsKey("value") && jsonObject["value"] is JsonObject ->
                ColorSettingCapabilityStateObjectValueObjectScene(Json.decodeFromJsonElement(jsonObject["value"]!!))
            jsonObject.containsKey("h") && jsonObject.containsKey("s") && jsonObject.containsKey("v") ->
                ColorSettingCapabilityStateObjectValueObjectHSV(Json.decodeFromJsonElement(jsonObject))
            else -> throw IllegalArgumentException("Invalid ColorSettingCapabilityStateObjectValue")
        }
    }
}

object HSVObjectSerializer : KSerializer<HSVObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("HSVObject") {
        element<Int>("h")
        element<Int>("s")
        element<Int>("v")
    }

    override fun serialize(encoder: Encoder, value: HSVObject) {
        val jsonObject = buildJsonObject {
            put("h", value.h)
            put("s", value.s)
            put("v", value.v)
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): HSVObject {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return HSVObject(
            h = jsonObject["h"]?.jsonPrimitive?.int ?: throw IllegalArgumentException("Missing h"),
            s = jsonObject["s"]?.jsonPrimitive?.int ?: throw IllegalArgumentException("Missing s"),
            v = jsonObject["v"]?.jsonPrimitive?.int ?: throw IllegalArgumentException("Missing v")
        )
    }
}

object RangeCapabilityStateObjectDataValueSerializer : KSerializer<RangeCapabilityStateObjectDataValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RangeCapabilityStateObjectDataValue") {
        element<Float>("value")
    }

    override fun serialize(encoder: Encoder, value: RangeCapabilityStateObjectDataValue) {
        val jsonObject = buildJsonObject {
            put("value", value.value)
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): RangeCapabilityStateObjectDataValue {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return RangeCapabilityStateObjectDataValue(
            value = jsonObject["value"]?.jsonPrimitive?.float ?: throw IllegalArgumentException("Missing value")
        )
    }
}

object ToggleCapabilityStateObjectDataValueSerializer : KSerializer<ToggleCapabilityStateObjectDataValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ToggleCapabilityStateObjectDataValue") {
        element<Boolean>("value")
    }

    override fun serialize(encoder: Encoder, value: ToggleCapabilityStateObjectDataValue) {
        val jsonObject = buildJsonObject {
            put("value", value.value)
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ToggleCapabilityStateObjectDataValue {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ToggleCapabilityStateObjectDataValue(
            value = jsonObject["value"]?.jsonPrimitive?.boolean ?: throw IllegalArgumentException("Missing value")
        )
    }
}

object VideoStreamCapabilityStateObjectRequestValueSerializer : KSerializer<VideoStreamCapabilityStateObjectRequestValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("VideoStreamCapabilityStateObjectRequestValue") {
        element<List<VideoStreamProtocolWrapper>>("protocols")
    }

    override fun serialize(encoder: Encoder, value: VideoStreamCapabilityStateObjectRequestValue) {
        val jsonObject = buildJsonObject {
            putJsonArray("protocols") {
                value.protocols.forEach { add(Json.encodeToJsonElement(it)) }
            }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): VideoStreamCapabilityStateObjectRequestValue {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return VideoStreamCapabilityStateObjectRequestValue(
            protocols = jsonObject["protocols"]?.jsonArray?.map { Json.decodeFromJsonElement<VideoStreamProtocolWrapper>(it) }
                ?: throw IllegalArgumentException("Missing protocols")
        )
    }
}

object VideoStreamCapabilityStateObjectResponseValueSerializer : KSerializer<VideoStreamCapabilityStateObjectResponseValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("VideoStreamCapabilityStateObjectResponseValue") {
        element<String>("stream_url")
        element<VideoStreamProtocolWrapper>("protocol")
    }

    override fun serialize(encoder: Encoder, value: VideoStreamCapabilityStateObjectResponseValue) {
        val jsonObject = buildJsonObject {
            put("stream_url", value.streamUrl)
            put("protocol", Json.encodeToJsonElement(value.protocol))
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): VideoStreamCapabilityStateObjectResponseValue {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return VideoStreamCapabilityStateObjectResponseValue(
            streamUrl = jsonObject["stream_url"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing stream_url"),
            protocol = Json.decodeFromJsonElement(jsonObject["protocol"]!!)
        )
    }
}

object ActionResultSerializer : KSerializer<ActionResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ActionResult") {
        element<StatusWrapper>("status")
        element<ErrorCodeWrapper?>("error_code")
        element<String?>("error_message")
    }

    override fun serialize(encoder: Encoder, value: ActionResult) {
        val jsonObject = buildJsonObject {
            put("status", Json.encodeToJsonElement(value.status))
            value.errorCode?.let { put("error_code", Json.encodeToJsonElement(it)) }
            value.errorMessage?.let { put("error_message", it) }
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ActionResult {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ActionResult(
            status = Json.decodeFromJsonElement(jsonObject["status"]!!),
            errorCode = jsonObject["error_code"]?.let { Json.decodeFromJsonElement(it) },
            errorMessage = jsonObject["error_message"]?.jsonPrimitive?.contentOrNull
        )
    }
}

object StatusWrapperSerializer : KSerializer<StatusWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("StatusWrapper") {
        element<String>("status")
    }

    override fun serialize(encoder: Encoder, value: StatusWrapper) {
        val jsonObject = buildJsonObject {
            put("status", value.status.code())
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): StatusWrapper {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return StatusWrapper(
            status = jsonObject["status"]?.jsonPrimitive?.content?.let {
                Status.entries.find { status -> status.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid status")
        )
    }
}

object ErrorCodeWrapperSerializer : KSerializer<ErrorCodeWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ErrorCodeWrapper") {
        element<String>("errorCode")
    }

    override fun serialize(encoder: Encoder, value: ErrorCodeWrapper) {
        val jsonObject = buildJsonObject {
            put("errorCode", value.errorCode.code())
        }
        encoder.encodeSerializableValue(JsonObject.serializer(), jsonObject)
    }

    override fun deserialize(decoder: Decoder): ErrorCodeWrapper {
        val jsonObject = decoder.decodeSerializableValue(JsonObject.serializer())
        return ErrorCodeWrapper(
            errorCode = jsonObject["errorCode"]?.jsonPrimitive?.content?.let {
                ErrorCode.entries.find { errorCode -> errorCode.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid error code")
        )
    }
}

object CapabilityStateSerializer : KSerializer<CapabilityState> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CapabilityState") {
        element<CapabilityStateObjectInstance>("instance")
    }

    override fun serialize(encoder: Encoder, value: CapabilityState) {
        val jsonElement = when (value) {
            is StateResultObject -> Json.encodeToJsonElement(StateResultObjectSerializer, value)
            is CapabilityStateObjectData -> Json.encodeToJsonElement(CapabilityStateObjectDataSerializer, value)
            else -> throw IllegalArgumentException("Unknown CapabilityState type")
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): CapabilityState {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when {
            "action_result" in jsonObject -> Json.decodeFromJsonElement(StateResultObjectSerializer, jsonElement)
            else -> Json.decodeFromJsonElement(CapabilityStateObjectDataSerializer, jsonElement)
        }
    }
}

object CapabilityStateObjectActionResultSerializer : KSerializer<CapabilityStateObjectActionResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CapabilityStateObjectActionResult") {
        element<CapabilityStateObjectInstance>("instance")
        element<ActionResult>("action_result")
    }

    override fun serialize(encoder: Encoder, value: CapabilityStateObjectActionResult) {
        val jsonElement = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("action_result", Json.encodeToJsonElement(value.actionResult))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): CapabilityStateObjectActionResult {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when (val instance = jsonObject["instance"]?.jsonObject?.get("instance")?.jsonPrimitive?.content) {
            "on" -> Json.decodeFromJsonElement(OnOffCapabilityStateObjectActionResultSerializer, jsonElement)
            "rgb", "hsv", "temperature_k", "scene" -> Json.decodeFromJsonElement(ColorSettingCapabilityStateObjectActionResultSerializer, jsonElement)
            "thermostat", "fan_speed", "swing", "work_speed" -> Json.decodeFromJsonElement(ModeCapabilityStateObjectActionResultSerializer, jsonElement)
            "brightness", "channel", "humidity", "open", "temperature", "volume" -> Json.decodeFromJsonElement(RangeCapabilityStateObjectActionResultSerializer, jsonElement)
            "backlight", "controls_locked", "ionization", "keep_warm", "mute", "oscillation", "pause" -> Json.decodeFromJsonElement(ToggleCapabilityStateObjectActionResultSerializer, jsonElement)
            "get_stream" -> Json.decodeFromJsonElement(VideoStreamCapabilityStateObjectActionResultSerializer, jsonElement)
            else -> throw IllegalArgumentException("Unknown CapabilityStateObjectActionResult instance: $instance")
        }
    }
}

object CapabilityStateObjectInstanceSerializer : KSerializer<CapabilityStateObjectInstance> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CapabilityStateObjectInstance")

    override fun serialize(encoder: Encoder, value: CapabilityStateObjectInstance) {
        val jsonElement = when (value) {
            is OnOffCapabilityStateObjectInstanceWrapper -> Json.encodeToJsonElement(OnOffCapabilityStateObjectInstanceWrapperSerializer, value)
            is ColorSettingCapabilityStateObjectInstanceWrapper -> Json.encodeToJsonElement(ColorSettingCapabilityStateObjectInstanceWrapperSerializer, value)
            is ModeCapabilityInstanceWrapper -> Json.encodeToJsonElement(ModeCapabilityInstanceWrapperSerializer, value)
            is RangeCapabilityWrapper -> Json.encodeToJsonElement(RangeCapabilityWrapperSerializer, value)
            is ToggleCapabilityWrapper -> Json.encodeToJsonElement(ToggleCapabilityWrapperSerializer, value)
            is VideoStreamCapabilityStateObjectInstanceWrapper -> Json.encodeToJsonElement(VideoStreamCapabilityStateObjectInstanceWrapperSerializer, value)
            else -> throw IllegalArgumentException("Unknown CapabilityStateObjectInstance type")
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): CapabilityStateObjectInstance {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when {
            "on" in jsonObject -> Json.decodeFromJsonElement(OnOffCapabilityStateObjectInstanceWrapperSerializer, jsonElement)
            "rgb" in jsonObject || "hsv" in jsonObject || "temperature_k" in jsonObject || "scene" in jsonObject ->
                Json.decodeFromJsonElement(ColorSettingCapabilityStateObjectInstanceWrapperSerializer, jsonElement)
            "mode" in jsonObject -> Json.decodeFromJsonElement(ModeCapabilityInstanceWrapperSerializer, jsonElement)
            "range" in jsonObject -> Json.decodeFromJsonElement(RangeCapabilityWrapperSerializer, jsonElement)
            "toggle" in jsonObject -> Json.decodeFromJsonElement(ToggleCapabilityWrapperSerializer, jsonElement)
            "videoStream" in jsonObject -> Json.decodeFromJsonElement(VideoStreamCapabilityStateObjectInstanceWrapperSerializer, jsonElement)
            else -> throw IllegalArgumentException("Unknown CapabilityStateObjectInstance type")
        }
    }
}

object CapabilityStateObjectValueSerializer : KSerializer<CapabilityStateObjectValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CapabilityStateObjectValue")

    override fun serialize(encoder: Encoder, value: CapabilityStateObjectValue) {
        val jsonElement = when (value) {
            is OnOffCapabilityStateObjectValue -> Json.encodeToJsonElement(OnOffCapabilityStateObjectValueSerializer, value)
            is ColorSettingCapabilityStateObjectValue -> Json.encodeToJsonElement(ColorSettingCapabilityStateObjectValueSerializer, value)
            is ModeCapabilityModeWrapper -> Json.encodeToJsonElement(ModeCapabilityModeWrapperSerializer, value)
            is RangeCapabilityStateObjectDataValue -> Json.encodeToJsonElement(RangeCapabilityStateObjectDataValueSerializer, value)
            is ToggleCapabilityStateObjectDataValue -> Json.encodeToJsonElement(ToggleCapabilityStateObjectDataValueSerializer, value)
            is VideoStreamCapabilityStateObjectRequestValue -> Json.encodeToJsonElement(VideoStreamCapabilityStateObjectRequestValueSerializer, value)
            is VideoStreamCapabilityStateObjectResponseValue -> Json.encodeToJsonElement(VideoStreamCapabilityStateObjectResponseValueSerializer, value)
            else -> throw IllegalArgumentException("Unknown CapabilityStateObjectValue type")
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): CapabilityStateObjectValue {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when {
            "value" in jsonObject && jsonObject["value"] is JsonPrimitive -> {
                when (jsonObject["value"]?.jsonPrimitive?.content) {
                    "true", "false" -> Json.decodeFromJsonElement(OnOffCapabilityStateObjectValueSerializer, jsonElement)
                    else -> Json.decodeFromJsonElement(RangeCapabilityStateObjectDataValueSerializer, jsonElement)
                }
            }
            "value" in jsonObject && jsonObject["value"] is JsonObject -> Json.decodeFromJsonElement(ColorSettingCapabilityStateObjectValueSerializer, jsonElement)
            "mode" in jsonObject -> Json.decodeFromJsonElement(ModeCapabilityModeWrapperSerializer, jsonElement)
            "protocols" in jsonObject -> Json.decodeFromJsonElement(VideoStreamCapabilityStateObjectRequestValueSerializer, jsonElement)
            "stream_url" in jsonObject -> Json.decodeFromJsonElement(VideoStreamCapabilityStateObjectResponseValueSerializer, jsonElement)
            else -> throw IllegalArgumentException("Unknown CapabilityStateObjectValue type")
        }
    }
}

object OnOffCapabilityStateObjectInstanceWrapperSerializer : KSerializer<OnOffCapabilityStateObjectInstanceWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OnOffCapabilityStateObjectInstanceWrapper") {
        element<String>("instance")
    }

    override fun serialize(encoder: Encoder, value: OnOffCapabilityStateObjectInstanceWrapper) {
        val jsonElement = buildJsonObject {
            put("instance", value.onOff.code())
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): OnOffCapabilityStateObjectInstanceWrapper {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return OnOffCapabilityStateObjectInstanceWrapper(
            onOff = jsonObject["instance"]?.jsonPrimitive?.content?.let {
                OnOffCapabilityStateObjectInstance.entries.find { instance -> instance.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid OnOff instance")
        )
    }
}

object ColorSettingCapabilityStateObjectInstanceWrapperSerializer : KSerializer<ColorSettingCapabilityStateObjectInstanceWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorSettingCapabilityStateObjectInstanceWrapper") {
        element<String>("instance")
    }

    override fun serialize(encoder: Encoder, value: ColorSettingCapabilityStateObjectInstanceWrapper) {
        val jsonElement = buildJsonObject {
            put("instance", value.colorSetting.code())
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): ColorSettingCapabilityStateObjectInstanceWrapper {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return ColorSettingCapabilityStateObjectInstanceWrapper(
            colorSetting = jsonObject["instance"]?.jsonPrimitive?.content?.let {
                ColorSettingCapabilityStateObjectInstance.entries.find { instance -> instance.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid ColorSetting instance")
        )
    }
}

object ColorSettingCapabilityStateObjectValueRGBSerializer : KSerializer<ColorSettingCapabilityStateObjectValueRGB> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorSettingCapabilityStateObjectValueRGB") {
        element<Int>("value")
    }

    override fun serialize(encoder: Encoder, value: ColorSettingCapabilityStateObjectValueRGB) {
        val jsonElement = buildJsonObject {
            put("value", value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): ColorSettingCapabilityStateObjectValueRGB {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return ColorSettingCapabilityStateObjectValueRGB(
            value = jsonObject["value"]?.jsonPrimitive?.int ?: throw IllegalArgumentException("Missing or invalid RGB value")
        )
    }
}

object ColorSettingCapabilityStateObjectValueObjectSceneSerializer : KSerializer<ColorSettingCapabilityStateObjectValueObjectScene> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorSettingCapabilityStateObjectValueObjectScene") {
        element<SceneObjectWrapper>("value")
    }

    override fun serialize(encoder: Encoder, value: ColorSettingCapabilityStateObjectValueObjectScene) {
        val jsonElement = buildJsonObject {
            put("value", Json.encodeToJsonElement(value.value))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): ColorSettingCapabilityStateObjectValueObjectScene {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return ColorSettingCapabilityStateObjectValueObjectScene(
            value = Json.decodeFromJsonElement(jsonObject["value"]!!)
        )
    }
}

object ColorSettingCapabilityStateObjectValueObjectHSVSerializer : KSerializer<ColorSettingCapabilityStateObjectValueObjectHSV> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorSettingCapabilityStateObjectValueObjectHSV") {
        element<HSVObject>("value")
    }

    override fun serialize(encoder: Encoder, value: ColorSettingCapabilityStateObjectValueObjectHSV) {
        val jsonElement = buildJsonObject {
            put("value", Json.encodeToJsonElement(value.value))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): ColorSettingCapabilityStateObjectValueObjectHSV {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return ColorSettingCapabilityStateObjectValueObjectHSV(
            value = Json.decodeFromJsonElement(jsonObject["value"]!!)
        )
    }
}

object ModeCapabilityStateObjectActionResultSerializer : KSerializer<ModeCapabilityStateObjectActionResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ModeCapabilityStateObjectActionResult") {
        element<ModeCapabilityInstanceWrapper>("instance")
        element<ActionResult>("action_result")
    }

    override fun serialize(encoder: Encoder, value: ModeCapabilityStateObjectActionResult) {
        val jsonElement = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("action_result", Json.encodeToJsonElement(value.actionResult))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): ModeCapabilityStateObjectActionResult {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return ModeCapabilityStateObjectActionResult(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            actionResult = Json.decodeFromJsonElement(jsonObject["action_result"]!!)
        )
    }
}

object RangeCapabilityStateObjectActionResultSerializer : KSerializer<RangeCapabilityStateObjectActionResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RangeCapabilityStateObjectActionResult") {
        element<RangeCapabilityWrapper>("instance")
        element<ActionResult>("action_result")
    }

    override fun serialize(encoder: Encoder, value: RangeCapabilityStateObjectActionResult) {
        val jsonElement = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("action_result", Json.encodeToJsonElement(value.actionResult))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): RangeCapabilityStateObjectActionResult {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return RangeCapabilityStateObjectActionResult(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            actionResult = Json.decodeFromJsonElement(jsonObject["action_result"]!!)
        )
    }
}

object ToggleCapabilityStateObjectActionResultSerializer : KSerializer<ToggleCapabilityStateObjectActionResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ToggleCapabilityStateObjectActionResult") {
        element<ToggleCapabilityWrapper>("instance")
        element<ActionResult>("action_result")
    }

    override fun serialize(encoder: Encoder, value: ToggleCapabilityStateObjectActionResult) {
        val jsonElement = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("action_result", Json.encodeToJsonElement(value.actionResult))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): ToggleCapabilityStateObjectActionResult {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return ToggleCapabilityStateObjectActionResult(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            actionResult = Json.decodeFromJsonElement(jsonObject["action_result"]!!)
        )
    }
}

object VideoStreamProtocolWrapperSerializer : KSerializer<VideoStreamProtocolWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("VideoStreamProtocolWrapper") {
        element<String>("protocol")
    }

    override fun serialize(encoder: Encoder, value: VideoStreamProtocolWrapper) {
        val jsonElement = buildJsonObject {
            put("protocol", value.protocol.code())
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): VideoStreamProtocolWrapper {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return VideoStreamProtocolWrapper(
            protocol = jsonObject["protocol"]?.jsonPrimitive?.content?.let {
                VideoStreamProtocol.entries.find { protocol -> protocol.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid video stream protocol")
        )
    }
}

object VideoStreamCapabilityStateObjectInstanceWrapperSerializer : KSerializer<VideoStreamCapabilityStateObjectInstanceWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("VideoStreamCapabilityStateObjectInstanceWrapper") {
        element<String>("videoStream")
    }

    override fun serialize(encoder: Encoder, value: VideoStreamCapabilityStateObjectInstanceWrapper) {
        val jsonElement = buildJsonObject {
            put("videoStream", value.videoStream.code())
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): VideoStreamCapabilityStateObjectInstanceWrapper {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return VideoStreamCapabilityStateObjectInstanceWrapper(
            videoStream = jsonObject["videoStream"]?.jsonPrimitive?.content?.let {
                VideoStreamCapabilityStateObjectInstance.entries.find { instance -> instance.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid video stream capability state object instance")
        )
    }
}

object VideoStreamCapabilityStateObjectActionResultSerializer : KSerializer<VideoStreamCapabilityStateObjectActionResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("VideoStreamCapabilityStateObjectActionResult") {
        element<VideoStreamCapabilityStateObjectInstanceWrapper>("instance")
        element<VideoStreamCapabilityStateObjectResponseValue>("value")
        element<ActionResult>("action_result")
    }

    override fun serialize(encoder: Encoder, value: VideoStreamCapabilityStateObjectActionResult) {
        val jsonElement = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("value", Json.encodeToJsonElement(value.value))
            put("action_result", Json.encodeToJsonElement(value.actionResult))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): VideoStreamCapabilityStateObjectActionResult {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return VideoStreamCapabilityStateObjectActionResult(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            value = Json.decodeFromJsonElement(jsonObject["value"]!!),
            actionResult = Json.decodeFromJsonElement(jsonObject["action_result"]!!)
        )
    }
}

object OnOffCapabilityStateObjectActionResultSerializer : KSerializer<OnOffCapabilityStateObjectActionResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OnOffCapabilityStateObjectActionResult") {
        element<OnOffCapabilityStateObjectInstanceWrapper>("instance")
        element<ActionResult>("action_result")
    }

    override fun serialize(encoder: Encoder, value: OnOffCapabilityStateObjectActionResult) {
        val jsonElement = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("action_result", Json.encodeToJsonElement(value.actionResult))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): OnOffCapabilityStateObjectActionResult {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return OnOffCapabilityStateObjectActionResult(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            actionResult = Json.decodeFromJsonElement(jsonObject["action_result"]!!)
        )
    }
}

object ColorSettingCapabilityStateObjectActionResultSerializer : KSerializer<ColorSettingCapabilityStateObjectActionResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorSettingCapabilityStateObjectActionResult") {
        element<ColorSettingCapabilityStateObjectInstanceWrapper>("instance")
        element<ActionResult>("action_result")
    }

    override fun serialize(encoder: Encoder, value: ColorSettingCapabilityStateObjectActionResult) {
        val jsonElement = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("action_result", Json.encodeToJsonElement(value.actionResult))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): ColorSettingCapabilityStateObjectActionResult {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return ColorSettingCapabilityStateObjectActionResult(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            actionResult = Json.decodeFromJsonElement(jsonObject["action_result"]!!)
        )
    }
}
