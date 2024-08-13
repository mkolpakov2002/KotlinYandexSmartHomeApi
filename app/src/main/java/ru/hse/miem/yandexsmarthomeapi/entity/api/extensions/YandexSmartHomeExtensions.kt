package ru.hse.miem.yandexsmarthomeapi.entity.api.extensions

import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexUserInfoResponse
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceActionsObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.GroupObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.HouseholdObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.RoomObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.ScenarioObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.SmartHomeInfo
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.DeviceCapabilityObject

/**
 * Файл содержит функции расширения для преобразования объектов Яндекс Умного дома
 */

/**
 * Преобразует DeviceObject в DeviceActionsObject
 */
fun DeviceObject.toDeviceActionsObject(): DeviceActionsObject {
    return DeviceActionsObject(
        id = this.id,
        actions = this.capabilities.map { it.toCapabilityObject() }.toMutableList()
    )
}

/**
 * Преобразует DeviceCapabilityObject в CapabilityObject
 */
fun DeviceCapabilityObject.toCapabilityObject(): CapabilityObject {
    return CapabilityObject(
        type = this.type,
        state = this.state
    )
}

/**
 * Преобразует YandexUserInfoResponse в SmartHomeInfo
 */
fun YandexUserInfoResponse.toSmartHomeInfo(): SmartHomeInfo {
    return SmartHomeInfo(
        rooms = this.rooms.map { it.toRoomObject() }.toMutableList(),
        groups = this.groups.map { it.toGroupObject() }.toMutableList(),
        devices = this.devices.map { it.toDeviceObject() }.toMutableList(),
        scenarios = this.scenarios.map { it.toScenarioObject() }.toMutableList(),
        households = this.households.map { it.toHouseholdObject() }.toMutableList()
    )
}

/**
 * Преобразует RoomObject из ответа Яндекса в RoomObject для SmartHomeInfo
 */
fun RoomObject.toRoomObject(): RoomObject {
    return this
}

/**
 * Преобразует GroupObject из ответа Яндекса в GroupObject для SmartHomeInfo
 */
fun GroupObject.toGroupObject(): GroupObject {
    return this
}

/**
 * Преобразует DeviceObject из ответа Яндекса в DeviceObject для SmartHomeInfo
 * Этот метод может быть расширен в будущем, если потребуется дополнительная логика преобразования
 */
fun DeviceObject.toDeviceObject(): DeviceObject {
    return this
}

/**
 * Преобразует ScenarioObject из ответа Яндекса в ScenarioObject для SmartHomeInfo
 */
fun ScenarioObject.toScenarioObject(): ScenarioObject {
    return this
}

/**
 * Преобразует HouseholdObject из ответа Яндекса в HouseholdObject для SmartHomeInfo
 */
fun HouseholdObject.toHouseholdObject(): HouseholdObject {
    return this
}