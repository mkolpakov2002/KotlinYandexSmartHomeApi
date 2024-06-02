
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.TestConstants
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexDeviceStateResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexUserInfoResponse
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceType
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorModel
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueInteger
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.DeviceCapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.GroupCapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectDataValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.TemperatureK
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.DevicePropertyObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.toDeviceObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.toSmartHomeInfo
import ru.hse.miem.yandexsmarthomeapi.entity.common.toJson
import ru.hse.miem.yandexsmarthomeapi.entity.common.toYandexManageDeviceCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.common.toYandexManageGroupCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.common.toYandexUserInfoResponse

class YandexApiExtensionsTest {

    private val json = Json {
        prettyPrint = true
    }

    @Test
    fun `test mapUserInfoResponse`() {
        val response = json.decodeFromString<YandexUserInfoResponse>(TestConstants.responseUserInfoJson)
        val result = response.toSmartHomeInfo()

        assertEquals(4, result.rooms.size)
        assertEquals("ca82a680-0317-4bec-b92e-5c3dd27c61eb", result.rooms[0].id)
        assertEquals("Балкон", result.rooms[0].name)
        assertEquals(listOf("51e797a4-93cf-4bc4-832e-698b6703467c"), result.rooms[0].devices)
        assertEquals("c9a8269c-9939-429b-bb56-05f5abae2937", result.rooms[0].householdId)

        assertEquals(1, result.groups.size)
        assertEquals("d7eded8d-bdb4-4541-beba-7bbf88fea853", result.groups[0].id)
        assertEquals("Освещение", result.groups[0].name)
        assertEquals(mutableListOf<String>(), result.groups[0].aliases)
        assertEquals("devices.types.light", result.groups[0].type.type.code())
        assertEquals(listOf("d7e57431-7953-49aa-b46e-589495b71986"), result.groups[0].devices)
        assertEquals("f80b6641-8880-49d5-be31-1b35745c321a", result.groups[0].householdId)
        assertEquals(2, result.groups[0].capabilities.size)
        assertEquals(CapabilityType.COLOR_SETTING.codifiedEnum(), result.groups[0].capabilities[0].type.type)
        assertEquals(
            ColorSettingCapabilityStateObjectInstanceWrapper(
            ColorSettingCapabilityStateObjectInstance.TEMPERATURE_K.codifiedEnum()), result.groups[0].capabilities[0].state?.instance)
        assertEquals(6500, (result.groups[0].capabilities[0].state?.value as ColorSettingCapabilityStateObjectValueInteger).value)

        assertEquals(3, result.devices.size)
        assertEquals("4a7a2b29-3788-4e09-b5ef-387447185c96", result.devices[0].id)
        assertEquals("Яндекс Лайт", result.devices[0].name)
        assertEquals(DeviceTypeWrapper(DeviceType.YANDEX_SMART_SPEAKER.codifiedEnum()), result.devices[0].type)
        assertEquals("L00BN1200M42ZY.yandexmicro", result.devices[0].externalId)
        assertEquals("Q", result.devices[0].skillId)
        assertEquals("f80b6641-8880-49d5-be31-1b35745c321a", result.devices[0].householdId)
        assertEquals(null, result.devices[0].room)
        assertEquals(mutableListOf<String>(), result.devices[0].groups)
        assertEquals(mutableListOf<DeviceCapabilityObject>(), result.devices[0].capabilities)
        assertEquals(1, result.devices[0].properties.size)

        assertEquals("Лампочка", result.devices[2].name)
        assertEquals(6500, (result.devices[2].capabilities[0].state?.value as ColorSettingCapabilityStateObjectValueInteger).value)

        assertEquals(0, result.scenarios.size)

        assertEquals(2, result.households.size)
        assertEquals("c9a8269c-9939-429b-bb56-05f5abae2937", result.households[0].id)
        assertEquals("Мой дом", result.households[0].name)

        val yandexObjectResult = result.toYandexUserInfoResponse()
        val jsonDBResult = yandexObjectResult.toJson()

        val responseFromDB = json.decodeFromString<YandexUserInfoResponse>(jsonDBResult)
        val resultFromDB = responseFromDB.toSmartHomeInfo()

        assertEquals(result, resultFromDB)
    }

    @Test
    fun `test mapDeviceStateResponse`() {
        val response = json.decodeFromString<YandexDeviceStateResponse>(TestConstants.responseDeviceStateJson)
        val result = response.toDeviceObject()

        assertEquals("51e797a4-93cf-4bc4-832e-698b6703467c", result.id)
        assertEquals("Лампа", result.name)
        assertEquals(mutableListOf<String>(), result.aliases)
        assertEquals(DeviceTypeWrapper(DeviceType.LIGHT.codifiedEnum()), result.type)
        assertEquals("bf9159632e4fb1987bi7am", result.externalId)
        assertEquals("35e2897a-c583-495a-9e33-f5d6f0f4cb49", result.skillId)
        assertEquals("", result.householdId)
        assertEquals("ca82a680-0317-4bec-b92e-5c3dd27c61eb", result.room)
        assertEquals(mutableListOf<String>(), result.groups)

        assertEquals(3, result.capabilities.size)
        assertEquals(CapabilityType.COLOR_SETTING.codifiedEnum(), result.capabilities[0].type.type)
        assertEquals(true, result.capabilities[0].retrievable)
        assertEquals(null, result.capabilities[0].state)
        assertEquals(0.0f, result.capabilities[0].lastUpdated)

        val colorSettingParameters = result.capabilities[0].parameters as ColorSettingCapabilityParameterObject
        assertEquals(ColorModel.HSV.codifiedEnum(), colorSettingParameters.colorModel?.colorModel)
        assertEquals(2700, colorSettingParameters.temperatureK?.min)
        assertEquals(6500, colorSettingParameters.temperatureK?.max)
        assertEquals(null, colorSettingParameters.colorScene)

        assertEquals(mutableListOf<DevicePropertyObject>(), result.properties)
    }

    @Test
    fun `test mapManageDeviceCapabilitiesStateRequest`() {
        val deviceObjects = listOf(
            DeviceObject(
                id = "lamp-id-1",
                name = "",
                aliases = mutableListOf(),
                type = DeviceTypeWrapper(DeviceType.SOCKET.codifiedEnum()),
                externalId = "",
                skillId = "",
                householdId = "",
                room = "",
                groups = mutableListOf(),
                capabilities = mutableListOf(
                    DeviceCapabilityObject(
                        type = CapabilityTypeWrapper(CapabilityType.ON_OFF.codifiedEnum()),
                        reportable = true,
                        retrievable = true,
                        parameters = OnOffCapabilityParameterObject(split = false),
                        state = OnOffCapabilityStateObjectData(
                            instance = OnOffCapabilityStateObjectInstanceWrapper(
                                OnOffCapabilityStateObjectInstance.ON.codifiedEnum()),
                            value = OnOffCapabilityStateObjectValue(true)
                        ),
                        lastUpdated = 0f
                    ),
                    DeviceCapabilityObject(
                        type = CapabilityTypeWrapper(CapabilityType.RANGE.codifiedEnum()),
                        reportable = true,
                        retrievable = true,
                        parameters = RangeCapabilityParameterObject(
                            instance = RangeCapabilityWrapper(RangeCapability.BRIGHTNESS.codifiedEnum()),
                            randomAccess = true
                        ),
                        state = RangeCapabilityStateObjectData(
                            instance = RangeCapabilityWrapper(RangeCapability.BRIGHTNESS.codifiedEnum()),
                            value = RangeCapabilityStateObjectDataValue(50.0f)
                        ),
                        lastUpdated = 0f
                    ),
                    DeviceCapabilityObject(
                        type = CapabilityTypeWrapper(CapabilityType.COLOR_SETTING.codifiedEnum()),
                        reportable = true,
                        retrievable = true,
                        parameters = ColorSettingCapabilityParameterObject(
                            temperatureK = TemperatureK(4000, 4000)
                        ),
                        state = ColorSettingCapabilityStateObjectData(
                            instance = ColorSettingCapabilityStateObjectInstanceWrapper(
                                ColorSettingCapabilityStateObjectInstance.TEMPERATURE_K.codifiedEnum()),
                            value = ColorSettingCapabilityStateObjectValueInteger(4000)
                        ),
                        lastUpdated = 0f
                    )
                ),
                properties = mutableListOf()
            )
        )

        val requestObject = deviceObjects.toYandexManageDeviceCapabilitiesStateRequest()
        val serializedRequest = json.encodeToString(requestObject)

        assertEquals(TestConstants.requestManageDeviceCapabilitiesJson, serializedRequest)
    }

    @Test
    fun `test mapManageGroupCapabilitiesStateRequest`() {
        val groupCapabilityObjects = listOf(
            GroupCapabilityObject(
                type = CapabilityTypeWrapper(CapabilityType.ON_OFF.codifiedEnum()),
                retrievable = true,
                parameters = OnOffCapabilityParameterObject(split = false),
                state = OnOffCapabilityStateObjectData(
                    instance = OnOffCapabilityStateObjectInstanceWrapper(OnOffCapabilityStateObjectInstance.ON.codifiedEnum()),
                    value = OnOffCapabilityStateObjectValue(true)
                )
            )
        )

        val requestObject = groupCapabilityObjects.toYandexManageGroupCapabilitiesStateRequest()
        val serializedRequest = json.encodeToString(requestObject)

        assertEquals(TestConstants.requestManageGroupCapabilitiesJson, serializedRequest)
    }
}