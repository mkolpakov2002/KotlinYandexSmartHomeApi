package ru.hse.miem.yandexsmarthomeapi.ui.device_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.domain.YandexSmartHomeClient
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexApiResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageDeviceCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceActionsObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ActionResult
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueObjectHSV
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueRGB
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.DeviceCapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.HSVObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityModeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectDataValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.StateResultObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.Status
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.StatusWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityStateObjectDataValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamCapabilityStateObjectRequestValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamCapabilityStateObjectResponseValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamProtocol
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamProtocolWrapper
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.hse.miem.yandexsmarthomeapi.entity.api.extensions.toDeviceActionsObject
import ru.hse.miem.yandexsmarthomeapi.entity.api.extensions.toSmartHomeInfo

open class DeviceListViewModel(private val client: YandexSmartHomeClient) : ViewModel() {

    private val _devices = MutableStateFlow<List<DeviceObject>>(emptyList())
    open val devices: StateFlow<List<DeviceObject>> = _devices.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    init {
        refreshDevices()
    }

    fun refreshDevices() {
        viewModelScope.launch {
            try {
                val response = client.getUserInfo()
                when (response) {
                    is YandexApiResponse.SuccessUserInfo -> {
                        val smartHomeInfo = response.data.toSmartHomeInfo()
                        _devices.value = smartHomeInfo.devices
                    }
                    is YandexApiResponse.Error -> {
                        _error.emit("Failed to fetch devices: ${response.error.error}")
                    }
                    else -> {
                        _error.emit("Unexpected response type")
                    }
                }
            } catch (e: Exception) {
                _error.emit("Error fetching devices: ${e.message}")
            }
        }
    }

    fun updateDeviceCapability(deviceId: String, capabilityType: String, newValue: Any) {
        viewModelScope.launch {
            try {
                val device = _devices.value.find { it.id == deviceId } ?: throw IllegalArgumentException("Device not found")
                val capability = device.capabilities.find { it.type.type.code() == capabilityType } ?: throw IllegalArgumentException("Capability not found")

                val updatedCapability = updateCapabilityState(capability, capabilityType, newValue)
                val updatedDevice = device.copy(capabilities = device.capabilities.map {
                    if (it.type.type.code() == capabilityType) updatedCapability else it
                }.toMutableList())

                _devices.value = _devices.value.map { if (it.id == deviceId) updatedDevice else it }

                val request = YandexManageDeviceCapabilitiesStateRequest(listOf(updatedDevice.toDeviceActionsObject()))
                when (val response = client.manageDeviceCapabilitiesState(request)) {
                    is YandexApiResponse.SuccessManageDeviceCapabilitiesState -> {
                        // Обработка успешного ответа, если необходимо
                    }
                    is YandexApiResponse.Error -> {
                        _error.emit("Failed to update device: ${response.error.error}")
                    }
                    else -> {
                        _error.emit("Unexpected response type")
                    }
                }
            } catch (e: Exception) {
                _error.emit("Error updating device: ${e.message}")
            }
        }
    }

    private fun updateCapabilityState(capability: DeviceCapabilityObject, capabilityType: String, newValue: Any): DeviceCapabilityObject {
        val newState = when (capabilityType) {
            CapabilityType.ON_OFF.code -> OnOffCapabilityStateObjectData(
                instance = OnOffCapabilityStateObjectInstanceWrapper(OnOffCapabilityStateObjectInstance.ON.codifiedEnum()),
                value = OnOffCapabilityStateObjectValue(newValue as Boolean)
            )
            CapabilityType.COLOR_SETTING.code -> createColorSettingState(newValue)
            CapabilityType.MODE.code -> ModeCapabilityStateObjectData(
                instance = (capability.parameters as? ModeCapabilityParameterObject)?.instance
                    ?: throw IllegalArgumentException("Invalid mode capability parameters"),
                value = ModeCapabilityModeWrapper((newValue as String).codifiedEnum())
            )
            CapabilityType.RANGE.code -> RangeCapabilityStateObjectData(
                instance = (capability.parameters as? RangeCapabilityParameterObject)?.instance
                    ?: throw IllegalArgumentException("Invalid range capability parameters"),
                value = RangeCapabilityStateObjectDataValue(newValue as Float)
            )
            CapabilityType.TOGGLE.code -> ToggleCapabilityStateObjectData(
                instance = (capability.parameters as? ToggleCapabilityParameterObject)?.instance
                    ?: throw IllegalArgumentException("Invalid toggle capability parameters"),
                value = ToggleCapabilityStateObjectDataValue(newValue as Boolean)
            )
            else -> throw IllegalArgumentException("Unsupported capability type: $capabilityType")
        }
        return capability.copy(state = newState)
    }

    private fun createColorSettingState(newValue: Any): ColorSettingCapabilityStateObjectData {
        return when (newValue) {
            is Int -> ColorSettingCapabilityStateObjectData(
                instance = ColorSettingCapabilityStateObjectInstanceWrapper(ColorSettingCapabilityStateObjectInstance.RGB.codifiedEnum()),
                value = ColorSettingCapabilityStateObjectValueRGB(newValue)
            )
            is HSVObject -> ColorSettingCapabilityStateObjectData(
                instance = ColorSettingCapabilityStateObjectInstanceWrapper(ColorSettingCapabilityStateObjectInstance.HSV.codifiedEnum()),
                value = ColorSettingCapabilityStateObjectValueObjectHSV(newValue)
            )
            else -> throw IllegalArgumentException("Invalid color setting value type")
        }
    }

    fun getDeviceById(deviceId: String): StateFlow<DeviceObject?> {
        return devices.map { deviceList -> deviceList.find { it.id == deviceId } }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }

    fun getVideoStream(deviceId: String) {
        viewModelScope.launch {
            try {
                val device = _devices.value.find { it.id == deviceId } ?: throw IllegalArgumentException("Device not found")
                val capability = device.capabilities.find { it.type.type.code() == CapabilityType.VIDEO_STREAM.code }
                    ?: throw IllegalArgumentException("Video stream capability not found")

                val request = createVideoStreamRequest(deviceId)
                when (val response = client.manageDeviceCapabilitiesState(request)) {
                    is YandexApiResponse.SuccessManageDeviceCapabilitiesState -> {
                        handleVideoStreamResponse(response, device, capability)
                    }
                    is YandexApiResponse.Error -> {
                        _error.emit("Failed to get video stream: ${response.error.error}")
                    }
                    else -> {
                        _error.emit("Unexpected response type")
                    }
                }
            } catch (e: Exception) {
                _error.emit("Error getting video stream: ${e.message}")
            }
        }
    }

    private fun createVideoStreamRequest(deviceId: String): YandexManageDeviceCapabilitiesStateRequest {
        return YandexManageDeviceCapabilitiesStateRequest(
            listOf(
                DeviceActionsObject(
                    deviceId,
                    mutableListOf(
                        CapabilityObject(
                            type = CapabilityTypeWrapper(CapabilityType.VIDEO_STREAM.codifiedEnum()),
                            state = VideoStreamCapabilityStateObjectData(
                                instance = VideoStreamCapabilityStateObjectInstanceWrapper(
                                    VideoStreamCapabilityStateObjectInstance.GET_STREAM.codifiedEnum()
                                ),
                                value = VideoStreamCapabilityStateObjectRequestValue(
                                    protocols = listOf(VideoStreamProtocolWrapper(VideoStreamProtocol.HLS.codifiedEnum()))
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    private fun handleVideoStreamResponse(
        response: YandexApiResponse.SuccessManageDeviceCapabilitiesState,
        device: DeviceObject,
        capability: DeviceCapabilityObject
    ) {
        val streamData = extractVideoStreamData(response)
        if (streamData != null) {
            updateDeviceWithStreamData(device, capability, streamData)
        } else {
            viewModelScope.launch {
                _error.emit("Failed to get stream URL")
            }
        }
    }

    private fun extractVideoStreamData(response: YandexApiResponse.SuccessManageDeviceCapabilitiesState): VideoStreamCapabilityStateObjectResponseValue? {
        val streamResult = response.data.devices.firstOrNull()
        return ((streamResult?.capabilities?.firstOrNull()?.state as? VideoStreamCapabilityStateObjectData)?.value as? VideoStreamCapabilityStateObjectResponseValue)
    }

    private fun updateDeviceWithStreamData(
        device: DeviceObject,
        capability: DeviceCapabilityObject,
        streamData: VideoStreamCapabilityStateObjectResponseValue
    ) {
        val updatedCapability = capability.copy(
            state = VideoStreamCapabilityStateObjectData(
                instance = VideoStreamCapabilityStateObjectInstanceWrapper(
                    VideoStreamCapabilityStateObjectInstance.GET_STREAM.codifiedEnum()
                ),
                value = streamData
            )
        )
        val updatedDevice = device.copy(capabilities = device.capabilities.map {
            if (it.type.type.code() == CapabilityType.VIDEO_STREAM.code) updatedCapability else it
        }.toMutableList())
        _devices.value = _devices.value.map { if (it.id == device.id) updatedDevice else it }
    }
}

