package ru.hse.miem.yandexsmarthomeapi.domain

import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexApiResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageDeviceCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageGroupCapabilitiesStateRequest

interface YandexSmartHomeApi {
    suspend fun getUserInfo(): YandexApiResponse
    suspend fun getDeviceState(deviceId: String): YandexApiResponse
    suspend fun manageDeviceCapabilitiesState(request: YandexManageDeviceCapabilitiesStateRequest): YandexApiResponse
    suspend fun manageGroupCapabilitiesState(groupId: String, request: YandexManageGroupCapabilitiesStateRequest): YandexApiResponse
    suspend fun getDeviceGroup(groupId: String): YandexApiResponse
}