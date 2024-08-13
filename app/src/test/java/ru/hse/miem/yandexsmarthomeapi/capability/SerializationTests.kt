package ru.hse.miem.yandexsmarthomeapi.capability

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexDeviceStateResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageDeviceCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageGroupCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexUserInfoResponse
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.*
import ru.hse.miem.yandexsmarthomeapi.entity.common.*
import ru.hse.miem.yandexsmarthomeapi.entity.common.extensions.toJson
import ru.hse.miem.yandexsmarthomeapi.entity.common.extensions.*
import ru.hse.miem.yandexsmarthomeapi.entity.common.extensions.toYandexManageDeviceCapabilitiesStateResponse
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.MeasurementUnit

class SerializationTests {
    private val json = Json { ignoreUnknownKeys = false }

    @Test
    fun testOnOffCapabilityDeserialization() {
        val jsonString = """
        {
          "id": "socket-001-xda",
          "name": "розетка",
          "description": "умная розетка xda",
          "room": "спальня",
          "type": "devices.types.socket",
          "custom_data": {
            "api_location": "rus"
          },
          "capabilities": [
            {
              "type": "devices.capabilities.on_off",
              "retrievable": false,
              "reportable": false,
              "parameters": {
                "split": false
              }
            }
          ],
          "device_info": {
            "manufacturer": "Provider-01",
            "model": "xda 1",
            "hw_version": "1.2",
            "sw_version": "5.4"
          }
        }
        """

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val deviceObject = jsonObject.toDeviceObject()

        assertEquals("socket-001-xda", deviceObject.id)
        assertEquals("розетка", deviceObject.name)
        assertEquals(DeviceType.SOCKET, deviceObject.type.type.knownOrNull())
        assertEquals(1, deviceObject.capabilities.size)

        val capability = deviceObject.capabilities[0]
        assertEquals(CapabilityType.ON_OFF, capability.type.type.knownOrNull())
        assertFalse(capability.retrievable)
        assertFalse(capability.reportable)
        assertTrue(capability.parameters is OnOffCapabilityParameterObject)
        assertFalse((capability.parameters as OnOffCapabilityParameterObject).split)

        val serializedJson = deviceObject.toJson()
        val deserializedDevice = serializedJson.toDeviceObject()
        assertEquals(deviceObject, deserializedDevice)
    }

    @Test
    fun testColorSettingCapabilityDeserialization() {
        val jsonString = """
        {
          "id": "lamp-001-xdl",
          "name": "лампочка",
          "description": "умная лампочка xdl",
          "room": "спальня",
          "type": "devices.types.light",
          "custom_data": {
            "api_location": "rus"
          },
          "capabilities": [
            {
              "type": "devices.capabilities.color_setting",
              "retrievable": true,
              "reportable": false,
              "parameters": {
                "color_model": "hsv",
                "temperature_k": {
                  "max": 6500,
                  "min": 2700
                },
                "color_scene": {
                  "scenes": [
                    {"id": "party"},
                    {"id": "alarm"},
                    {"id": "fantasy"},
                    {"id": "reading"}
                  ]
                }
              }
            }
          ],
          "device_info": {
            "manufacturer": "Provider-01",
            "model": "xdl 1",
            "hw_version": "3.2",
            "sw_version": "2.4"
          }
        }
        """

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val deviceObject = jsonObject.toDeviceObject()

        assertEquals("lamp-001-xdl", deviceObject.id)
        assertEquals("лампочка", deviceObject.name)
        assertEquals(DeviceType.LIGHT, deviceObject.type.type.knownOrNull())
        assertEquals(1, deviceObject.capabilities.size)

        val capability = deviceObject.capabilities[0]
        assertEquals(CapabilityType.COLOR_SETTING, capability.type.type.knownOrNull())
        assertTrue(capability.retrievable)
        assertFalse(capability.reportable)
        assertTrue(capability.parameters is ColorSettingCapabilityParameterObject)

        val colorParams = capability.parameters as ColorSettingCapabilityParameterObject
        assertEquals(ColorModel.HSV, colorParams.colorModel?.colorModel?.knownOrNull())
        assertEquals(6500, colorParams.temperatureK?.max)
        assertEquals(2700, colorParams.temperatureK?.min)
        assertEquals(4, colorParams.colorScene?.scenes?.size)

        val serializedJson = deviceObject.toJson()
        val deserializedDevice = serializedJson.toDeviceObject()
        assertEquals(deviceObject, deserializedDevice)
    }

    @Test
    fun testVideoStreamCapabilityDeserialization() {
        val jsonString = """
        {
          "id": "cam-hd-01x",
          "name": "моя камера",
          "description": "умная камера",
          "room": "спальня",
          "type": "devices.types.camera",
          "capabilities": [
            {
              "type": "devices.capabilities.video_stream",
              "retrievable": false,
              "reportable": false,
              "parameters": {
                "protocols": ["hls"]
              }
            }
          ],
          "device_info": {
            "manufacturer": "Provider-01",
            "model": "hd-01x",
            "hw_version": "1.2",
            "sw_version": "5.4"
          }
        }
        """

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val deviceObject = jsonObject.toDeviceObject()

        assertEquals("cam-hd-01x", deviceObject.id)
        assertEquals("моя камера", deviceObject.name)
        assertEquals(DeviceType.CAMERA, deviceObject.type.type.knownOrNull())
        assertEquals(1, deviceObject.capabilities.size)

        val capability = deviceObject.capabilities[0]
        assertEquals(CapabilityType.VIDEO_STREAM, capability.type.type.knownOrNull())
        assertFalse(capability.retrievable)
        assertFalse(capability.reportable)
        assertTrue(capability.parameters is VideoStreamCapabilityParameterObject)

        val videoParams = capability.parameters as VideoStreamCapabilityParameterObject
        assertEquals(1, videoParams.protocols.size)
        assertEquals(VideoStreamProtocol.HLS, videoParams.protocols[0].protocol.knownOrNull())

        val serializedJson = deviceObject.toJson()
        val deserializedDevice = serializedJson.toDeviceObject()
        assertEquals(deviceObject, deserializedDevice)
    }

    @Test
    fun testModeCapabilityDeserialization() {
        val jsonString = """
        {
          "id": "ac-001-xdc",
          "name": "кондиционер",
          "description": "умный кондиционер xdc",
          "room": "спальня",
          "type": "devices.types.thermostat.ac",
          "custom_data": {
            "api_location": "rus"
          },
          "capabilities": [
            {
              "type": "devices.capabilities.mode",
              "retrievable": true,
              "reportable": false,
              "parameters": {
                "instance": "thermostat",
                "modes": [
                  {"value": "fan_only"},
                  {"value": "heat"},
                  {"value": "cool"},
                  {"value": "dry"},
                  {"value": "auto"}
                ]
              }
            }
          ],
          "device_info": {
            "manufacturer": "Provider-01",
            "model": "xdc 1",
            "hw_version": "1.2",
            "sw_version": "5.4"
          }
        }
        """

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val deviceObject = jsonObject.toDeviceObject()

        assertEquals("ac-001-xdc", deviceObject.id)
        assertEquals("кондиционер", deviceObject.name)
        assertEquals(DeviceType.THERMOSTAT_AC, deviceObject.type.type.knownOrNull())
        assertEquals(1, deviceObject.capabilities.size)

        val capability = deviceObject.capabilities[0]
        assertEquals(CapabilityType.MODE, capability.type.type.knownOrNull())
        assertTrue(capability.retrievable)
        assertFalse(capability.reportable)
        assertTrue(capability.parameters is ModeCapabilityParameterObject)

        val modeParams = capability.parameters as ModeCapabilityParameterObject
        assertEquals(ModeCapability.THERMOSTAT, modeParams.instance.mode.knownOrNull())
        assertEquals(5, modeParams.modes.size)

        val serializedJson = deviceObject.toJson()
        val deserializedDevice = serializedJson.toDeviceObject()
        assertEquals(deviceObject, deserializedDevice)
    }

    @Test
    fun testRangeCapabilityDeserialization() {
        val jsonString = """
        {
          "id": "lamp-001-xdl",
          "name": "лампочка",
          "description": "умная лампочка xdl",
          "room": "спальня",
          "type": "devices.types.light",
          "custom_data": {
            "api_location": "rus"
          },
          "capabilities": [
            {
              "type": "devices.capabilities.range",
              "retrievable": true,
              "reportable": false,
              "parameters": {
                "instance": "brightness",
                "random_access": true,
                "range": {
                  "max": 100,
                  "min": 0,
                  "precision": 10
                },
                "unit": "unit.percent"
              }
            }
          ],
          "device_info": {
            "manufacturer": "Provider-01",
            "model": "xdl 1",
            "hw_version": "3.2",
            "sw_version": "2.4"
          }
        }
        """

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val deviceObject = jsonObject.toDeviceObject()

        assertEquals("lamp-001-xdl", deviceObject.id)
        assertEquals("лампочка", deviceObject.name)
        assertEquals(DeviceType.LIGHT, deviceObject.type.type.knownOrNull())
        assertEquals(1, deviceObject.capabilities.size)

        val capability = deviceObject.capabilities[0]
        assertEquals(CapabilityType.RANGE, capability.type.type.knownOrNull())
        assertTrue(capability.retrievable)
        assertFalse(capability.reportable)
        assertTrue(capability.parameters is RangeCapabilityParameterObject)

        val rangeParams = capability.parameters as RangeCapabilityParameterObject
        assertEquals(RangeCapability.BRIGHTNESS, rangeParams.instance.range.knownOrNull())
        assertTrue(rangeParams.randomAccess)
        assertEquals(100f, rangeParams.range?.max)
        assertEquals(0f, rangeParams.range?.min)
        assertEquals(10f, rangeParams.range?.precision)
        assertEquals(MeasurementUnit.PERCENT, rangeParams.unit?.unit?.knownOrNull())

        val serializedJson = deviceObject.toJson()
        val deserializedDevice = serializedJson.toDeviceObject()
        assertEquals(deviceObject, deserializedDevice)
    }

    @Test
    fun testToggleCapabilityDeserialization() {
        val jsonString = """
        {
          "id": "humidifier-001-xdh",
          "name": "увлажнитель",
          "description": "умный увлажнитель xdh",
          "room": "спальня",
          "type": "devices.types.humidifier",
          "custom_data": {
            "api_location": "rus"
          },
          "capabilities": [
            {
              "type": "devices.capabilities.toggle",
              "retrievable": true,
              "reportable": false,
              "parameters": {
                "instance": "ionization"
              }
            }
          ],
          "device_info": {
            "manufacturer": "Provider-01",
            "model": "xdh 1",
            "hw_version": "1.2",
            "sw_version": "5.4"
          }
        }
        """

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val deviceObject = jsonObject.toDeviceObject()

        assertEquals("humidifier-001-xdh", deviceObject.id)
        assertEquals("увлажнитель", deviceObject.name)
        assertEquals(DeviceType.HUMIDIFIER, deviceObject.type.type.knownOrNull())
        assertEquals(1, deviceObject.capabilities.size)

        val capability = deviceObject.capabilities[0]
        assertEquals(CapabilityType.TOGGLE, capability.type.type.knownOrNull())
        assertTrue(capability.retrievable)
        assertFalse(capability.reportable)
        assertTrue(capability.parameters is ToggleCapabilityParameterObject)

        val toggleParams = capability.parameters as ToggleCapabilityParameterObject
        assertEquals(ToggleCapability.IONIZATION, toggleParams.instance.toggle.knownOrNull())

        val serializedJson = deviceObject.toJson()
        val deserializedDevice = serializedJson.toDeviceObject()
        assertEquals(deviceObject, deserializedDevice)
    }

    @Test
    fun testDeviceActionRequest() {
        val jsonString = """
        {
          "payload": {
            "devices": [
              {
                "id": "socket-001-xda",
                "custom_data": {
                  "api_location": "rus"
                },
                "capabilities": [
                  {
                    "type": "devices.capabilities.on_off",
                    "state": {
                      "instance": "on",
                      "value": false
                    }
                  }
                ]
              }
            ]
          }
        }
        """

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val request = jsonObject["payload"]?.jsonObject?.toYandexManageDeviceCapabilitiesStateRequest()

        assertNotNull(request)
        assertEquals(1, request?.devices?.size)

        val device = request?.devices?.get(0)?.toDeviceActionsObject()
        assertEquals("socket-001-xda", device?.id)
        assertEquals(1, device?.actions?.size)

        val action = device?.actions?.get(0)
        assertEquals(CapabilityType.ON_OFF, action?.type?.type?.knownOrNull())
        assertTrue(action?.state is OnOffCapabilityStateObjectData)
        assertEquals(OnOffCapabilityStateObjectInstance.ON, (action?.state as OnOffCapabilityStateObjectData).instance.onOff.knownOrNull())
        assertFalse((action.state as OnOffCapabilityStateObjectData).value.value)

        val serializedJson = request.toJson()
        val deserializedRequest = serializedJson.toYandexManageDeviceCapabilitiesStateRequest()
        assertEquals(request, deserializedRequest)
    }

    @Test
    fun testDeviceQueryResponse() {
        val jsonString = """
        {
            "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
            "payload": {
                "devices": [
                    {
                        "id": "lamp-001-xdl",
                        "capabilities": [
                            {
                                "type": "devices.capabilities.color_setting",
                                "state": {
                                    "instance": "hsv",
                                    "value": {
                                        "h": 255,
                                        "s": 100,
                                        "v": 50
                                    }
                                }
                            }
                        ]
                    }
                ]
            }
        }
        """
        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val response = jsonObject.toYandexDeviceStateResponse()

        assertEquals("ff36a3cc-ec34-11e6-b1a0-64510650abcf", response.requestId)
        assertEquals("lamp-001-xdl", response.id)
        assertEquals("Лампочка", response.name)
        assertEquals("devices.types.light", response.type.content)
        assertEquals(1, response.capabilities.size)

        val capability = response.capabilities[0].toCapabilityObject()
        assertEquals(CapabilityType.COLOR_SETTING, capability.type.type.knownOrNull())

        val state = capability.state
        assertNotNull(state)
        assertTrue(state is ColorSettingCapabilityStateObjectData)

        val colorState = state as ColorSettingCapabilityStateObjectData
        assertEquals(ColorSettingCapabilityStateObjectInstance.HSV, colorState.instance.colorSetting.knownOrNull())
        assertTrue(colorState.value is ColorSettingCapabilityStateObjectValueObjectHSV)

        val hsvValue = (colorState.value as ColorSettingCapabilityStateObjectValueObjectHSV).value
        assertEquals(255, hsvValue.h)
        assertEquals(100, hsvValue.s)
        assertEquals(50, hsvValue.v)

        val serializedJson = response.toJson()
        val deserializedResponse = serializedJson.toYandexDeviceStateResponse()
        assertEquals(response, deserializedResponse)
    }

    @Test
    fun testDeviceActionResponse() {
        val jsonString = """
        {
            "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
            "payload": {
                "devices": [
                    {
                        "id": "socket-001-xda",
                        "capabilities": [
                            {
                                "type": "devices.capabilities.on_off",
                                "state": {
                                    "instance": "on",
                                    "action_result": {
                                        "status": "DONE"
                                    }
                                }
                            }
                        ]
                    }
                ]
            }
        }
        """

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val response = jsonObject.toYandexManageDeviceCapabilitiesStateResponse()

        assertEquals("ff36a3cc-ec34-11e6-b1a0-64510650abcf", response.requestId)
        assertEquals(1, response.devices.size)

        val device = response.devices[0].toDeviceActionsResultObject()
        assertEquals("socket-001-xda", device.id)
        assertEquals(1, device.capabilities.size)

        val capability = device.capabilities[0]
        assertEquals(CapabilityType.ON_OFF, capability.type.type.knownOrNull())
        assertTrue(capability.state is StateResultObject)

        val stateResult = capability.state as StateResultObject
        assertEquals(OnOffCapabilityStateObjectInstance.ON, (stateResult.instance as OnOffCapabilityStateObjectInstanceWrapper).onOff.knownOrNull())
        assertEquals(Status.DONE, stateResult.actionResult.status.status.knownOrNull())

        val serializedJson = response.toJson()
        val deserializedResponse = serializedJson.toYandexManageDeviceCapabilitiesStateResponse()
        assertEquals(response, deserializedResponse)
    }

    @Test
    fun testVideoStreamActionResponse() {
        val jsonString = """
        {
          "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
          "payload": {
            "devices": [
              {
                "id": "cam-hd-01x",
                "capabilities": [
                  {
                    "type": "devices.capabilities.video_stream",
                    "state": {
                      "instance": "get_stream",
                      "value": {
                        "stream_url": "https://host/path/to/playlist.m3u8?token=123456789abcdef",
                        "protocol": "hls"
                      },
                      "action_result": {
                        "status": "DONE"
                      }
                    }
                  }
                ]
              }
            ]
          }
        }
        """

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val response = jsonObject.toYandexManageDeviceCapabilitiesStateResponse()

        assertEquals("ff36a3cc-ec34-11e6-b1a0-64510650abcf", response.requestId)
        assertEquals(1, response.devices.size)

        val device = response.devices[0].toDeviceActionsResultObject()
        assertEquals("cam-hd-01x", device.id)
        assertEquals(1, device.capabilities.size)

        val capability = device.capabilities[0]
        assertEquals(CapabilityType.VIDEO_STREAM, capability.type.type.knownOrNull())
        assertTrue(capability.state is VideoStreamCapabilityStateObjectActionResult)

        val stateResult = capability.state as VideoStreamCapabilityStateObjectActionResult
        assertEquals(VideoStreamCapabilityStateObjectInstance.GET_STREAM, stateResult.instance.videoStream.knownOrNull())
        assertEquals("https://host/path/to/playlist.m3u8?token=123456789abcdef", stateResult.value.streamUrl)
        assertEquals(VideoStreamProtocol.HLS, stateResult.value.protocol.protocol.knownOrNull())
        assertEquals(Status.DONE, stateResult.actionResult.status.status.knownOrNull())

        val serializedJson = response.toJson()
        val deserializedResponse = serializedJson.toYandexManageDeviceCapabilitiesStateResponse()
        assertEquals(response, deserializedResponse)
    }
}