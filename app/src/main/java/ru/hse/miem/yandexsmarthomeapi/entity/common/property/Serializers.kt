package ru.hse.miem.yandexsmarthomeapi.entity.common.property

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.entity.common.MeasurementUnitWrapper

object DevicePropertyObjectSerializer : KSerializer<DevicePropertyObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DevicePropertyObject") {
        element<PropertyTypeWrapper>("type")
        element<Boolean>("reportable")
        element<Boolean>("retrievable")
        element<PropertyParameterObject>("parameters")
        element<PropertyStateObjectData?>("state")
        element<Float>("last_updated")
    }

    override fun serialize(encoder: Encoder, value: DevicePropertyObject) {
        val jsonElement = buildJsonObject {
            put("type", Json.encodeToJsonElement(value.type))
            put("reportable", value.reportable)
            put("retrievable", value.retrievable)
            put("parameters", Json.encodeToJsonElement(value.parameters))
            value.state?.let { put("state", Json.encodeToJsonElement(it)) }
            put("last_updated", value.lastUpdated)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): DevicePropertyObject {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return DevicePropertyObject(
            type = Json.decodeFromJsonElement(jsonObject["type"]!!),
            reportable = jsonObject["reportable"]?.jsonPrimitive?.boolean ?: false,
            retrievable = jsonObject["retrievable"]?.jsonPrimitive?.boolean ?: false,
            parameters = Json.decodeFromJsonElement(jsonObject["parameters"]!!),
            state = jsonObject["state"]?.let { Json.decodeFromJsonElement(it) },
            lastUpdated = jsonObject["last_updated"]?.jsonPrimitive?.float ?: 0f
        )
    }
}

object PropertyObjectSerializer : KSerializer<PropertyObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PropertyObject") {
        element<PropertyTypeWrapper>("type")
        element<Boolean>("retrievable")
        element<PropertyParameterObject>("parameters")
        element<PropertyStateObjectData?>("state")
        element<Float>("last_updated")
    }

    override fun serialize(encoder: Encoder, value: PropertyObject) {
        val jsonElement = buildJsonObject {
            put("type", Json.encodeToJsonElement(value.type))
            put("retrievable", value.retrievable)
            put("parameters", Json.encodeToJsonElement(value.parameters))
            value.state?.let { put("state", Json.encodeToJsonElement(it)) }
            put("last_updated", value.lastUpdated)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): PropertyObject {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return PropertyObject(
            type = Json.decodeFromJsonElement(jsonObject["type"]!!),
            retrievable = jsonObject["retrievable"]?.jsonPrimitive?.boolean ?: false,
            parameters = Json.decodeFromJsonElement(jsonObject["parameters"]!!),
            state = jsonObject["state"]?.let { Json.decodeFromJsonElement(it) },
            lastUpdated = jsonObject["last_updated"]?.jsonPrimitive?.float ?: 0f
        )
    }
}

object PropertyParameterObjectSerializer : KSerializer<PropertyParameterObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PropertyParameterObject")

    override fun serialize(encoder: Encoder, value: PropertyParameterObject) {
        val jsonElement = when (value) {
            is FloatPropertyParameterObject -> Json.encodeToJsonElement(FloatPropertyParameterObjectSerializer, value)
            is EventPropertyParameterObject -> Json.encodeToJsonElement(EventPropertyParameterObjectSerializer, value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): PropertyParameterObject {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when (jsonObject["instance"]?.jsonObject?.get("function")?.jsonPrimitive?.content) {
            "voice_activity", "amperage", "battery_level", "co2_level", "electricity_meter", "food_level",
            "gas_meter", "heat_meter", "humidity", "illumination", "meter", "pm10_density", "pm1_density",
            "pm2.5_density", "power", "pressure", "temperature", "tvoc", "voltage", "water_level", "water_meter" ->
                Json.decodeFromJsonElement(FloatPropertyParameterObjectSerializer, jsonElement)
            "vibration", "open", "button", "motion", "smoke", "gas", "water_leak", "custom" ->
                Json.decodeFromJsonElement(EventPropertyParameterObjectSerializer, jsonElement)
            else -> throw IllegalArgumentException("Unknown PropertyParameterObject type")
        }
    }
}

object FloatPropertyParameterObjectSerializer : KSerializer<FloatPropertyParameterObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FloatPropertyParameterObject") {
        element<PropertyFunctionWrapper>("instance")
        element<MeasurementUnitWrapper>("unit")
    }

    override fun serialize(encoder: Encoder, value: FloatPropertyParameterObject) {
        val jsonElement = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("unit", Json.encodeToJsonElement(value.unit))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): FloatPropertyParameterObject {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return FloatPropertyParameterObject(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            unit = Json.decodeFromJsonElement(jsonObject["unit"]!!)
        )
    }
}

object EventPropertyParameterObjectSerializer : KSerializer<EventPropertyParameterObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("EventPropertyParameterObject") {
        element<PropertyFunctionWrapper>("instance")
        element<List<EventObject>>("events")
    }

    override fun serialize(encoder: Encoder, value: EventPropertyParameterObject) {
        val jsonElement = buildJsonObject {
            put("instance", Json.encodeToJsonElement(value.instance))
            put("events", Json.encodeToJsonElement(value.events))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): EventPropertyParameterObject {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return EventPropertyParameterObject(
            instance = Json.decodeFromJsonElement(jsonObject["instance"]!!),
            events = Json.decodeFromJsonElement(jsonObject["events"]!!)
        )
    }
}

object EventObjectSerializer : KSerializer<EventObject> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("EventObject") {
        element<EventObjectValueWrapper>("value")
    }

    override fun serialize(encoder: Encoder, value: EventObject) {
        val jsonElement = buildJsonObject {
            put("value", Json.encodeToJsonElement(value.value))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): EventObject {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return EventObject(
            value = Json.decodeFromJsonElement(jsonObject["value"]!!)
        )
    }
}

object FloatObjectValueSerializer : KSerializer<FloatObjectValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FloatObjectValue") {
        element<Float>("value")
    }

    override fun serialize(encoder: Encoder, value: FloatObjectValue) {
        val jsonElement = buildJsonObject {
            put("value", value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): FloatObjectValue {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return FloatObjectValue(
            value = jsonObject["value"]?.jsonPrimitive?.float ?: throw IllegalArgumentException("Missing float value")
        )
    }
}

object PropertyFunctionWrapperSerializer : KSerializer<PropertyFunctionWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PropertyFunctionWrapper") {
        element<String>("function")
    }

    override fun serialize(encoder: Encoder, value: PropertyFunctionWrapper) {
        val jsonElement = buildJsonObject {
            put("function", value.function.code())
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): PropertyFunctionWrapper {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return PropertyFunctionWrapper(
            function = jsonObject["function"]?.jsonPrimitive?.content?.let {
                PropertyFunction.entries.find { func -> func.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid property function")
        )
    }
}

object PropertyTypeWrapperSerializer : KSerializer<PropertyTypeWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PropertyTypeWrapper") {
        element<String>("type")
    }

    override fun serialize(encoder: Encoder, value: PropertyTypeWrapper) {
        val jsonElement = buildJsonObject {
            put("type", value.type.code())
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): PropertyTypeWrapper {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return PropertyTypeWrapper(
            type = jsonObject["type"]?.jsonPrimitive?.content?.let {
                PropertyType.entries.find { type -> type.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid property type")
        )
    }
}

object PropertyStateObjectDataSerializer : KSerializer<PropertyStateObjectData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PropertyStateObjectData")

    override fun serialize(encoder: Encoder, value: PropertyStateObjectData) {
        val jsonElement = when (value) {
            is FloatPropertyStateObjectData -> Json.encodeToJsonElement(FloatPropertyStateObjectDataSerializer, value)
            is EventPropertyStateObjectData -> Json.encodeToJsonElement(EventPropertyStateObjectDataSerializer, value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): PropertyStateObjectData {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when (jsonObject["type"]?.jsonObject?.get("type")?.jsonPrimitive?.content) {
            "devices.properties.float" -> Json.decodeFromJsonElement(FloatPropertyStateObjectDataSerializer, jsonElement)
            "devices.properties.event" -> Json.decodeFromJsonElement(EventPropertyStateObjectDataSerializer, jsonElement)
            else -> throw IllegalArgumentException("Unknown PropertyStateObjectData type")
        }
    }
}

object FloatPropertyStateObjectDataSerializer : KSerializer<FloatPropertyStateObjectData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FloatPropertyStateObjectData") {
        element<PropertyTypeWrapper>("type")
        element<FloatPropertyState>("state")
    }

    override fun serialize(encoder: Encoder, value: FloatPropertyStateObjectData) {
        val jsonElement = buildJsonObject {
            put("type", Json.encodeToJsonElement(value.type))
            put("state", Json.encodeToJsonElement(value.state))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): FloatPropertyStateObjectData {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return FloatPropertyStateObjectData(
            type = Json.decodeFromJsonElement(jsonObject["type"]!!),
            state = Json.decodeFromJsonElement(jsonObject["state"]!!)
        )
    }
}

object EventPropertyStateObjectDataSerializer : KSerializer<EventPropertyStateObjectData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("EventPropertyStateObjectData") {
        element<PropertyTypeWrapper>("type")
        element<EventPropertyState>("state")
    }

    override fun serialize(encoder: Encoder, value: EventPropertyStateObjectData) {
        val jsonElement = buildJsonObject {
            put("type", Json.encodeToJsonElement(value.type))
            put("state", Json.encodeToJsonElement(value.state))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): EventPropertyStateObjectData {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return EventPropertyStateObjectData(
            type = Json.decodeFromJsonElement(jsonObject["type"]!!),
            state = Json.decodeFromJsonElement(jsonObject["state"]!!)
        )
    }
}

object FloatPropertyStateSerializer : KSerializer<FloatPropertyState> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FloatPropertyState") {
        element<PropertyFunctionWrapper>("function")
        element<FloatObjectValue>("value")
    }

    override fun serialize(encoder: Encoder, value: FloatPropertyState) {
        val jsonElement = buildJsonObject {
            put("function", Json.encodeToJsonElement(value.propertyFunction))
            put("value", Json.encodeToJsonElement(value.propertyValue))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): FloatPropertyState {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return FloatPropertyState(
            propertyFunction = Json.decodeFromJsonElement(jsonObject["function"]!!),
            propertyValue = Json.decodeFromJsonElement(jsonObject["value"]!!)
        )
    }
}

object EventPropertyStateSerializer : KSerializer<EventPropertyState> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("EventPropertyState") {
        element<PropertyFunctionWrapper>("function")
        element<EventObject>("value")
    }

    override fun serialize(encoder: Encoder, value: EventPropertyState) {
        val jsonElement = buildJsonObject {
            put("function", Json.encodeToJsonElement(value.propertyFunction))
            put("value", Json.encodeToJsonElement(value.propertyValue))
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): EventPropertyState {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return EventPropertyState(
            propertyFunction = Json.decodeFromJsonElement(jsonObject["function"]!!),
            propertyValue = Json.decodeFromJsonElement(jsonObject["value"]!!)
        )
    }
}

object MeasurementUnitWrapperSerializer : KSerializer<MeasurementUnitWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MeasurementUnitWrapper") {
        element<String>("unit")
    }

    override fun serialize(encoder: Encoder, value: MeasurementUnitWrapper) {
        val jsonElement = buildJsonObject {
            put("unit", value.unit.code())
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): MeasurementUnitWrapper {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return MeasurementUnitWrapper(
            unit = jsonObject["unit"]?.jsonPrimitive?.content?.let {
                MeasurementUnit.entries.find { unit -> unit.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid measurement unit")
        )
    }
}

object EventObjectValueWrapperSerializer : KSerializer<EventObjectValueWrapper> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("EventObjectValueWrapper") {
        element<String>("value")
    }

    override fun serialize(encoder: Encoder, value: EventObjectValueWrapper) {
        val jsonElement = buildJsonObject {
            put("value", value.value.code())
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): EventObjectValueWrapper {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return EventObjectValueWrapper(
            value = jsonObject["value"]?.jsonPrimitive?.content?.let {
                EventObjectValue.entries.find { event -> event.code == it }?.codifiedEnum()
            } ?: throw IllegalArgumentException("Invalid event object value")
        )
    }
}

object PropertySerializer : KSerializer<Property> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Property") {
        element<PropertyTypeWrapper>("type")
        element<Boolean>("retrievable")
        element<PropertyParameterObject>("parameters")
        element<PropertyStateObjectData?>("state")
        element<Float>("last_updated")
    }

    override fun serialize(encoder: Encoder, value: Property) {
        val jsonElement = when (value) {
            is DevicePropertyObject -> Json.encodeToJsonElement(DevicePropertyObjectSerializer, value)
            is PropertyObject -> Json.encodeToJsonElement(PropertyObjectSerializer, value)
            else -> throw IllegalArgumentException("Unknown Property type")
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): Property {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when {
            "reportable" in jsonObject -> Json.decodeFromJsonElement(DevicePropertyObjectSerializer, jsonElement)
            else -> Json.decodeFromJsonElement(PropertyObjectSerializer, jsonElement)
        }
    }
}

object PropertyValueSerializer : KSerializer<PropertyValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PropertyValue")

    override fun serialize(encoder: Encoder, value: PropertyValue) {
        val jsonElement = when (value) {
            is EventObject -> Json.encodeToJsonElement(EventObjectSerializer, value)
            is FloatObjectValue -> Json.encodeToJsonElement(FloatObjectValueSerializer, value)
            else -> throw IllegalArgumentException("Unknown PropertyValue type")
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): PropertyValue {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when {
            "value" in jsonObject && jsonObject["value"] is JsonObject -> Json.decodeFromJsonElement(EventObjectSerializer, jsonElement)
            "value" in jsonObject && jsonObject["value"] is JsonPrimitive -> Json.decodeFromJsonElement(FloatObjectValueSerializer, jsonElement)
            else -> throw IllegalArgumentException("Unknown PropertyValue type")
        }
    }
}

object PropertyStateSerializer : KSerializer<PropertyState> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PropertyState") {
        element<PropertyFunctionWrapper>("function")
        element<PropertyValue>("value")
    }

    override fun serialize(encoder: Encoder, value: PropertyState) {
        val jsonElement = when (value) {
            is FloatPropertyState -> Json.encodeToJsonElement(FloatPropertyStateSerializer, value)
            is EventPropertyState -> Json.encodeToJsonElement(EventPropertyStateSerializer, value)
            else -> throw IllegalArgumentException("Unknown PropertyState type")
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), jsonElement)
    }

    override fun deserialize(decoder: Decoder): PropertyState {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject
        return when {
            jsonObject["value"] is JsonObject -> Json.decodeFromJsonElement(EventPropertyStateSerializer, jsonElement)
            jsonObject["value"] is JsonPrimitive -> Json.decodeFromJsonElement(FloatPropertyStateSerializer, jsonElement)
            else -> throw IllegalArgumentException("Unknown PropertyState type")
        }
    }
}