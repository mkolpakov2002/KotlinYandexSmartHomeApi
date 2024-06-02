package ru.hse.miem.yandexsmarthomeapi.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.hse.miem.yandexsmarthomeapi.domain.DeviceRepository
import ru.hse.miem.yandexsmarthomeapi.domain.TokenRepository
import ru.hse.miem.yandexsmarthomeapi.domain.YandexSmartHomeClient
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexApiResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageDeviceCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceActionsObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.toDeviceObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.toJson
import ru.hse.miem.yandexsmarthomeapi.ui.models.DeviceCapabilityUIModel
import ru.hse.miem.yandexsmarthomeapi.ui.models.DevicePropertyUIModel
import ru.hse.miem.yandexsmarthomeapi.ui.models.DeviceUIModel
import ru.hse.miem.yandexsmarthomeapi.ui.views.getIconResId

class MainActivityViewModel : ViewModel() {

    private val _deviceList = MutableStateFlow<List<DeviceUIModel>>(emptyList())
    val deviceList: StateFlow<List<DeviceUIModel>> get() = _deviceList

    private val _stateUpdates = MutableStateFlow<Triple<String, String, CapabilityStateObjectData>?>(null)
    val stateUpdates: StateFlow<Triple<String, String, CapabilityStateObjectData>?> get() = _stateUpdates

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val tokenRepository = TokenRepository.getInstance()
    private val deviceRepository = DeviceRepository()

    private val client: YandexSmartHomeClient by lazy {
        val token = tokenRepository.getToken()
        val url = tokenRepository.getUrl()

        if (token != null && url != null) {
            YandexSmartHomeClient.getInstance(url, token)
        } else {
            throw IllegalStateException("Token or URL not found")
        }
    }

    init {
        viewModelScope.launch {
            fetchDeviceList()
            subscribeToStateFlows()
        }
    }

    private fun subscribeToStateFlows() {
        viewModelScope.launch {
            _deviceList.collect { devices ->
                devices.forEach { device ->
                    device.capabilities.forEach { capability ->
                        launch {
                            capability.stateFlow.collect { newState ->
                                if (newState != null) {
                                    handleStateUpdate(device.id, capability.id, newState)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleStateUpdate(deviceId: String, capabilityId: String, newState: CapabilityStateObjectData) {
        if (_stateUpdates.value != Triple(deviceId, capabilityId, newState)) {
            _stateUpdates.value = Triple(deviceId, capabilityId, newState)
        }
    }

    fun setDeviceList(devices: List<DeviceUIModel>) {
        _deviceList.value = devices
    }

    fun updatePropertyPosition(deviceId: String, fromPosition: Int, toPosition: Int) {
        Log.d("MainActivityViewModel", "Updating property position for deviceId=$deviceId, fromPosition=$fromPosition, toPosition=$toPosition")
        _deviceList.update { devices ->
            devices.map { device ->
                if (device.id == deviceId) {
                    val updatedProperties = device.properties.toMutableList().apply {
                        if (fromPosition in indices && toPosition in indices) {
                            val movedItem = removeAt(fromPosition)
                            add(toPosition, movedItem)
                            for (i in indices) {
                                this[i] = this[i].copy(position = i)
                            }
                        }
                    }
                    device.copy(properties = updatedProperties)
                } else {
                    device
                }
            }
        }
    }

    fun updateCapabilityPosition(deviceId: String, fromPosition: Int, toPosition: Int) {
        Log.d("MainActivityViewModel", "Updating capability position for deviceId=$deviceId, fromPosition=$fromPosition, toPosition=$toPosition")
        _deviceList.update { devices ->
            devices.map { device ->
                if (device.id == deviceId) {
                    val updatedCapabilities = device.capabilities.toMutableList().apply {
                        if (fromPosition in indices && toPosition in indices) {
                            val movedItem = removeAt(fromPosition)
                            add(toPosition, movedItem)
                            for (i in indices) {
                                this[i] = this[i].copy(position = i)
                            }
                        }
                    }
                    device.copy(capabilities = updatedCapabilities)
                } else {
                    device
                }
            }
        }
    }

    fun fetchDeviceList() {
        viewModelScope.launch {
            try {
                val devices = deviceRepository.getDevices()
                _deviceList.value = devices
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при получении списка устройств"
                Log.e("MainActivityViewModel", "Error fetching device list", e)
            }
        }
    }

    fun fetchDeviceState(deviceId: String): Job {
        Log.d("MainActivityViewModel", "Fetching device state for deviceId=$deviceId")
        return viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: YandexApiResponse = client.getDeviceState(deviceId)
                if (response is YandexApiResponse.SuccessDeviceState) {
                    val deviceState = response.data.toDeviceObject()

                    _deviceList.update { devices ->
                        val updatedDevices = devices.toMutableList()
                        val index = devices.indexOfFirst { it.id == deviceId }
                        if (index != -1) {
                            val oldDevice = devices[index]
                            val updatedCapabilities = deviceState.capabilities.map { capability ->
                                oldDevice.capabilities.find { it.type == capability.type }?.copy(
                                    parameters = capability.parameters,
                                    retrievable = capability.retrievable,
                                    reportable = capability.reportable,
                                    stateFlow = MutableStateFlow(capability.state)
                                ) ?: DeviceCapabilityUIModel(
                                    id = capability.type.type.code(),
                                    name = capability.type.type.code(),
                                    type = capability.type,
                                    parameters = capability.parameters,
                                    retrievable = capability.retrievable,
                                    reportable = capability.reportable,
                                    stateFlow = MutableStateFlow(capability.state),
                                    position = oldDevice.capabilities.size
                                )
                            }.toMutableList()

                            val updatedProperties = deviceState.properties.map { property ->
                                oldDevice.properties.find { it.type == property.type }?.copy(
                                    parameters = property.parameters,
                                    retrievable = property.retrievable,
                                    reportable = property.reportable,
                                    stateFlow = MutableStateFlow(property.state)
                                ) ?: DevicePropertyUIModel(
                                    id = property.type.type.code(),
                                    name = property.type.type.code(),
                                    type = property.type,
                                    parameters = property.parameters,
                                    retrievable = property.retrievable,
                                    reportable = property.reportable,
                                    stateFlow = MutableStateFlow(property.state),
                                    position = oldDevice.properties.size + deviceState.capabilities.size
                                )
                            }.toMutableList()

                            updatedDevices[index] = oldDevice.copy(
                                capabilities = updatedCapabilities,
                                properties = updatedProperties
                            )
                        } else {
                            updatedDevices.add(
                                DeviceUIModel(
                                    id = deviceState.id,
                                    name = deviceState.name,
                                    type = deviceState.type,
                                    capabilities = deviceState.capabilities.mapIndexed { idx, capability ->
                                        DeviceCapabilityUIModel(
                                            id = capability.type.type.code(),
                                            name = capability.type.type.code(),
                                            type = capability.type,
                                            parameters = capability.parameters,
                                            retrievable = capability.retrievable,
                                            reportable = capability.reportable,
                                            stateFlow = MutableStateFlow(capability.state),
                                            position = idx
                                        )
                                    }.toMutableList(),
                                    properties = deviceState.properties.mapIndexed { idx, property ->
                                        DevicePropertyUIModel(
                                            id = property.type.type.code(),
                                            name = property.type.type.code(),
                                            type = property.type,
                                            parameters = property.parameters,
                                            retrievable = property.retrievable,
                                            reportable = property.reportable,
                                            stateFlow = MutableStateFlow(property.state),
                                            position = idx + deviceState.capabilities.size
                                        )
                                    }.toMutableList(),
                                    iconResId = deviceState.type.getIconResId()
                                )
                            )
                        }
                        updatedDevices
                    }
                    Log.d("MainActivityViewModel", "Device state fetched successfully: $deviceState")
                } else if (response is YandexApiResponse.Error) {
                    _errorMessage.value = "Ошибка при получении состояния устройства"
                    Log.e("MainActivityViewModel", "Error fetching device state: ${response.error}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при получении состояния устройства"
                Log.e("MainActivityViewModel", "Error fetching device state", e)
            }
        }
    }

    // Проверка состояния перед отправкой запроса
//                if (capabilityObject?.state == newState) {
//                    return@launch
//                }

    fun manageDeviceCapability(deviceId: String, capabilityId: String, newState: CapabilityStateObjectData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val capabilityObject = _deviceList.value
                    .find { it.id == deviceId }
                    ?.capabilities
                    ?.find { it.id == capabilityId }
                    ?.toCapabilityObject()

                capabilityObject?.state = newState

                val manageDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(listOf(
                    DeviceActionsObject(
                        deviceId, mutableListOf(capabilityObject!!)
                    )
                ).map { it.toJson() })

                when (val response = client.manageDeviceCapabilitiesState(manageDeviceRequest)) {
                    is YandexApiResponse.SuccessManageDeviceCapabilitiesState -> {
                        _errorMessage.value = null
                        Log.d("MainActivityViewModel", "Device capability managed successfully: $response")
                        // Сброс состояния после успешной отправки запроса
                        _stateUpdates.value = null
                    }
                    is YandexApiResponse.Error -> {
                        _errorMessage.value = "Ошибка при изменении состояния устройства"
                        Log.e("MainActivityViewModel", "Error managing device capability: ${response.error}")
                    }
                    else -> {
                        Log.w("MainActivityViewModel", "Unexpected response: $response")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при изменении состояния устройства"
                Log.e("MainActivityViewModel", "Error managing device capability", e)
            }
        }
    }

    fun saveTokenAndUrl(token: String, url: String) {
        viewModelScope.launch {
            tokenRepository.saveTokenAndUrl(token, url)
            _deviceList.value = emptyList()
        }
    }

    fun getToken(): String? = tokenRepository.getToken()

    fun getUrl(): String? = tokenRepository.getUrl()
}