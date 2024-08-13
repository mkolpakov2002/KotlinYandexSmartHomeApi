package ru.hse.miem.yandexsmarthomeapi.ui.device_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import ru.hse.miem.yandexsmarthomeapi.ui.device_list.DeviceListViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import chaintech.videoplayer.model.PlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.domain.YandexSmartHomeClient
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceType
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.MeasurementUnitWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ActionResult
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorModel
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueRGB
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueObjectHSV
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.DeviceCapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.HSVObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ModeCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectDataValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.Status
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.StatusWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.TemperatureK
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ToggleCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.VideoStreamCapabilityStateObjectResponseValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.DevicePropertyObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.FloatObjectValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.FloatPropertyParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.FloatPropertyState
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.FloatPropertyStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.MeasurementUnit
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyFunction
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyFunctionWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyType
import ru.hse.miem.yandexsmarthomeapi.entity.common.property.PropertyTypeWrapper
import ru.hse.miem.yandexsmarthomeapi.ui.views.getIconResId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    deviceId: String,
    viewModel: DeviceListViewModel = viewModel(),
    onDismiss: () -> Unit,
    isPreview: Boolean = false
) {
    val device by viewModel.getDeviceById(deviceId).collectAsState(initial = null)
    val sheetState = rememberModalBottomSheetState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (device != null) {
            if (isPreview) {
                // В режиме предпросмотра показываем содержимое напрямую
                DeviceDetailContent(device = device!!, viewModel = viewModel)
            } else {
                // В обычном режиме используем ModalBottomSheet
                ModalBottomSheet(
                    onDismissRequest = onDismiss,
                    sheetState = sheetState
                ) {
                    DeviceDetailContent(device = device!!, viewModel = viewModel)
                }
            }
        } else {
            Text("Устройство не найдено")
        }
    }
}

@Composable
fun DeviceDetailContent(device: DeviceObject, viewModel: DeviceListViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DeviceStatusSection(device = device, viewModel = viewModel)
        DeviceControlSection(device = device, viewModel = viewModel)
    }
}

@Composable
fun DeviceStatusSection(device: DeviceObject, viewModel: DeviceListViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = device.type.getIconResId()),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(device.name, style = MaterialTheme.typography.titleLarge)
                Text(device.type.type.code(), style = MaterialTheme.typography.bodyMedium)
            }
            OnOffControl(
                device = device,
                capability = device.capabilities.firstOrNull { it.type.type == CapabilityType.ON_OFF.codifiedEnum() },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun DeviceControlSection(device: DeviceObject, viewModel: DeviceListViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        device.capabilities
            .filter { it.type.type != CapabilityType.ON_OFF.codifiedEnum() }
            .forEach { capability ->
                DeviceCapabilityControl(device = device, capability = capability, viewModel = viewModel)
            }
    }
}

@Composable
fun DeviceCapabilityControl(device: DeviceObject, capability: DeviceCapabilityObject, viewModel: DeviceListViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (capability.type.type.knownOrNull()) {
                CapabilityType.COLOR_SETTING -> ColorSettingControl(device, capability, viewModel)
                CapabilityType.MODE -> ModeControl(device, capability, viewModel)
                CapabilityType.RANGE -> RangeControl(device, capability, viewModel)
                CapabilityType.TOGGLE -> ToggleControl(device, capability, viewModel)
                CapabilityType.VIDEO_STREAM -> VideoStreamControl(device, capability, viewModel)
                CapabilityType.ON_OFF -> OnOffControl(device, capability, viewModel)
                null -> Text("Неизвестная возможность", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun OnOffControl(device: DeviceObject, capability: DeviceCapabilityObject?, viewModel: DeviceListViewModel) {
    val state = capability?.state as? OnOffCapabilityStateObjectData
    val isOn = state?.value?.value ?: false

    Text(
        text = if (isOn) "Вкл." else "Выкл.",
        style = MaterialTheme.typography.titleLarge
    )
    Switch(
        checked = isOn,
        onCheckedChange = { newState ->
            viewModel.updateDeviceCapability(device.id, capability?.type?.type?.code() ?: "", newState)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            uncheckedTrackColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun ColorSettingControl(device: DeviceObject, capability: DeviceCapabilityObject, viewModel: DeviceListViewModel) {
    val state = capability.state as? ColorSettingCapabilityStateObjectData
    val colorModel = (capability.parameters as? ColorSettingCapabilityParameterObject)?.colorModel?.colorModel
    when (colorModel?.knownOrNull()) {
        ColorModel.RGB -> RGBColorPicker(device, state, viewModel)
        ColorModel.HSV -> HSVColorPicker(device, state, viewModel)
        null -> TemperatureControl(device, capability, viewModel)
    }
}

@Composable
fun RGBColorPicker(device: DeviceObject, state: ColorSettingCapabilityStateObjectData?, viewModel: DeviceListViewModel) {
    var red by remember { mutableFloatStateOf(0f) }
    var green by remember { mutableFloatStateOf(0f) }
    var blue by remember { mutableFloatStateOf(0f) }

    state?.let {
        val colorValue = it.value as? ColorSettingCapabilityStateObjectValueRGB
        colorValue?.let { rgb ->
            red = (rgb.value shr 16 and 0xFF) / 255f
            green = (rgb.value shr 8 and 0xFF) / 255f
            blue = (rgb.value and 0xFF) / 255f
        }
    }

    val currentColor = Color(red, green, blue)

    Column {
        Text("RGB Color", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        ColorIndicator(currentColor)
        Spacer(modifier = Modifier.height(16.dp))
        ColorSlider("Red", red, Color.Red) { red = it; updateRGBColor(device, viewModel, red, green, blue) }
        ColorSlider("Green", green, Color.Green) { green = it; updateRGBColor(device, viewModel, red, green, blue) }
        ColorSlider("Blue", blue, Color.Blue) { blue = it; updateRGBColor(device, viewModel, red, green, blue) }
    }
}

@Composable
fun ColorSlider(label: String, value: Float, trackColor: Color, onValueChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.width(80.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = trackColor,
                activeTrackColor = trackColor,
                inactiveTrackColor = trackColor.copy(alpha = 0.3f)
            )
        )
        Text(String.format("%.0f", value * 255), modifier = Modifier.width(40.dp))
    }
}

fun updateRGBColor(device: DeviceObject, viewModel: DeviceListViewModel, red: Float, green: Float, blue: Float) {
    val rgbValue = (red * 255).toInt() shl 16 or ((green * 255).toInt() shl 8) or (blue * 255).toInt()
    viewModel.updateDeviceCapability(device.id, CapabilityType.COLOR_SETTING.code, rgbValue)
}

fun updateHSVColor(device: DeviceObject, viewModel: DeviceListViewModel, hue: Float, saturation: Float, value: Float) {
    val hsvObject = HSVObject((hue * 360).toInt(), (saturation * 100).toInt(), (value * 100).toInt())
    viewModel.updateDeviceCapability(device.id, CapabilityType.COLOR_SETTING.code, hsvObject)
}

fun temperatureToColor(temperature: Float): Color {
    // Простая реализация преобразования температуры в цвет
    // Это приближение, для более точного результата нужен более сложный алгоритм
    val t = temperature.coerceIn(2700f, 6500f)
    val k = (t - 2700) / (6500 - 2700)
    return Color(
        red = 1f,
        green = (0.5f + 0.5f * k).coerceIn(0f, 1f),
        blue = k.coerceIn(0f, 1f)
    )
}

@Composable
fun HSVColorPicker(device: DeviceObject, state: ColorSettingCapabilityStateObjectData?, viewModel: DeviceListViewModel) {
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var value by remember { mutableFloatStateOf(1f) }

    state?.let {
        val colorValue = it.value as? ColorSettingCapabilityStateObjectValueObjectHSV
        colorValue?.let { hsv ->
            hue = hsv.value.h.toFloat() / 360f
            saturation = hsv.value.s.toFloat() / 100f
            value = hsv.value.v.toFloat() / 100f
        }
    }

    val currentColor = Color.hsv(hue * 360, saturation, value)

    Column {
        Text("HSV Color", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        ColorIndicator(currentColor)
        Spacer(modifier = Modifier.height(16.dp))
        ColorSlider("Hue", hue, Color.Red) { hue = it; updateHSVColor(device, viewModel, hue, saturation, value) }
        ColorSlider("Saturation", saturation, Color.Gray) { saturation = it; updateHSVColor(device, viewModel, hue, saturation, value) }
        ColorSlider("Value", value, Color.Black) { value = it; updateHSVColor(device, viewModel, hue, saturation, value) }
    }
}

@Composable
fun TemperatureControl(device: DeviceObject, capability: DeviceCapabilityObject, viewModel: DeviceListViewModel) {
    val state = capability.state as? ColorSettingCapabilityStateObjectData
    val temperatureK = (capability.parameters as? ColorSettingCapabilityParameterObject)?.temperatureK
    var temperature by remember { mutableFloatStateOf((state?.value as? ColorSettingCapabilityStateObjectValueRGB)?.value?.toFloat() ?: 2700f) }

    val currentColor = temperatureToColor(temperature)

    Column {
        Text("Color Temperature", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        ColorIndicator(currentColor)
        Spacer(modifier = Modifier.height(16.dp))
        Slider(
            value = temperature,
            onValueChange = {
                temperature = it
                viewModel.updateDeviceCapability(device.id, CapabilityType.COLOR_SETTING.code, temperature.toInt())
            },
            valueRange = (temperatureK?.min?.toFloat() ?: 2700f)..(temperatureK?.max?.toFloat() ?: 6500f),
            steps = 38 // (6500 - 2700) / 100
        )
        Text("${temperature.toInt()} K")
    }
}

@Composable
fun ColorIndicator(color: Color) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(color)
            .border(1.dp, Color.Black)
    )
}

@Composable
fun ModeControl(device: DeviceObject, capability: DeviceCapabilityObject, viewModel: DeviceListViewModel) {
    val state = capability.state as? ModeCapabilityStateObjectData
    val modes = (capability.parameters as? ModeCapabilityParameterObject)?.modes ?: emptyList()
    val currentMode = state?.value?.mode?.code()

    Column {
        Text("Modes", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        modes.forEach { mode ->
            val isSelected = mode.value.mode.code() == currentMode
            FilterChip(
                selected = isSelected,
                onClick = {
                    viewModel.updateDeviceCapability(device.id, CapabilityType.MODE.code, mode.value.mode.code())
                },
                label = { Text(mode.value.mode.code()) },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun RangeControl(device: DeviceObject, capability: DeviceCapabilityObject, viewModel: DeviceListViewModel) {
    val state = capability.state as? RangeCapabilityStateObjectData
    val range = (capability.parameters as? RangeCapabilityParameterObject)?.range
    var currentValue by remember { mutableFloatStateOf(state?.value?.value ?: 0f) }

    Column {
        Text((capability.parameters as? RangeCapabilityParameterObject)?.instance?.range?.code() ?: "Range", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = currentValue,
            onValueChange = {
                currentValue = it
                viewModel.updateDeviceCapability(device.id, CapabilityType.RANGE.code, currentValue)
            },
            valueRange = (range?.min ?: 0f)..(range?.max ?: 100f),
            steps = ((range?.max ?: 100f) - (range?.min ?: 0f) / (range?.precision ?: 1f)).toInt()
        )
        Text("${currentValue.toInt()} ${(capability.parameters as? RangeCapabilityParameterObject)?.unit?.unit?.code() ?: ""}")
    }
}

@Composable
fun ToggleControl(device: DeviceObject, capability: DeviceCapabilityObject, viewModel: DeviceListViewModel) {
    val state = capability.state as? ToggleCapabilityStateObjectData
    var isOn by remember { mutableStateOf(state?.value?.value ?: false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text((capability.parameters as? ToggleCapabilityParameterObject)?.instance?.toggle?.code() ?: "Toggle", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = isOn,
            onCheckedChange = {
                isOn = it
                viewModel.updateDeviceCapability(device.id, CapabilityType.TOGGLE.code, isOn)
            }
        )
    }
}

@Composable
fun VideoStreamControl(device: DeviceObject, capability: DeviceCapabilityObject, viewModel: DeviceListViewModel) {

}

@Preview(
    name = "DefaultPreviewLight",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun DeviceDetailScreenPreview() {
    val fakeViewModel = object : DeviceListViewModel(YandexSmartHomeClient.getInstance("https://api.iot.yandex.net", "test_token")) {
        override val devices = MutableStateFlow(
            mutableListOf(
                DeviceObject(
                    id = "lamp-id-1",
                    name = "Smart Lamp",
                    aliases = mutableListOf(),
                    type = DeviceTypeWrapper(DeviceType.LIGHT.codifiedEnum()),
                    externalId = "",
                    skillId = "",
                    householdId = "",
                    room = "",
                    groups = mutableListOf(),
                    capabilities = mutableListOf(
                        DeviceCapabilityObject(
                            type = CapabilityTypeWrapper(CapabilityType.ON_OFF.codifiedEnum()),
                            reportable = true,
                            retrievable = true,
                            parameters = OnOffCapabilityParameterObject(split = false),
                            state = OnOffCapabilityStateObjectData(
                                instance = OnOffCapabilityStateObjectInstanceWrapper(
                                    OnOffCapabilityStateObjectInstance.ON.codifiedEnum()),
                                value = OnOffCapabilityStateObjectValue(true)
                            ),
                            lastUpdated = 0f
                        ),
                        DeviceCapabilityObject(
                            type = CapabilityTypeWrapper(CapabilityType.RANGE.codifiedEnum()),
                            reportable = true,
                            retrievable = true,
                            parameters = RangeCapabilityParameterObject(
                                instance = RangeCapabilityWrapper(RangeCapability.BRIGHTNESS.codifiedEnum()),
                                randomAccess = true
                            ),
                            state = RangeCapabilityStateObjectData(
                                instance = RangeCapabilityWrapper(RangeCapability.BRIGHTNESS.codifiedEnum()),
                                value = RangeCapabilityStateObjectDataValue(50.0f)
                            ),
                            lastUpdated = 0f
                        ),
                        DeviceCapabilityObject(
                            type = CapabilityTypeWrapper(CapabilityType.COLOR_SETTING.codifiedEnum()),
                            reportable = true,
                            retrievable = true,
                            parameters = ColorSettingCapabilityParameterObject(
                                temperatureK = TemperatureK(2700, 6000)
                            ),
                            state = ColorSettingCapabilityStateObjectData(
                                instance = ColorSettingCapabilityStateObjectInstanceWrapper(
                                    ColorSettingCapabilityStateObjectInstance.TEMPERATURE_K.codifiedEnum()),
                                value = ColorSettingCapabilityStateObjectValueRGB(4000)
                            ),
                            lastUpdated = 0f
                        ),
                    ),
                    properties = mutableListOf(
                        DevicePropertyObject(
                            type = PropertyTypeWrapper(PropertyType.FLOAT.codifiedEnum()),
                            reportable = true,
                            retrievable = true,
                            parameters = FloatPropertyParameterObject(
                                instance = PropertyFunctionWrapper(PropertyFunction.HUMIDITY.codifiedEnum()),
                                unit = MeasurementUnitWrapper(MeasurementUnit.PERCENT.codifiedEnum())
                            ),
                            state = FloatPropertyStateObjectData(
                                state = FloatPropertyState(
                                    propertyFunction = PropertyFunctionWrapper(PropertyFunction.HUMIDITY.codifiedEnum()),
                                    propertyValue = FloatObjectValue(55.0f)
                                )
                            ),
                            lastUpdated = 0f
                        )
                    )
                )
            )
        )
    }

    DeviceDetailScreen(deviceId = "lamp-id-1", viewModel = fakeViewModel, onDismiss = {}, isPreview = true)
}