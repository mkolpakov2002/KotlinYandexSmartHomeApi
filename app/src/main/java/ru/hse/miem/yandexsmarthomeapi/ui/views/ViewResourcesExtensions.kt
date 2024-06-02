package ru.hse.miem.yandexsmarthomeapi.ui.views

import android.content.Context
import pl.brightinventions.codified.enums.CodifiedEnum
import ru.hse.miem.yandexsmarthomeapi.R
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceType
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityMode
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityParameterObject

fun DeviceTypeWrapper.getIconResId(): Int {
    return when (this.type) {
        is CodifiedEnum.Known -> {
            when (this.type.value) {
                DeviceType.YANDEX_SMART_SPEAKER -> R.drawable.ya_icon
                DeviceType.LIGHT -> R.drawable.light
                DeviceType.SOCKET -> R.drawable.socket
                DeviceType.SWITCH -> R.drawable.ya_switch
                DeviceType.THERMOSTAT -> R.drawable.thermostat
                DeviceType.THERMOSTAT_AC -> R.drawable.thermostat_ac
                DeviceType.MEDIA_DEVICE -> R.drawable.media_device
                DeviceType.MEDIA_DEVICE_TV -> R.drawable.media_device_tv
                DeviceType.MEDIA_DEVICE_TV_BOX -> R.drawable.media_device_tv_box
                DeviceType.MEDIA_DEVICE_RECEIVER -> R.drawable.media_device_receiver
                DeviceType.COOKING -> R.drawable.cooking
                DeviceType.COFFEE_MAKER -> R.drawable.cooking_coffee_maker
                DeviceType.KETTLE -> R.drawable.cooking_kettle
                DeviceType.MULTICOOKER -> R.drawable.cooking_multicooker
                DeviceType.OPENABLE -> R.drawable.openable
                DeviceType.OPENABLE_CURTAIN -> R.drawable.openable_curtain
                DeviceType.HUMIDIFIER -> R.drawable.humidifier
                DeviceType.PURIFIER -> R.drawable.purifier
                DeviceType.VACUUM_CLEANER -> R.drawable.vacuum_cleaner
                DeviceType.WASHING_MACHINE -> R.drawable.washing_machine
                DeviceType.DISHWASHER -> R.drawable.dishwasher
                DeviceType.IRON -> R.drawable.iron
                DeviceType.SENSOR -> R.drawable.sensor
                DeviceType.SENSOR_MOTION -> R.drawable.sensor_motion
                DeviceType.SENSOR_DOOR -> R.drawable.sensor_open
                DeviceType.SENSOR_WINDOW -> R.drawable.sensor_open
                DeviceType.SENSOR_WATER_LEAK -> R.drawable.sensor_water_leak
                DeviceType.SENSOR_SMOKE -> R.drawable.sensor_smoke
                DeviceType.SENSOR_GAS -> R.drawable.sensor_gas
                DeviceType.SENSOR_VIBRATION -> R.drawable.sensor_vibration
                DeviceType.SENSOR_BUTTON -> R.drawable.sensor_button
                DeviceType.SENSOR_ILLUMINATION -> R.drawable.sensor_illumination
                DeviceType.OTHER -> R.drawable.other
            }
        }
        is CodifiedEnum.Unknown -> R.drawable.other
    }
}

fun ToggleCapabilityParameterObject.getToggleCapabilityTitle(context: Context): String {
    return when (this.instance.toggle) {
        is CodifiedEnum.Known -> {
            when (this.instance.toggle.value) {
                ToggleCapability.BACKLIGHT -> context.getString(R.string.capability_backlight_title)
                ToggleCapability.CONTROLS_LOCKED -> context.getString(R.string.capability_controls_locked_title)
                ToggleCapability.IONIZATION -> context.getString(R.string.capability_ionization_title)
                ToggleCapability.KEEP_WARM -> context.getString(R.string.capability_keep_warm_title)
                ToggleCapability.MUTE -> context.getString(R.string.capability_mute_title)
                ToggleCapability.OSCILLATION -> context.getString(R.string.capability_oscillation_title)
                ToggleCapability.PAUSE -> context.getString(R.string.capability_pause_title)
            }
        }
        is CodifiedEnum.Unknown -> this.instance.toggle.code()
    }
}

fun ModeCapabilityParameterObject.getModeCapabilityTitle(context: Context): String {
    return when (this.instance.mode) {
        is CodifiedEnum.Known -> {
            when (this.instance.mode.value) {
                ModeCapability.CLEANUP_MODE -> context.getString(R.string.capability_cleanup_mode_title)
                ModeCapability.COFFEE_MODE -> context.getString(R.string.capability_coffee_mode_title)
                ModeCapability.DISHWASHING -> context.getString(R.string.capability_dishwashing_title)
                ModeCapability.FAN_SPEED -> context.getString(R.string.capability_fan_speed_title)
                ModeCapability.HEAT -> context.getString(R.string.capability_heat_title)
                ModeCapability.INPUT_SOURCE -> context.getString(R.string.capability_input_source_title)
                ModeCapability.PROGRAM -> context.getString(R.string.capability_program_title)
                ModeCapability.SWING -> context.getString(R.string.capability_swing_title)
                ModeCapability.TEA_MODE -> context.getString(R.string.capability_tea_mode_title)
                ModeCapability.THERMOSTAT -> context.getString(R.string.capability_thermostat_title)
                ModeCapability.WORK_SPEED -> context.getString(R.string.capability_work_speed_title)
            }
        }
        is CodifiedEnum.Unknown -> this.instance.mode.code()
    }
}

fun ModeObject.getModeTitle(context: Context): String {
    return when (this.value.mode) {
        is CodifiedEnum.Known -> {
            when (this.value.mode.value) {
                ModeCapabilityMode.AUTO -> context.getString(R.string.mode_auto_title)
                ModeCapabilityMode.ECO -> context.getString(R.string.mode_eco_title)
                ModeCapabilityMode.SMART -> context.getString(R.string.mode_smart_title)
                ModeCapabilityMode.TURBO -> context.getString(R.string.mode_turbo_title)
                ModeCapabilityMode.COOL -> context.getString(R.string.mode_cool_title)
                ModeCapabilityMode.DRY -> context.getString(R.string.mode_dry_title)
                ModeCapabilityMode.FAN_ONLY -> context.getString(R.string.mode_fan_only_title)
                ModeCapabilityMode.HEAT -> context.getString(R.string.mode_heat_title)
                ModeCapabilityMode.PREHEAT -> context.getString(R.string.mode_preheat_title)
                ModeCapabilityMode.HIGH -> context.getString(R.string.mode_high_title)
                ModeCapabilityMode.LOW -> context.getString(R.string.mode_low_title)
                ModeCapabilityMode.MEDIUM -> context.getString(R.string.mode_medium_title)
                ModeCapabilityMode.MAX -> context.getString(R.string.mode_max_title)
                ModeCapabilityMode.MIN -> context.getString(R.string.mode_min_title)
                ModeCapabilityMode.FAST -> context.getString(R.string.mode_fast_title)
                ModeCapabilityMode.SLOW -> context.getString(R.string.mode_slow_title)
                ModeCapabilityMode.EXPRESS -> context.getString(R.string.mode_express_title)
                ModeCapabilityMode.NORMAL -> context.getString(R.string.mode_normal_title)
                ModeCapabilityMode.QUIET -> context.getString(R.string.mode_quiet_title)
                ModeCapabilityMode.HORIZONTAL -> context.getString(R.string.mode_horizontal_title)
                ModeCapabilityMode.STATIONARY -> context.getString(R.string.mode_stationary_title)
                ModeCapabilityMode.VERTICAL -> context.getString(R.string.mode_vertical_title)
                ModeCapabilityMode.ONE -> context.getString(R.string.mode_one_title)
                ModeCapabilityMode.TWO -> context.getString(R.string.mode_two_title)
                ModeCapabilityMode.THREE -> context.getString(R.string.mode_three_title)
                ModeCapabilityMode.FOUR -> context.getString(R.string.mode_four_title)
                ModeCapabilityMode.FIVE -> context.getString(R.string.mode_five_title)
                ModeCapabilityMode.SIX -> context.getString(R.string.mode_six_title)
                ModeCapabilityMode.SEVEN -> context.getString(R.string.mode_seven_title)
                ModeCapabilityMode.EIGHT -> context.getString(R.string.mode_eight_title)
                ModeCapabilityMode.NINE -> context.getString(R.string.mode_nine_title)
                ModeCapabilityMode.TEN -> context.getString(R.string.mode_ten_title)
                ModeCapabilityMode.AMERICANO -> context.getString(R.string.mode_americano_title)
                ModeCapabilityMode.CAPPUCCINO -> context.getString(R.string.mode_cappuccino_title)
                ModeCapabilityMode.DOUBLE -> context.getString(R.string.mode_double_title)
                ModeCapabilityMode.ESPRESSO -> context.getString(R.string.mode_espresso_title)
                ModeCapabilityMode.DOUBLE_ESPRESSO -> context.getString(R.string.mode_double_espresso_title)
                ModeCapabilityMode.LATTE -> context.getString(R.string.mode_latte_title)
                ModeCapabilityMode.BLACK_TEA -> context.getString(R.string.mode_black_tea_title)
                ModeCapabilityMode.FLOWER_TEA -> context.getString(R.string.mode_flower_tea_title)
                ModeCapabilityMode.GREEN_TEA -> context.getString(R.string.mode_green_tea_title)
                ModeCapabilityMode.HERBAL_TEA -> context.getString(R.string.mode_herbal_tea_title)
                ModeCapabilityMode.OOLONG_TEA -> context.getString(R.string.mode_oolong_tea_title)
                ModeCapabilityMode.PUERH_TEA -> context.getString(R.string.mode_puerh_tea_title)
                ModeCapabilityMode.RED_TEA -> context.getString(R.string.mode_red_tea_title)
                ModeCapabilityMode.WHITE_TEA -> context.getString(R.string.mode_white_tea_title)
                ModeCapabilityMode.GLASS -> context.getString(R.string.mode_glass_title)
                ModeCapabilityMode.INTENSIVE -> context.getString(R.string.mode_intensive_title)
                ModeCapabilityMode.PRE_RINSE -> context.getString(R.string.mode_pre_rinse_title)
                ModeCapabilityMode.ASPIC -> context.getString(R.string.mode_aspic_title)
                ModeCapabilityMode.BABY_FOOD -> context.getString(R.string.mode_baby_food_title)
                ModeCapabilityMode.BAKING -> context.getString(R.string.mode_baking_title)
                ModeCapabilityMode.BREAD -> context.getString(R.string.mode_bread_title)
                ModeCapabilityMode.BOILING -> context.getString(R.string.mode_boiling_title)
                ModeCapabilityMode.CEREALS -> context.getString(R.string.mode_cereals_title)
                ModeCapabilityMode.CHEESECAKE -> context.getString(R.string.mode_cheesecake_title)
                ModeCapabilityMode.DEEP_FRYER -> context.getString(R.string.mode_deep_fryer_title)
                ModeCapabilityMode.DESSERT -> context.getString(R.string.mode_dessert_title)
                ModeCapabilityMode.FOWL -> context.getString(R.string.mode_fowl_title)
                ModeCapabilityMode.FRYING -> context.getString(R.string.mode_frying_title)
                ModeCapabilityMode.MACARONI -> context.getString(R.string.mode_macaroni_title)
                ModeCapabilityMode.MILK_PORRIDGE -> context.getString(R.string.mode_milk_porridge_title)
                ModeCapabilityMode.MULTICOOKER -> context.getString(R.string.mode_multicooker_title)
                ModeCapabilityMode.PASTA -> context.getString(R.string.mode_pasta_title)
                ModeCapabilityMode.PILAF -> context.getString(R.string.mode_pilaf_title)
                ModeCapabilityMode.PIZZA -> context.getString(R.string.mode_pizza_title)
                ModeCapabilityMode.SAUCE -> context.getString(R.string.mode_sauce_title)
                ModeCapabilityMode.SLOW_COOK -> context.getString(R.string.mode_slow_cook_title)
                ModeCapabilityMode.SOUP -> context.getString(R.string.mode_soup_title)
                ModeCapabilityMode.STEAM -> context.getString(R.string.mode_steam_title)
                ModeCapabilityMode.STEWING -> context.getString(R.string.mode_stewing_title)
                ModeCapabilityMode.VACUUM -> context.getString(R.string.mode_vacuum_title)
                ModeCapabilityMode.YOGURT -> context.getString(R.string.mode_yogurt_title)
            }
        }
        is CodifiedEnum.Unknown -> this.value.mode.code()
    }
}

fun RangeCapabilityParameterObject.getRangeCapabilityTitle(context: Context): String {
    return when (this.instance.range) {
        is CodifiedEnum.Known -> {
            when (this.instance.range.value) {
                RangeCapability.OPEN -> context.getString(R.string.capability_open_title)
                RangeCapability.BRIGHTNESS -> context.getString(R.string.capability_brightness_title)
                RangeCapability.HUMIDITY -> context.getString(R.string.capability_humidity_title)
                RangeCapability.CHANNEL -> context.getString(R.string.capability_channel_title)
                RangeCapability.VOLUME -> context.getString(R.string.capability_volume_title)
                RangeCapability.TEMPERATURE -> context.getString(R.string.capability_temperature_title)
            }
        }
        is CodifiedEnum.Unknown -> this.instance.range.code()
    }
}