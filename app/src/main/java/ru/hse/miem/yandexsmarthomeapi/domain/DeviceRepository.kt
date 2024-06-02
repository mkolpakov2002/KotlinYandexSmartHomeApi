package ru.hse.miem.yandexsmarthomeapi.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import ru.hse.miem.yandexsmarthomeapi.App
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexApiResponse
import ru.hse.miem.yandexsmarthomeapi.entity.common.toDeviceObject
import ru.hse.miem.yandexsmarthomeapi.ui.models.DeviceCapabilityUIModel
import ru.hse.miem.yandexsmarthomeapi.ui.models.DevicePropertyUIModel
import ru.hse.miem.yandexsmarthomeapi.ui.models.DeviceUIModel

class DeviceRepository {

    private val tokenRepository = TokenRepository.getInstance()

    suspend fun getDevices(): List<DeviceUIModel> {
        val token = tokenRepository.getToken()
        val url = tokenRepository.getUrl()

        if (token != null && url != null) {
            val client = YandexSmartHomeClient.getInstance(url, token)
            val response = client.getUserInfo()

            if (response is YandexApiResponse.SuccessUserInfo) {
                return withContext(Dispatchers.Default) {
                    response.data.devices.map {
                        val device = it.toDeviceObject()
                        DeviceUIModel(
                            id = device.id,
                            name = device.name,
                            type = device.type,
                            capabilities = device.capabilities.map { capability ->
                                DeviceCapabilityUIModel(
                                    id = capability.type.type.code(),
                                    name = capability.type.type.code(),
                                    type = capability.type,
                                    parameters = capability.parameters,
                                    retrievable = capability.retrievable,
                                    reportable = capability.reportable,
                                    stateFlow = MutableStateFlow(capability.state)
                                )
                            }.toMutableList(),
                            properties = device.properties.map { property ->
                                DevicePropertyUIModel(
                                    id = property.type.type.code(),
                                    name = property.type.type.code(),
                                    type = property.type,
                                    parameters = property.parameters,
                                    retrievable = property.retrievable,
                                    reportable = property.reportable,
                                    stateFlow = MutableStateFlow(property.state)
                                )
                            }.toMutableList()
                        )
                    }
                }
            }
        }
        return emptyList()
    }
}