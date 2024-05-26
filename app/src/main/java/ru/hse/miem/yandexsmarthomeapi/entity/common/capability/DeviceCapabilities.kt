package ru.hse.miem.yandexsmarthomeapi.entity.common.capability

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.brightinventions.codified.Codified
import pl.brightinventions.codified.enums.CodifiedEnum
import pl.brightinventions.codified.enums.codifiedEnum
import pl.brightinventions.codified.enums.serializer.codifiedEnumSerializer
import ru.hse.miem.yandexsmarthomeapi.entity.common.MeasurementUnitWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.MeasurementUnit

@Serializable
data class GroupCapabilityObject(
    @SerialName("type")
    val type: CapabilityTypeWrapper,
    @SerialName("retrievable") val retrievable: Boolean,
    @SerialName("parameters") val parameters: CapabilityParameterObject?,
    @SerialName("state") val state: CapabilityStateObjectData?
)

enum class CapabilityType(override val code: String) : Codified<String> {
    COLOR_SETTING("devices.capabilities.color_setting"),
    ON_OFF("devices.capabilities.on_off"),
    RANGE("devices.capabilities.range"),
    MODE("devices.capabilities.mode"),
    TOGGLE("devices.capabilities.toggle"),
    VIDEO_STREAM("devices.capabilities.video_stream");
    object CodifiedSerializer : KSerializer<CodifiedEnum<CapabilityType, String>> by codifiedEnumSerializer()
}

@Serializable
data class CapabilityTypeWrapper(
    @Serializable(with = CapabilityType.CodifiedSerializer::class)
    val type: CodifiedEnum<CapabilityType, String>
)

sealed interface Capability{
    @SerialName("type")  val type: CapabilityTypeWrapper
    @SerialName("state") val state: CapabilityStateObjectData?
}

@Serializable
data class DeviceCapabilityObject(
    override val type: CapabilityTypeWrapper,
    val reportable: Boolean,
    val retrievable: Boolean,
    val parameters: CapabilityParameterObject,
    override var state: CapabilityStateObjectData?,
    val lastUpdated: Float
) : Capability

@Serializable
data class CapabilityObject(
    override val type: CapabilityTypeWrapper,
    override val state: CapabilityStateObjectData?,
) : Capability

@Serializable
data class CapabilityActionResultObject(
    @SerialName("type") val type: CapabilityTypeWrapper,
    @SerialName("state") val state: StateResultObject
)

@Serializable
sealed class CapabilityParameterObject

@Serializable
data class ColorSettingCapabilityParameterObject(
    @SerialName("color_model")
    val colorModel: ColorModelWrapper? = null,
    @SerialName("temperature_k") val temperatureK: TemperatureK? = null,
    @SerialName("color_scene") val colorScene: ColorScene? = null
): CapabilityParameterObject() {
    init {
        require(colorModel != null || temperatureK != null || colorScene != null) {
            "one of color_model, temperature_k or color_scene must have a value"
        }
    }
}

@Serializable
data class OnOffCapabilityParameterObject(
    val split: Boolean
): CapabilityParameterObject()

@Serializable
data class ModeCapabilityParameterObject(
    val instance: ModeCapabilityInstanceWrapper,
    val modes: List<ModeObject>
): CapabilityParameterObject()

@Serializable
data class RangeCapabilityParameterObject(
    val instance: RangeCapabilityWrapper,
    var unit: MeasurementUnitWrapper? = null,
    @SerialName("random_access") val randomAccess: Boolean,
    val range: Range? = null,
    val looped: Boolean? = null
): CapabilityParameterObject() {
    init {
        unit = instance.range.knownOrNull()?.let {
            when (it) {
                RangeCapability.BRIGHTNESS,
                RangeCapability.HUMIDITY,
                RangeCapability.OPEN -> MeasurementUnitWrapper(MeasurementUnit.PERCENT.codifiedEnum())
                else -> MeasurementUnitWrapper(MeasurementUnit.TEMPERATURE_CELSIUS.codifiedEnum())
            }
        }
    }
}

@Serializable
data class ToggleCapabilityParameterObject(
    val instance: ToggleCapabilityWrapper
): CapabilityParameterObject()


@Serializable
data class VideoStreamCapabilityParameterObject(
    val protocols: List<VideoStreamCapabilityParameterObjectStreamProtocolWrapper>
): CapabilityParameterObject()

enum class ColorModel(override val code: String) : Codified<String> {
    RGB("rgb"),
    HSV("hsv");
    object CodifiedSerializer : KSerializer<CodifiedEnum<ColorModel, String>> by codifiedEnumSerializer()
}

@Serializable
data class ColorModelWrapper(
    @Serializable(with = ColorModel.CodifiedSerializer::class)
    val colorModel: CodifiedEnum<ColorModel, String>
)

@Serializable
data class TemperatureK(
    val min: Int,
    val max: Int
)

@Serializable
data class ColorScene(
    val scenes: List<Scene>
)

@Serializable
data class Scene(
    @SerialName("id")
    val id: SceneObjectWrapper
)

enum class SceneObject(override val code: String) : Codified<String> {
    ALARM("alarm"),
    ALICE("alice"),
    CANDLE("candle"),
    DINNER("dinner"),
    FANTASY("fantasy"),
    GARLAND("garland"),
    JUNGLE("jungle"),
    MOVIE("movie"),
    NEON("neon"),
    NIGHT("night"),
    OCEAN("ocean"),
    PARTY("party"),
    READING("reading"),
    REST("rest"),
    ROMANCE("romance"),
    SIREN("siren"),
    SUNRISE("sunrise"),
    SUNSET("sunset");
    object CodifiedSerializer : KSerializer<CodifiedEnum<SceneObject, String>> by codifiedEnumSerializer()
}

@Serializable
data class SceneObjectWrapper(
    @Serializable(with = SceneObject.CodifiedSerializer::class)
    val scene: CodifiedEnum<SceneObject, String>
)

@Serializable
data class ModeObject(val value: ModeCapabilityModeWrapper)

enum class ModeCapabilityMode(override val code: String) : Codified<String> {
    AUTO("auto"),
    ECO("eco"),
    SMART("smart"),
    TURBO("turbo"),
    COOL("cool"),
    DRY("dry"),
    FAN_ONLY("fan_only"),
    HEAT("heat"),
    PREHEAT("preheat"),
    HIGH("high"),
    LOW("low"),
    MEDIUM("medium"),
    MAX("max"),
    MIN("min"),
    FAST("fast"),
    SLOW("slow"),
    EXPRESS("express"),
    NORMAL("normal"),
    QUIET("quiet"),
    HORIZONTAL("horizontal"),
    STATIONARY("stationary"),
    VERTICAL("vertical"),
    ONE("one"),
    TWO("two"),
    THREE("three"),
    FOUR("four"),
    FIVE("five"),
    SIX("six"),
    SEVEN("seven"),
    EIGHT("eight"),
    NINE("nine"),
    TEN("ten"),
    AMERICANO("americano"),
    CAPPUCCINO("cappuccino"),
    DOUBLE("double"),
    ESPRESSO("espresso"),
    DOUBLE_ESPRESSO("double_espresso"),
    LATTE("latte"),
    BLACK_TEA("black_tea"),
    FLOWER_TEA("flower_tea"),
    GREEN_TEA("green_tea"),
    HERBAL_TEA("herbal_tea"),
    OOLONG_TEA("oolong_tea"),
    PUERH_TEA("puerh_tea"),
    RED_TEA("red_tea"),
    WHITE_TEA("white_tea"),
    GLASS("glass"),
    INTENSIVE("intensive"),
    PRE_RINSE("pre_rinse"),
    ASPIC("aspic"),
    BABY_FOOD("baby_food"),
    BAKING("baking"),
    BREAD("bread"),
    BOILING("boiling"),
    CEREALS("cereals"),
    CHEESECAKE("cheesecake"),
    DEEP_FRYER("deep_fryer"),
    DESSERT("dessert"),
    FOWL("fowl"),
    FRYING("frying"),
    MACARONI("macaroni"),
    MILK_PORRIDGE("milk_porridge"),
    MULTICOOKER("multicooker"),
    PASTA("pasta"),
    PILAF("pilaf"),
    PIZZA("pizza"),
    SAUCE("sauce"),
    SLOW_COOK("slow_cook"),
    SOUP("soup"),
    STEAM("steam"),
    STEWING("stewing"),
    VACUUM("vacuum"),
    YOGURT("yogurt");
    object CodifiedSerializer : KSerializer<CodifiedEnum<ModeCapabilityMode, String>> by codifiedEnumSerializer()
}

@Serializable
data class ModeCapabilityModeWrapper(
    @Serializable(with = ModeCapabilityMode.CodifiedSerializer::class)
    val mode: CodifiedEnum<ModeCapabilityMode, String>
) : CapabilityStateObjectValue

enum class ModeCapability(override val code: String) : Codified<String> {
    CLEANUP_MODE("cleanup_mode"),
    COFFEE_MODE("coffee_mode"),
    DISHWASHING("dishwashing"),
    FAN_SPEED("fan_speed"),
    HEAT("heat"),
    INPUT_SOURCE("input_source"),
    PROGRAM("program"),
    SWING("swing"),
    TEA_MODE("tea_mode"),
    THERMOSTAT("thermostat"),
    WORK_SPEED("work_speed");
    object CodifiedSerializer : KSerializer<CodifiedEnum<ModeCapability, String>> by codifiedEnumSerializer()
}

@Serializable
data class ModeCapabilityInstanceWrapper(
    @Serializable(with = ModeCapability.CodifiedSerializer::class)
    val mode: CodifiedEnum<ModeCapability, String>
) : CapabilityStateObjectInstance

enum class RangeCapability(override val code: String) : Codified<String> {
    BRIGHTNESS("brightness"),
    CHANNEL("channel"),
    HUMIDITY("humidity"),
    OPEN("open"),
    TEMPERATURE("temperature"),
    VOLUME("volume");
    object CodifiedSerializer : KSerializer<CodifiedEnum<RangeCapability, String>> by codifiedEnumSerializer()
}

@Serializable
data class RangeCapabilityWrapper(
    @Serializable(with = RangeCapability.CodifiedSerializer::class)
    val range: CodifiedEnum<RangeCapability, String>
) : CapabilityStateObjectInstance

@Serializable
data class Range(
    val min: Float,
    val max: Float,
    val precision: Float
) {
    override fun toString() = "[$min, $max]"
}

enum class ToggleCapability(override val code: String) : Codified<String> {
    BACKLIGHT("backlight"),
    CONTROLS_LOCKED("controls_locked"),
    IONIZATION("ionization"),
    KEEP_WARM("keep_warm"),
    MUTE("mute"),
    OSCILLATION("oscillation"),
    PAUSE("pause");
    object CodifiedSerializer : KSerializer<CodifiedEnum<ToggleCapability, String>> by codifiedEnumSerializer()
}

@Serializable
data class ToggleCapabilityWrapper(
    @Serializable(with = ToggleCapability.CodifiedSerializer::class)
    val toggle: CodifiedEnum<ToggleCapability, String>
) : CapabilityStateObjectInstance

enum class VideoStreamCapabilityParameterObjectStreamProtocol(override val code: String) :
    Codified<String> {
    HLS("hls"),
    RTMP("rtmp");
    object CodifiedSerializer : KSerializer<CodifiedEnum<VideoStreamCapabilityParameterObjectStreamProtocol, String>> by codifiedEnumSerializer()
}

@Serializable
data class VideoStreamCapabilityParameterObjectStreamProtocolWrapper(
    @Serializable(with = VideoStreamCapabilityParameterObjectStreamProtocol.CodifiedSerializer::class)
    val streamProtocol: CodifiedEnum<VideoStreamCapabilityParameterObjectStreamProtocol, String>
)

@Serializable
sealed interface CapabilityState {
    val instance: CapabilityStateObjectInstance
}

@Serializable
data class StateResultObject(
    override val instance: CapabilityStateObjectInstance,
    @SerialName("action_result") val actionResult: ActionResult
) : CapabilityState

@Serializable
sealed class CapabilityStateObjectData: CapabilityState {
    abstract override val instance: CapabilityStateObjectInstance
    abstract val value: CapabilityStateObjectValue
}

@Serializable
sealed interface CapabilityStateObjectActionResult : CapabilityState {
    abstract override val instance: CapabilityStateObjectInstance
    @SerialName("action_result") val actionResult: ActionResult
}

@Serializable
data class ActionResult(
    val status: StatusWrapper,
    @SerialName("error_code")
    val errorCode: ErrorCodeWrapper? = null,
    @SerialName("error_message") val errorMessage: String? = null
)

enum class ErrorCode(override val code: String) : Codified<String> {
    DOOR_OPEN("DOOR_OPEN"),
    LID_OPEN("LID_OPEN"),
    REMOTE_CONTROL_DISABLED("REMOTE_CONTROL_DISABLED"),
    NOT_ENOUGH_WATER("NOT_ENOUGH_WATER"),
    LOW_CHARGE_LEVEL("LOW_CHARGE_LEVEL"),
    CONTAINER_FULL("CONTAINER_FULL"),
    CONTAINER_EMPTY("CONTAINER_EMPTY"),
    DRIP_TRAY_FULL("DRIP_TRAY_FULL"),
    DEVICE_STUCK("DEVICE_STUCK"),
    DEVICE_OFF("DEVICE_OFF"),
    FIRMWARE_OUT_OF_DATE("FIRMWARE_OUT_OF_DATE"),
    NOT_ENOUGH_DETERGENT("NOT_ENOUGH_DETERGENT"),
    HUMAN_INVOLVEMENT_NEEDED("HUMAN_INVOLVEMENT_NEEDED"),
    DEVICE_UNREACHABLE("DEVICE_UNREACHABLE"),
    DEVICE_BUSY("DEVICE_BUSY"),
    INTERNAL_ERROR("INTERNAL_ERROR"),
    INVALID_ACTION("INVALID_ACTION"),
    INVALID_VALUE("INVALID_VALUE"),
    NOT_SUPPORTED_IN_CURRENT_MODE("NOT_SUPPORTED_IN_CURRENT_MODE"),
    ACCOUNT_LINKING_ERROR("ACCOUNT_LINKING_ERROR"),
    DEVICE_NOT_FOUND("DEVICE_NOT_FOUND");
    object CodifiedSerializer : KSerializer<CodifiedEnum<ErrorCode, String>> by codifiedEnumSerializer()
}

@Serializable
data class ErrorCodeWrapper(
    @Serializable(with = ErrorCode.CodifiedSerializer::class)
    val errorCode: CodifiedEnum<ErrorCode, String>
)

enum class Status(override val code: String) : Codified<String> {
    DONE("DONE"),
    ERROR("ERROR");
    object CodifiedSerializer : KSerializer<CodifiedEnum<Status, String>> by codifiedEnumSerializer()
}

@Serializable
data class StatusWrapper(
    @Serializable(with = Status.CodifiedSerializer::class)
    val status: CodifiedEnum<Status, String>
)

@Serializable
sealed interface CapabilityStateObjectInstance

@Serializable
sealed interface CapabilityStateObjectValue

@Serializable
data class OnOffCapabilityStateObjectData(
    override val instance: OnOffCapabilityStateObjectInstanceWrapper,
    override var value: OnOffCapabilityStateObjectValue
): CapabilityStateObjectData()

@Serializable
data class OnOffCapabilityStateObjectValue(val value: Boolean) : CapabilityStateObjectValue

@Serializable
enum class OnOffCapabilityStateObjectInstance(override val code: String) : Codified<String> {
    ON("on");
    object CodifiedSerializer : KSerializer<CodifiedEnum<OnOffCapabilityStateObjectInstance, String>> by codifiedEnumSerializer()
}

@Serializable
data class OnOffCapabilityStateObjectInstanceWrapper(
    @Serializable(with = OnOffCapabilityStateObjectInstance.CodifiedSerializer::class)
    val onOff: CodifiedEnum<OnOffCapabilityStateObjectInstance, String>
) : CapabilityStateObjectInstance

@Serializable
data class ColorSettingCapabilityStateObjectData(
    override val instance: ColorSettingCapabilityStateObjectInstanceWrapper,
    override var value: ColorSettingCapabilityStateObjectValue
): CapabilityStateObjectData()

@Serializable
enum class ColorSettingCapabilityStateObjectInstance(override val code: String) : Codified<String> {
    BASE("base"),
    RGB("rgb"),
    HSV("hsv"),
    TEMPERATURE_K("temperature_k"),
    SCENE("scene");
    object CodifiedSerializer : KSerializer<CodifiedEnum<ColorSettingCapabilityStateObjectInstance, String>> by codifiedEnumSerializer()
}

@Serializable
data class ColorSettingCapabilityStateObjectInstanceWrapper(
    @Serializable(with = ColorSettingCapabilityStateObjectInstance.CodifiedSerializer::class)
    val colorSetting: CodifiedEnum<ColorSettingCapabilityStateObjectInstance, String>
) : CapabilityStateObjectInstance

@Serializable
sealed interface ColorSettingCapabilityStateObjectValue : CapabilityStateObjectValue

@Serializable
data class ColorSettingCapabilityStateObjectValueInteger(val value: Int) :
    ColorSettingCapabilityStateObjectValue

@Serializable
data class ColorSettingCapabilityStateObjectValueObjectScene(
    val value: SceneObjectWrapper
) : ColorSettingCapabilityStateObjectValue

@Serializable
data class ColorSettingCapabilityStateObjectValueObjectHSV(val value: HSVObject)
    : ColorSettingCapabilityStateObjectValue

@Serializable
data class HSVObject(val h: Int, val s: Int, val v: Int)

@Serializable
data class VideoStreamCapabilityStateObjectData(
    override val instance: VideoStreamCapabilityStateObjectInstanceWrapper,
    override var value: VideoStreamCapabilityStateObjectDataValue
): CapabilityStateObjectData()

@Serializable
data class VideoStreamCapabilityStateObjectInstanceWrapper(
    @Serializable(with = VideoStreamCapabilityStateObjectInstance.CodifiedSerializer::class)
    val videoStream: CodifiedEnum<VideoStreamCapabilityStateObjectInstance, String>
): CapabilityStateObjectInstance

@Serializable
enum class VideoStreamCapabilityStateObjectInstance(override val code: String) : Codified<String> {
    GET_STREAM("get_stream");
    object CodifiedSerializer : KSerializer<CodifiedEnum<VideoStreamCapabilityStateObjectInstance, String>> by codifiedEnumSerializer()
}

@Serializable
data class VideoStreamCapabilityStateObjectDataValue(
    val protocols: List<VideoStreamCapabilityParameterObjectStreamProtocolWrapper>
) : CapabilityStateObjectValue

@Serializable
data class VideoStreamCapabilityStateObjectActionResultValue(
    val protocol: VideoStreamCapabilityParameterObjectStreamProtocolWrapper,
    @SerialName("stream_url") val streamUrl: String
) : CapabilityStateObjectValue

@Serializable
data class VideoStreamCapabilityStateObjectActionResult(
    override val instance: VideoStreamCapabilityStateObjectInstanceWrapper,
    val value: VideoStreamCapabilityStateObjectDataValue,
    override val actionResult: ActionResult
): CapabilityStateObjectActionResult

@Serializable
data class ModeCapabilityStateObjectData(
    override val instance: ModeCapabilityInstanceWrapper,
    override var value: ModeCapabilityModeWrapper
): CapabilityStateObjectData()

@Serializable
data class ModeCapabilityStateObjectActionResult(
    override val instance: ModeCapabilityInstanceWrapper,
    override val actionResult: ActionResult
): CapabilityStateObjectActionResult

@Serializable
data class RangeCapabilityStateObjectData(
    override val instance: RangeCapabilityWrapper,
    override var value: RangeCapabilityStateObjectDataValue,
    @SerialName("relative") val relative: Boolean? = null
): CapabilityStateObjectData()

@Serializable
data class RangeCapabilityStateObjectDataValue(
    val value: Float,
) : CapabilityStateObjectValue

@Serializable
data class RangeCapabilityStateObjectActionResult(
    override val instance: RangeCapabilityWrapper,
    override val actionResult: ActionResult
): CapabilityStateObjectActionResult

@Serializable
data class ToggleCapabilityStateObjectData(
    override val instance: ToggleCapabilityWrapper,
    override var value: ToggleCapabilityStateObjectDataValue
): CapabilityStateObjectData()

@Serializable
data class ToggleCapabilityStateObjectDataValue(
    val value: Boolean
) : CapabilityStateObjectValue

@Serializable
data class ToggleCapabilityStateObjectActionResult(
    override val instance: ToggleCapabilityWrapper,
    override val actionResult: ActionResult
): CapabilityStateObjectActionResult