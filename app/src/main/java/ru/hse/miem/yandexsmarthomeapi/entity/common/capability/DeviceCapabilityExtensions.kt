package ru.hse.miem.yandexsmarthomeapi.entity.common.capability

import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.DeviceCapabilityObject

fun DeviceCapabilityObject.toCapabilityObject(): CapabilityObject {
    return CapabilityObject(
        type = type,
        state = state
    )
}

fun CapabilityObject.toDeviceCapabilityObject(old : DeviceCapabilityObject): DeviceCapabilityObject {
    return old.copy(
        type = type,
        state = state
    )
}