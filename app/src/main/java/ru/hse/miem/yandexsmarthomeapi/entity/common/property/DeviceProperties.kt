package ru.hse.miem.yandexsmarthomeapi.entity.common.property

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.brightinventions.codified.Codified
import pl.brightinventions.codified.enums.CodifiedEnum
import pl.brightinventions.codified.enums.codifiedEnum
import pl.brightinventions.codified.enums.serializer.codifiedEnumSerializer
import ru.hse.miem.yandexsmarthomeapi.entity.common.MeasurementUnitWrapper

@Serializable
data class DevicePropertyObject(
    override val type: PropertyTypeWrapper,
    @SerialName("reportable") val reportable: Boolean,
    override val retrievable: Boolean,
    override val parameters: PropertyParameterObject,
    override val state: PropertyStateObjectData?,
    @SerialName("last_updated") override val lastUpdated: Float,
) : Property

@Serializable
sealed interface Property{
    @SerialName("type") val type: PropertyTypeWrapper
    @SerialName("retrievable") val retrievable: Boolean
    @SerialName("parameters") val parameters: PropertyParameterObject
    @SerialName("state") val state: PropertyStateObjectData?
    @SerialName("last_updated") val lastUpdated: Float
}

@Serializable
data class PropertyObject(
    override val type: PropertyTypeWrapper,
    override val retrievable: Boolean,
    override val parameters: PropertyParameterObject,
    override val state: PropertyStateObjectData?,
    @SerialName("last_updated") override val lastUpdated: Float
) : Property

@Serializable
sealed class PropertyParameterObject {
    abstract val instance: PropertyFunctionWrapper
}

@Serializable
@SerialName("float")
data class FloatPropertyParameterObject(
    override val instance: PropertyFunctionWrapper,
    val unit: MeasurementUnitWrapper
) : PropertyParameterObject()

enum class MeasurementUnit(override val code: String) : Codified<String> {
    TEMPERATURE_CELSIUS("unit.temperature.celsius"),
    AMPERE("unit.ampere"),
    CUBIC_METER("unit.cubic_meter"),
    GIGACALORIE("unit.gigacalorie"),
    KILOWATT_HOUR("unit.kilowatt_hour"),
    LUX("unit.illumination.lux"),
    MCG_M3("unit.density.mcg_m3"),
    PERCENT("unit.percent"),
    PPM("unit.ppm"),
    VOLT("unit.volt"),
    WATT("unit.watt");
    object CodifiedSerializer : KSerializer<CodifiedEnum<MeasurementUnit, String>> by codifiedEnumSerializer()
}

@Serializable
@SerialName("event")
data class EventPropertyParameterObject(
    override val instance: PropertyFunctionWrapper,
    val events: List<EventObject>
) : PropertyParameterObject()

@Serializable
sealed interface PropertyValue

enum class EventObjectValue(override val code: String) : Codified<String> {
    SPEECH_FINISHED("speech_finished"),
    TILT("tilt"),
    FALL("fall"),
    VIBRATION("vibration"),
    OPENED("opened"),
    CLOSED("closed"),
    CLICK("click"),
    DOUBLE_CLICK("double_click"),
    LONG_PRESS("long_press"),
    DETECTED("detected"),
    NOT_DETECTED("not_detected"),
    HIGH("high"),
    LOW("low"),
    NORMAL("normal"),
    EMPTY("empty"),
    DRY("dry"),
    LEAK("leak");

    object CodifiedSerializer : KSerializer<CodifiedEnum<EventObjectValue, String>> by codifiedEnumSerializer()
}

@Serializable
data class EventObjectValueWrapper(
    @Serializable(with = EventObjectValue.CodifiedSerializer::class)
    val value: CodifiedEnum<EventObjectValue, String>
)

@Serializable
data class EventObject(val value: EventObjectValueWrapper): PropertyValue

@Serializable
data class FloatObjectValue(val value: Float) : PropertyValue

enum class PropertyFunction(override val code: String) : Codified<String> {
    VOICE_ACTIVITY("voice_activity"),
    AMPERAGE("amperage"),
    BATTERY_LEVEL("battery_level"),
    CO2_LEVEL("co2_level"),
    ELECTRICITY_METER("electricity_meter"),
    FOOD_LEVEL("food_level"),
    GAS_METER("gas_meter"),
    HEAT_METER("heat_meter"),
    HUMIDITY("humidity"),
    ILLUMINATION("illumination"),
    METER("meter"),
    PM10_DENSITY("pm10_density"),
    PM1_DENSITY("pm1_density"),
    PM2_5_DENSITY("pm2.5_density"),
    POWER("power"),
    PRESSURE("pressure"),
    TEMPERATURE("temperature"),
    TVOC("tvoc"),
    VOLTAGE("voltage"),
    WATER_LEVEL("water_level"),
    WATER_METER("water_meter"),
    VIBRATION("vibration"),
    OPEN("open"),
    BUTTON("button"),
    MOTION("motion"),
    SMOKE("smoke"),
    GAS("gas"),
    WATER_LEAK("water_leak"),
    CUSTOM("custom");
    object CodifiedSerializer : KSerializer<CodifiedEnum<PropertyFunction, String>> by codifiedEnumSerializer()
}

@Serializable
data class PropertyFunctionWrapper(
    @Serializable(with = PropertyFunction.CodifiedSerializer::class)
    val function: CodifiedEnum<PropertyFunction, String>
)

enum class PropertyType(override val code: String) : Codified<String> {
    FLOAT("devices.properties.float"),
    EVENT("devices.properties.event");
    object CodifiedSerializer : KSerializer<CodifiedEnum<PropertyType, String>> by codifiedEnumSerializer()
}

@Serializable
data class PropertyTypeWrapper(
    @Serializable(with = PropertyType.CodifiedSerializer::class)
    val type: CodifiedEnum<PropertyType, String>
)

@Serializable
sealed interface PropertyStateObjectData{
    val type: PropertyTypeWrapper
    val state: PropertyState
}

@Serializable
sealed interface PropertyState{
    val propertyFunction: PropertyFunctionWrapper
    val propertyValue: PropertyValue
}

@Serializable
data class FloatPropertyStateObjectData(
    override val type: PropertyTypeWrapper = PropertyTypeWrapper(PropertyType.FLOAT.codifiedEnum()),
    override val state: FloatPropertyState
) : PropertyStateObjectData

@Serializable
data class EventPropertyStateObjectData(
    override val type: PropertyTypeWrapper = PropertyTypeWrapper(PropertyType.EVENT.codifiedEnum()),
    override val state: EventPropertyState
) : PropertyStateObjectData

@Serializable
data class FloatPropertyState(
    override val propertyFunction: PropertyFunctionWrapper,
    override val propertyValue: FloatObjectValue
) : PropertyState

@Serializable
data class EventPropertyState(
    override val propertyFunction: PropertyFunctionWrapper,
    override val propertyValue: EventObject
) : PropertyState