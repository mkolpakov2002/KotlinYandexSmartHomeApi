package ru.hse.miem.yandexsmarthomeapi.ui.views

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.ToggleButton
import androidx.annotation.OptIn
import androidx.core.graphics.toColor
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import pl.brightinventions.codified.enums.CodifiedEnum
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.R
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueInteger
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueObjectHSV
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueObjectScene
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.HSVObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectDataValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityStateObjectDataValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.EventPropertyStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.FloatPropertyStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyType
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.ui.models.DeviceCapabilityUIModel
import ru.hse.miem.yandexsmarthomeapi.ui.models.DevicePropertyUIModel
import kotlin.math.roundToInt

fun Context.getCapabilityTitle(capabilityTypeWrapper: CapabilityTypeWrapper): String {
    return when (capabilityTypeWrapper.type) {
        is CodifiedEnum.Known -> {
            when (capabilityTypeWrapper.type.value) {
                CapabilityType.ON_OFF -> getString(R.string.capability_on_off_title)
                CapabilityType.COLOR_SETTING -> getString(R.string.capability_color_setting_title)
                CapabilityType.RANGE -> getString(R.string.capability_range_title)
                CapabilityType.MODE -> getString(R.string.capability_mode_title)
                CapabilityType.TOGGLE -> getString(R.string.capability_toggle_title)
                CapabilityType.VIDEO_STREAM -> getString(R.string.capability_video_stream_title)
            }
        }
        is CodifiedEnum.Unknown -> getString(R.string.capability_unknown_title)
    }
}

fun Context.getPropertyTitle(propertyTypeWrapper: PropertyTypeWrapper): String {
    return when (propertyTypeWrapper.type) {
        is CodifiedEnum.Known -> {
            when (propertyTypeWrapper.type.value) {
                PropertyType.EVENT -> getString(R.string.property_event_title)
                PropertyType.FLOAT -> getString(R.string.property_float_title)
            }
        }
        is CodifiedEnum.Unknown -> getString(R.string.property_unknown_title)
    }
}

fun Context.createCardForCapability(capability: DeviceCapabilityUIModel): MaterialCardView {
    return MaterialCardView(this).apply {
        layoutParams = ViewGroup.LayoutParams(
            MATCH_PARENT,
            WRAP_CONTENT
        )
        setPadding(8, 8, 8, 8)
        cardElevation = 8f
        radius = 32f

        val title = MaterialTextView(this@createCardForCapability).apply {
            text = getCapabilityTitle(capability.type)
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }

        val view = createCapabilityView(capability)

        val layout = LinearLayout(this@createCardForCapability).apply {
            orientation = LinearLayout.VERTICAL
            addView(title)
            addView(view)
        }

        addView(layout)
    }
}

fun Context.createCardForProperty(property: DevicePropertyUIModel): MaterialCardView {
    return MaterialCardView(this).apply {
        layoutParams = ViewGroup.LayoutParams(
            MATCH_PARENT,
            WRAP_CONTENT
        )
        cardElevation = 8f
        radius = 32f
        setContentPadding(8, 8, 8, 8)

        val title = MaterialTextView(this@createCardForProperty).apply {
            text = getPropertyTitle(property.type)
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
            }
        }

        val view = createPropertyView(property)

        val layout = LinearLayout(this@createCardForProperty).apply {
            orientation = LinearLayout.VERTICAL
            addView(title)
            addView(view)
        }

        addView(layout)
    }
}

/**
 * Создает view для указанной возможности устройства.
 *
 * @param capability Модель возможности.
 * @return Созданный view.
 */
@OptIn(UnstableApi::class)
fun Context.createCapabilityView(capability: DeviceCapabilityUIModel): View {
    return when (capability.type.type.knownOrNull()) {
        CapabilityType.ON_OFF -> createOnOffCapabilityView(capability)
        CapabilityType.COLOR_SETTING -> createColorSettingCapabilityView(capability)
        CapabilityType.RANGE -> createRangeCapabilityView(capability)
        CapabilityType.MODE -> createModeCapabilityView(capability)
        CapabilityType.TOGGLE -> createToggleCapabilityView(capability)
        CapabilityType.VIDEO_STREAM -> createVideoStreamCapabilityView(capability)
        null -> throw Exception("Unknown capability type")
    }
}

/**
 * Создает view для свойства устройства.
 *
 * @param property Модель свойства.
 * @return Созданный view.
 */
fun Context.createPropertyView(property: DevicePropertyUIModel): View {
    return when (property.type.type.knownOrNull()) {
        PropertyType.FLOAT -> createFloatPropertyView(property)
        PropertyType.EVENT -> createEventPropertyView(property)
        null -> throw Exception("Unknown property type")
    }
}

/**
 * Создает view для возможности включения/выключения устройства.
 *
 * @param capability Модель возможности включения/выключения.
 * @return Созданный view.
 */
fun Context.createOnOffCapabilityView(capability: DeviceCapabilityUIModel): View {
    val layout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        setPadding(8, 8, 8, 8)
        gravity = Gravity.CENTER_VERTICAL
    }

    val state = capability.stateFlow.value as? OnOffCapabilityStateObjectData

    val label = MaterialTextView(this).apply {
        text = if (state?.value?.value == true) {
            context.getString(R.string.capability_on)
        } else {
            context.getString(R.string.capability_off)
        }
        textSize = 16f
        setPadding(0, 0, 8, 0)
    }

    val switch = SwitchMaterial(this).apply {
        isChecked = state?.value?.value ?: false
        isEnabled = capability.retrievable
        setOnCheckedChangeListener { _, isChecked ->
            val newState = state?.copy(value = OnOffCapabilityStateObjectValue(isChecked))
            capability.stateFlow.value = newState
            label.text = if (isChecked) {
                context.getString(R.string.capability_on)
            } else {
                context.getString(R.string.capability_off)
            }
        }
    }

    layout.addView(label)
    layout.addView(switch)

    return layout
}
/**
 * Создает view для настройки цвета устройства.
 *
 * @param capability Модель возможности настройки цвета.
 * @return Созданный view.
 */

fun Context.createColorSettingCapabilityView(capability: DeviceCapabilityUIModel): View {
    val layout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(8, 8, 8, 8)
    }

    val colorSetting = capability.parameters as? ColorSettingCapabilityParameterObject
    val state = capability.stateFlow.value as? ColorSettingCapabilityStateObjectData

    val colorView = MaterialCardView(this).apply {
        val colorInt = state?.value?.let {
            when (it) {
                is ColorSettingCapabilityStateObjectValueInteger -> {
                    this.visibility = VISIBLE
                    if(state.instance.colorSetting.code() == "rgb") {
                        it.value
                    } else {
                        val color = it.value.toFloat().kelvinToRgb()
                        Color.rgb(color.first.toInt(), color.second.toInt(), color.third.toInt())
                    }
                }
                is ColorSettingCapabilityStateObjectValueObjectScene -> {
                    this.visibility = GONE
                    Color.WHITE
                }
                is ColorSettingCapabilityStateObjectValueObjectHSV -> {
                    this.visibility = VISIBLE
                    Color.HSVToColor(
                        floatArrayOf(
                            it.value.h.toFloat(),
                            it.value.s / 100f,
                            it.value.v / 100f
                        )
                    )
                }
            }
        } ?: Color.WHITE
        setCardBackgroundColor(colorInt)
        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 100).apply {
            setMargins(0, 0, 0, 16)
        }
        radius = 16f
        elevation = 0f
    }

    layout.addView(colorView)

    colorSetting?.colorModel?.let {
        if (it.colorModel.code() == "rgb" || it.colorModel.code() == "hsv") {
            val sliders = createHSVSliders(state, capability) { newHsv ->
                val newColorInt = Color.HSVToColor(newHsv)
                colorView.setCardBackgroundColor(newColorInt)
            }
            sliders.forEach { slider ->
                layout.addView(slider)
            }
        }
    }

    colorSetting?.temperatureK?.let {
        val temperatureSlider = createTemperatureSlider(state, capability){
            val color = it.toFloat().kelvinToRgb()
            colorView.setCardBackgroundColor(Color.rgb(color.first.toInt(), color.second.toInt(), color.third.toInt()))
        }
        layout.addView(temperatureSlider)
    }

    return layout
}

private fun Context.createHSVSliders(
    state: ColorSettingCapabilityStateObjectData?,
    capability: DeviceCapabilityUIModel,
    onColorChange: (FloatArray) -> Unit
): List<View> {
    val hsv = state?.value?.let {
        when (it) {
            is ColorSettingCapabilityStateObjectValueObjectHSV -> floatArrayOf(
                it.value.h.toFloat(),
                it.value.s / 100f,
                it.value.v / 100f
            )
            is ColorSettingCapabilityStateObjectValueInteger -> {
                val color = Color.rgb(it.value shr 16 and 0xFF, it.value shr 8 and 0xFF, it.value and 0xFF)
                val hsvArray = FloatArray(3)
                Color.colorToHSV(color, hsvArray)
                hsvArray
            }
            else -> floatArrayOf(0f, 0f, 0f)
        }
    } ?: floatArrayOf(0f, 0f, 0f)

    val hueSlider = createSlider(getString(R.string.capability_color_setting_hue), hsv[0], 0f..360f, 1f) { value ->
        val newHsv = floatArrayOf(value, hsv[1], hsv[2])
        updateColorState(state, capability, newHsv)
        onColorChange(newHsv)
    }

    val saturationSlider = createSlider(getString(R.string.capability_color_setting_saturation), hsv[1] * 100, 0f..100f, 1f) { value ->
        val newHsv = floatArrayOf(hsv[0], value / 100f, hsv[2])
        updateColorState(state, capability, newHsv)
        onColorChange(newHsv)
    }

    val valueSlider = createSlider(getString(R.string.capability_color_setting_value), hsv[2] * 100, 0f..100f, 1f) { value ->
        val newHsv = floatArrayOf(hsv[0], hsv[1], value / 100f)
        updateColorState(state, capability, newHsv)
        onColorChange(newHsv)
    }

    return listOf(hueSlider, saturationSlider, valueSlider)
}

private fun Context.createTemperatureSlider(
    state: ColorSettingCapabilityStateObjectData?,
    capability: DeviceCapabilityUIModel,
    onTemperatureChange: (Int) -> Unit
): View {
    val temperatureK = (capability.parameters as? ColorSettingCapabilityParameterObject)?.temperatureK
    val temperatureState = state?.value as? ColorSettingCapabilityStateObjectValueInteger
    val temperature = temperatureState?.value ?: (temperatureK?.min ?: 4500)

    val minTemperature = temperatureK?.min?.toFloat() ?: 2000f
    val maxTemperature = temperatureK?.max?.toFloat() ?: 9000f

    return createSlider("Температура (K)", temperature.toFloat(), minTemperature..maxTemperature, 1f) { value ->
        val newState = state?.copy(
            instance = ColorSettingCapabilityStateObjectInstanceWrapper(ColorSettingCapabilityStateObjectInstance.TEMPERATURE_K.codifiedEnum()),
            value = ColorSettingCapabilityStateObjectValueInteger(value.toInt())
        )
        onTemperatureChange(value.toInt())
        capability.stateFlow.value = newState
    }
}

private fun Context.createSlider(
    label: String,
    initialValue: Float,
    range: ClosedFloatingPointRange<Float>,
    stepSize: Float,
    onValueChange: (Float) -> Unit
): View {
    val linearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(8, 8, 8, 8)
    }

    val textView = MaterialTextView(this).apply {
        text = "$label: ${initialValue.toInt()}"
    }

    val slider = Slider(this).apply {
        valueFrom = range.start
        valueTo = range.endInclusive
        this.stepSize = stepSize

        value = ((initialValue - valueFrom) / stepSize).roundToInt() * stepSize + valueFrom

        if (value < valueFrom) value = valueFrom
        if (value > valueTo) value = valueTo

        addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                textView.text = "$label: ${value.toInt()}"
                onValueChange(value)
            }
        }
    }

    linearLayout.addView(textView)
    linearLayout.addView(slider)

    return linearLayout
}


private fun updateColorState(
    state: ColorSettingCapabilityStateObjectData?,
    capability: DeviceCapabilityUIModel,
    hsv: FloatArray
) {
    val newState = ColorSettingCapabilityStateObjectData(
        instance = ColorSettingCapabilityStateObjectInstanceWrapper(ColorSettingCapabilityStateObjectInstance.HSV.codifiedEnum()),
        value = ColorSettingCapabilityStateObjectValueObjectHSV(
            HSVObject(h = hsv[0].toInt(), s = (hsv[1] * 100).toInt(), v = (hsv[2] * 100).toInt())
        )
    )
    capability.stateFlow.value = newState
}

/**
 * Создает view для настройки диапазона устройства.
 *
 * @param capability Модель возможности настройки диапазона.
 * @return Созданный view.
 */
fun Context.createRangeCapabilityView(capability: DeviceCapabilityUIModel): View {
    val layout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(8, 8, 8, 8)
    }

    val rangeSetting = capability.parameters as? RangeCapabilityParameterObject
    val state = capability.stateFlow.value as? RangeCapabilityStateObjectData

    rangeSetting?.let {
        val rangeSlider = createRangeSlider(state, capability, it)
        layout.addView(rangeSlider)
    }

    return layout
}

private fun Context.createRangeSlider(
    state: RangeCapabilityStateObjectData?,
    capability: DeviceCapabilityUIModel,
    rangeSetting: RangeCapabilityParameterObject
): View {
    val range = rangeSetting.range
    val min = range?.min ?: 0f
    val max = range?.max ?: 100f
    val precision = range?.precision ?: 1f
    val initialValue = state?.value?.value ?: min

    return createSlider(
        label = rangeSetting.getRangeCapabilityTitle(this),
        initialValue = initialValue,
        range = min..max,
        stepSize = precision
    ) { value ->
        val newState = state?.copy(value = RangeCapabilityStateObjectDataValue(value))
        capability.stateFlow.value = newState
    }
}

/**
 * Создает view для настройки режима устройства.
 *
 * @param capability Модель возможности настройки режима.
 * @return Созданный view.
 */
fun Context.createModeCapabilityView(capability: DeviceCapabilityUIModel): View {
    val layout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(8, 8, 8, 8)
    }

    val modeCapabilityParameter = capability.parameters as? ModeCapabilityParameterObject
    val state = capability.stateFlow.value as? ModeCapabilityStateObjectData
    val modes = modeCapabilityParameter?.modes ?: emptyList()

    val label = MaterialTextView(this).apply {
        text = modeCapabilityParameter?.getModeCapabilityTitle(this@createModeCapabilityView)
        textSize = 16f
        setPadding(0, 0, 0, 8)
    }

    val spinner = Spinner(this).apply {
        val adapter = ArrayAdapter(this@createModeCapabilityView, android.R.layout.simple_spinner_item, modes.map { it.getModeTitle(this@createModeCapabilityView) })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = adapter
        setSelection(modes.indexOfFirst { it.value == state?.value })
        isEnabled = capability.retrievable
    }

    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val newState = state?.copy(value = modes[position].value)
            capability.stateFlow.value = newState
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    layout.addView(label)
    layout.addView(spinner)

    return layout
}

/**
 * Создает view для возможности переключения устройства.
 *
 * @param capability Модель возможности переключения.
 * @return Созданный view.
 */
fun Context.createToggleCapabilityView(capability: DeviceCapabilityUIModel): View {
    val layout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(8, 8, 8, 8)
    }

    val toggle = capability.parameters as? ToggleCapabilityParameterObject
    val state = capability.stateFlow.value as? ToggleCapabilityStateObjectData

    val label = MaterialTextView(this).apply {
        text = toggle?.getToggleCapabilityTitle(this@createToggleCapabilityView)
        textSize = 16f
        setPadding(0, 0, 0, 8)
    }

    val toggleButton = ToggleButton(this).apply {
        isChecked = state?.value?.value ?: false
        isEnabled = capability.retrievable
        setOnCheckedChangeListener { _, isChecked ->
            val newState = state?.copy(value = ToggleCapabilityStateObjectDataValue(isChecked))
            capability.stateFlow.value = newState
        }
    }

    layout.addView(label)
    layout.addView(toggleButton)

    return layout
}

/**
 * Создает view для настройки видеопотока устройства с помощью AndroidX Media3.
 *
 * @param capability Модель возможности настройки видеопотока.
 * @return Созданный view.
 */
@UnstableApi
fun Context.createVideoStreamCapabilityView(capability: DeviceCapabilityUIModel): View {
    val playerView = PlayerView(this)
    val player = ExoPlayer.Builder(this).build()
    playerView.player = player

    TODO("Not yet implemented")
}

/**
 * Создает view для свойства с плавающей точкой.
 *
 * @param property Модель свойства с плавающей точкой.
 * @return Созданный view.
 */
fun Context.createFloatPropertyView(property: DevicePropertyUIModel): View {
    val textView = MaterialTextView(this)
    val state = property.stateFlow.value as? FloatPropertyStateObjectData
    textView.text = state?.state?.propertyValue?.value.toString()
    textView.isEnabled = false
    return textView
}

/**
 * Создает view для события свойства.
 *
 * @param property Модель события свойства.
 * @return Созданный view.
 */
fun Context.createEventPropertyView(property: DevicePropertyUIModel): View {
    val textView = MaterialTextView(this)
    val state = property.stateFlow.value as? EventPropertyStateObjectData
    textView.text = state?.state?.propertyValue?.value?.value?.code() ?: "Unknown"
    textView.isEnabled = false
    return textView
}