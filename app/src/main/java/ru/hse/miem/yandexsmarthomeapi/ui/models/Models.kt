package ru.hse.miem.yandexsmarthomeapi.ui.models

import kotlinx.coroutines.flow.MutableStateFlow
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceType
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyType
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.ui.views.getIconResId

data class DeviceCapabilityUIModel(
    val id: String,
    val name: String,
    val type: CapabilityTypeWrapper,
    val parameters: CapabilityParameterObject?,
    val retrievable: Boolean,
    val reportable: Boolean,
    var position: Int = 0,
    val stateFlow: MutableStateFlow<CapabilityStateObjectData?>
) {
    fun toCapabilityObject(): CapabilityObject {
        return CapabilityObject(
            type = type,
            state = stateFlow.value
        )
    }
}

data class DevicePropertyUIModel(
    val id: String,
    val name: String,
    val type: PropertyTypeWrapper,
    val parameters: PropertyParameterObject?,
    val retrievable: Boolean,
    val reportable: Boolean,
    var position: Int = 0,
    val stateFlow: MutableStateFlow<PropertyStateObjectData?>
)

data class DeviceUIModel(
    val id: String,
    val name: String,
    val type: DeviceTypeWrapper,
    val capabilities: MutableList<DeviceCapabilityUIModel>,
    val properties: MutableList<DevicePropertyUIModel>,
    val iconResId: Int = type.getIconResId()
)