import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pl.brightinventions.codified.enums.codifiedEnum
import ru.hse.miem.yandexsmarthomeapi.TestConstants
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexApiResponse
import ru.hse.miem.yandexsmarthomeapi.entity.api.YandexManageDeviceCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceActionsObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.DeviceType
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.CapabilityType
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueInteger
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectInstance
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectInstanceWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.OnOffCapabilityStateObjectValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.toCapabilityObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.toJson
import ru.hse.miem.yandexsmarthomeapi.entity.common.toSmartHomeInfo
import ru.hse.miem.yandexsmarthomeapi.entity.common.toYandexManageGroupCapabilitiesStateRequest
import ru.hse.miem.yandexsmarthomeapi.domain.YandexSmartHomeClient
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityParameterObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueObjectHSV
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.ColorSettingCapabilityStateObjectValueObjectScene
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.HSVObject
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapability
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectData
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityStateObjectDataValue
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.RangeCapabilityWrapper
import ru.hse.miem.yandexsmarthomeapi.entity.common.capability.SceneObjectWrapper
import kotlin.time.Duration.Companion.seconds

/**
 * Класс для тестирования взаимодействия с API Яндекс Умного Дома.
 */
class YandexSmartHomeClientLampOnlineTest {

    private val client = YandexSmartHomeClient.getInstance(
        endpoint = "https://api.iot.yandex.net",
        bearerToken = TestConstants.BEARER_TOKEN
    )

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `test serialization`() = runTest(timeout = 30.seconds) {
        launch(Dispatchers.Unconfined) {
            // Получение полной информации об умном доме пользователя
            val userInfoResult = client.getUserInfo()
            assertTrue(
                userInfoResult !is YandexApiResponse.Error,
                "Не удалось получить информацию о пользователе."
            )

            val smartHomeInfo =
                (userInfoResult as YandexApiResponse.SuccessUserInfo).data.toSmartHomeInfo()

            val json = smartHomeInfo.toJson()
            val deserializedSmartHomeInfo = json.toSmartHomeInfo()
            assertEquals(smartHomeInfo, deserializedSmartHomeInfo)
        }
    }

    @Test
    fun `test individual lamp OnOff capabilities modifications`() = runTest(timeout = 30.seconds) {
        launch(Dispatchers.Unconfined) {
            // Получение полной информации об умном доме пользователя
            val userInfoResult = client.getUserInfo()
            assertTrue(userInfoResult is YandexApiResponse.SuccessUserInfo, "Не удалось получить информацию о пользователе.")

            val smartHomeInfo = (userInfoResult as YandexApiResponse.SuccessUserInfo).data.toSmartHomeInfo()

            // Индивидуальное управление лампами
            smartHomeInfo.devices.filter {
                it.type.type == DeviceType.LIGHT.codifiedEnum()
            }.forEach { device ->
                // Находим возможность управления включением/выключением
                val onOffCapability = device.capabilities.firstOrNull {
                    it.type.type == CapabilityType.ON_OFF.codifiedEnum()
                }

                if (onOffCapability != null) {
                    val onOffCapabilityStateObjectData = onOffCapability.state as? OnOffCapabilityStateObjectData

                    val currentState = onOffCapabilityStateObjectData?.value?.value

                    println("Id лампы: ${device.id}; Имя ${device.name}")

                    println("Текущее состояние лампы ${device.name}: ${if (currentState == true) "включено" else if (currentState == false) "выключено" else "неизвестно"}")

                    var newCurrentState = currentState != true

                    println("Меняю состояние лампы ${device.name} на: ${if (newCurrentState) "включено" else "выключено"}")

                    // Создаем новое состояние для включения/выключения лампы
                    val newOnOffState = onOffCapabilityStateObjectData
                        ?.copy(value = OnOffCapabilityStateObjectValue(newCurrentState))
                        ?: OnOffCapabilityStateObjectData(
                            instance = OnOffCapabilityStateObjectInstanceWrapper(
                                OnOffCapabilityStateObjectInstance.ON.codifiedEnum()),
                            value = OnOffCapabilityStateObjectValue(newCurrentState)
                        )

                    onOffCapability.state = newOnOffState

                    // Отправляем запрос на изменение состояния лампы
                    val manageDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(listOf(
                        DeviceActionsObject(
                            device.id, device.capabilities.map { it.toCapabilityObject() }.toMutableList()
                        )
                    ).map { it.toJson() })

                    val manageResult = client.manageDeviceCapabilitiesState(manageDeviceRequest)
                    assertTrue(manageResult is YandexApiResponse.SuccessManageDeviceCapabilitiesState,
                        "Не удалось изменить состояние лампы: ${device.name}")

                    delay(6.seconds)  // Задержка для проверки изменения состояния

                    // Возвращаем предыдущее состояние лампы
                    newCurrentState = !newCurrentState

                    println("Меняю состояние лампы ${device.name} на: ${if (newCurrentState) "включено" else "выключено"}")

                    val revertOnOffState = onOffCapabilityStateObjectData
                        ?.copy(value = OnOffCapabilityStateObjectValue(newCurrentState))
                        ?: OnOffCapabilityStateObjectData(
                            instance = OnOffCapabilityStateObjectInstanceWrapper(
                                OnOffCapabilityStateObjectInstance.ON.codifiedEnum()),
                            value = OnOffCapabilityStateObjectValue(newCurrentState)
                        )

                    onOffCapability.state = revertOnOffState

                    // Отправляем запрос на изменение состояния лампы
                    val revertDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(listOf(
                        DeviceActionsObject(
                            device.id, device.capabilities.map { it.toCapabilityObject() }.toMutableList()
                        )
                    ).map { it.toJson() })

                    val revertResult = client.manageDeviceCapabilitiesState(revertDeviceRequest)
                    assertTrue(revertResult is YandexApiResponse.SuccessManageDeviceCapabilitiesState,
                        "Не удалось изменить состояние лампы: ${device.name}")
                    delay(6.seconds)
                }
            }
        }
    }

    @Test
    fun `test individual lamp range modifications`() = runTest(timeout = 30.seconds) {
        launch(Dispatchers.Unconfined) {
            // Получение полной информации об умном доме пользователя
            val userInfoResult = client.getUserInfo()
            assertTrue(userInfoResult !is YandexApiResponse.Error, "Не удалось получить информацию о пользователе.")

            val smartHomeInfo = (userInfoResult as YandexApiResponse.SuccessUserInfo).data.toSmartHomeInfo()

            // Индивидуальное управление лампами с диапазоном
            smartHomeInfo.devices.filter {
                it.type.type == DeviceType.LIGHT.codifiedEnum()
            }.forEach { device ->
                // Находим возможность управления диапазоном
                val rangeCapability = device.capabilities.firstOrNull {
                    it.type.type == CapabilityType.RANGE.codifiedEnum()
                }

                if (rangeCapability != null) {
                    val rangeCapabilityStateObjectData = rangeCapability.state as? RangeCapabilityStateObjectData

                    val currentValue = rangeCapabilityStateObjectData?.value?.value

                    println("Id лампы: ${device.id}; Имя ${device.name}")

                    println("Текущее значение диапазона лампы ${device.name}: $currentValue")

                    val newCurrentValue = (currentValue ?: 0f) - 10f // Увеличиваем значение на 10

                    println("Меняю значение диапазона лампы ${device.name} на: $newCurrentValue")

                    // Создаем новое состояние для диапазона лампы
                    val newRangeState = rangeCapabilityStateObjectData
                        ?.copy(value = RangeCapabilityStateObjectDataValue(newCurrentValue))
                        ?: RangeCapabilityStateObjectData(
                            instance = RangeCapabilityWrapper(
                                RangeCapability.BRIGHTNESS.codifiedEnum()),
                            value = RangeCapabilityStateObjectDataValue(newCurrentValue)
                        )

                    rangeCapability.state = newRangeState

                    // Отправляем запрос на изменение состояния лампы
                    val manageDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(listOf(
                        DeviceActionsObject(
                            device.id, device.capabilities.map { it.toCapabilityObject() }.toMutableList()
                        )
                    ).map { it.toJson() })

                    val manageResult = client.manageDeviceCapabilitiesState(manageDeviceRequest)
                    assertTrue(manageResult !is YandexApiResponse.Error,
                        "Не удалось изменить состояние лампы: ${device.name}")

                    delay(6.seconds)  // Задержка для проверки изменения состояния

                    // Возвращаем предыдущее значение диапазона лампы
                    val revertValue = (currentValue ?: 0f) + 10f

                    println("Меняю значение диапазона лампы ${device.name} на: $revertValue")

                    val revertRangeState = rangeCapabilityStateObjectData
                        ?.copy(value = RangeCapabilityStateObjectDataValue(revertValue))
                        ?: RangeCapabilityStateObjectData(
                            instance = RangeCapabilityWrapper(
                                RangeCapability.BRIGHTNESS.codifiedEnum()),
                            value = RangeCapabilityStateObjectDataValue(revertValue)
                        )

                    rangeCapability.state = revertRangeState

                    // Отправляем запрос на изменение состояния лампы
                    val revertDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(listOf(
                        DeviceActionsObject(
                            device.id, device.capabilities.map { it.toCapabilityObject() }.toMutableList()
                        )
                    ).map { it.toJson() })

                    val revertResult = client.manageDeviceCapabilitiesState(revertDeviceRequest)
                    assertTrue(revertResult !is YandexApiResponse.Error,
                        "Не удалось изменить состояние лампы: ${device.name}")
                    delay(6.seconds)
                }
            }
        }
    }

    @Test
    fun `test individual lamp temperature modifications`() = runTest(timeout = 30.seconds) {
        launch(Dispatchers.Unconfined) {
            // Получение полной информации об умном доме пользователя
            val userInfoResult = client.getUserInfo()
            assertTrue(userInfoResult !is YandexApiResponse.Error, "Не удалось получить информацию о пользователе.")

            val smartHomeInfo = (userInfoResult as YandexApiResponse.SuccessUserInfo).data.toSmartHomeInfo()

            // Индивидуальное управление лампами с температурой
            smartHomeInfo.devices.filter {
                it.type.type == DeviceType.LIGHT.codifiedEnum()
            }.forEach { device ->
                // Находим возможность управления цветовой температурой
                val colorSettingCapability = device.capabilities.firstOrNull {
                    it.type.type == CapabilityType.COLOR_SETTING.codifiedEnum()
                }

                val colorSettingParams = colorSettingCapability?.parameters as? ColorSettingCapabilityParameterObject
                val temperatureKSupported = colorSettingParams?.temperatureK != null

                if (colorSettingCapability != null && temperatureKSupported) {
                    val colorSettingStateObjectData = colorSettingCapability.state as? ColorSettingCapabilityStateObjectData
                    val currentTemperature = (colorSettingStateObjectData?.value as? ColorSettingCapabilityStateObjectValueInteger)?.value

                    println("Id лампы: ${device.id}; Имя ${device.name}")
                    println("Текущее значение цветовой температуры лампы ${device.name}: $currentTemperature")

                    val newTemperature = (currentTemperature ?: colorSettingParams?.temperatureK!!.min) + 100
                    println("Меняю значение цветовой температуры лампы ${device.name} на: $newTemperature")

                    // Создаем новое состояние для цветовой температуры лампы
                    val newColorSettingState = colorSettingStateObjectData
                        ?.copy(value = ColorSettingCapabilityStateObjectValueInteger(newTemperature))
                        ?: ColorSettingCapabilityStateObjectData(
                            instance = ColorSettingCapabilityStateObjectInstanceWrapper(
                                ColorSettingCapabilityStateObjectInstance.TEMPERATURE_K.codifiedEnum()),
                            value = ColorSettingCapabilityStateObjectValueInteger(newTemperature)
                        )

                    colorSettingCapability.state = newColorSettingState

                    // Отправляем запрос на изменение состояния лампы
                    val manageDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(listOf(
                        DeviceActionsObject(
                            device.id, device.capabilities.map { it.toCapabilityObject() }.toMutableList()
                        )
                    ).map { it.toJson() })

                    val manageResult = client.manageDeviceCapabilitiesState(manageDeviceRequest)
                    assertTrue(manageResult !is YandexApiResponse.Error,
                        "Не удалось изменить состояние лампы: ${device.name}")

                    delay(6.seconds)  // Задержка для проверки изменения состояния

                    // Возвращаем предыдущее значение цветовой температуры лампы
                    val revertTemperature = currentTemperature ?: colorSettingParams?.temperatureK!!.min
                    println("Возвращаю значение цветовой температуры лампы ${device.name} на: $revertTemperature")

                    val revertColorSettingState = colorSettingStateObjectData
                        ?.copy(value = ColorSettingCapabilityStateObjectValueInteger(revertTemperature))
                        ?: ColorSettingCapabilityStateObjectData(
                            instance = ColorSettingCapabilityStateObjectInstanceWrapper(
                                ColorSettingCapabilityStateObjectInstance.TEMPERATURE_K.codifiedEnum()),
                            value = ColorSettingCapabilityStateObjectValueInteger(revertTemperature)
                        )

                    colorSettingCapability.state = revertColorSettingState

                    // Отправляем запрос на изменение состояния лампы
                    val revertDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(listOf(
                        DeviceActionsObject(
                            device.id, device.capabilities.map { it.toCapabilityObject() }.toMutableList()
                        )
                    ).map { it.toJson() })

                    val revertResult = client.manageDeviceCapabilitiesState(revertDeviceRequest)
                    assertTrue(revertResult !is YandexApiResponse.Error,
                        "Не удалось изменить состояние лампы: ${device.name}")
                    delay(6.seconds)
                }
            }
        }
    }

    @Test
    fun `test group lamp capabilities modifications`() = runTest(timeout = 30.seconds) {
        launch(Dispatchers.Unconfined) {
            // Получение полной информации об умном доме пользователя
            val userInfoResult = client.getUserInfo()
            assertTrue(userInfoResult is YandexApiResponse.SuccessUserInfo, "Не удалось получить информацию о пользователе.")

            val smartHomeInfo = (userInfoResult as YandexApiResponse.SuccessUserInfo).data.toSmartHomeInfo()

            // Групповое управление
            val groupWithLamps = smartHomeInfo.groups.find { group ->
                group.devices.any { deviceId ->
                    smartHomeInfo.devices.any { device -> device.id == deviceId && device.type.type == DeviceType.LIGHT.codifiedEnum() }
                }
            }

            if (groupWithLamps != null) {
                println("Выполнение группового управления над лампами в группе: ${groupWithLamps.name}, id: ${groupWithLamps.id}")

                val oldState = groupWithLamps.capabilities.map { it.state }.toMutableList()

                // Меняем состояния на основе текущих значений
                val modifiedGroupActions = groupWithLamps.capabilities.map { capability ->
                    when (capability.state) {
                        is OnOffCapabilityStateObjectData -> {
                            val currentState = (capability.state as OnOffCapabilityStateObjectData).value.value
                            if (currentState) {
                                capability.state
                            } else {
                                (capability.state as OnOffCapabilityStateObjectData).copy(
                                    value = OnOffCapabilityStateObjectValue(true)
                                )
                            }
                        }
                        is ColorSettingCapabilityStateObjectData -> {
                            val colorSettingParams = capability.parameters as? ColorSettingCapabilityParameterObject
                            if (colorSettingParams?.temperatureK != null) {
                                val minTemp = colorSettingParams.temperatureK!!.min
                                val maxTemp = colorSettingParams.temperatureK!!.max
                                val newTemp = (minTemp + maxTemp) / 2
                                (capability.state as ColorSettingCapabilityStateObjectData).copy(
                                    instance = ColorSettingCapabilityStateObjectInstanceWrapper(
                                        ColorSettingCapabilityStateObjectInstance.TEMPERATURE_K.codifiedEnum()),
                                    value = ColorSettingCapabilityStateObjectValueInteger(newTemp)
                                )
                            } else {
                                capability.state
                            }
                        }
                        else -> capability.state
                    }
                }

                println("Старые состояния: $oldState")
                println("Новые состояния: $modifiedGroupActions")

                groupWithLamps.capabilities.forEachIndexed { index, capability ->
                    groupWithLamps.capabilities[index] = capability.copy(state = modifiedGroupActions[index])
                }

                // Отправляем запрос на изменение состояния группы устройств
                val manageGroupRequest = groupWithLamps.capabilities.toYandexManageGroupCapabilitiesStateRequest()

                println("Групповое управление лампами: ${Json.encodeToJsonElement(manageGroupRequest)}")

                val manageGroupResult = client.manageGroupCapabilitiesState(groupWithLamps.id, manageGroupRequest)
                assertTrue(manageGroupResult is YandexApiResponse.SuccessManageGroupCapabilitiesState,
                    "Не удалось выполнить групповое управление лампами.")

                delay(6.seconds)

                // Возвращаем предыдущее состояние ламп
                groupWithLamps.capabilities.forEachIndexed { index, capability ->
                    if (capability.state is OnOffCapabilityStateObjectData) {
                        val currentState = (capability.state as OnOffCapabilityStateObjectData).value.value
                        val originalState = (oldState[index] as OnOffCapabilityStateObjectData).value.value
                        if (!originalState && currentState) {
                            groupWithLamps.capabilities[index] = capability.copy(state = OnOffCapabilityStateObjectData(
                                instance = OnOffCapabilityStateObjectInstanceWrapper(
                                    OnOffCapabilityStateObjectInstance.ON.codifiedEnum()
                                ),
                                value = OnOffCapabilityStateObjectValue(false)
                            ))
                        }
                    } else {
                        groupWithLamps.capabilities[index] = capability.copy(state = oldState[index])
                    }
                }

                // Отправляем запрос на изменение состояния группы устройств
                val revertGroupRequest = groupWithLamps.capabilities.toYandexManageGroupCapabilitiesStateRequest()

                println("Групповое управление лампами: ${Json.encodeToJsonElement(revertGroupRequest)}")

                val revertGroupResult = client.manageGroupCapabilitiesState(groupWithLamps.id, revertGroupRequest)
                assertTrue(revertGroupResult is YandexApiResponse.SuccessManageGroupCapabilitiesState,
                    "Не удалось выполнить групповое управление лампами.")
            } else {
                println("Группа с лампами не найдена.")
            }
        }
    }

    @Test
    fun `test individual lamp color scenes modifications`() = runTest(timeout = 30.seconds) {
        launch(Dispatchers.Unconfined) {
            // Получение полной информации об умном доме пользователя
            val userInfoResult = client.getUserInfo()
            assertTrue(userInfoResult !is YandexApiResponse.Error, "Не удалось получить информацию о пользователе.")

            val smartHomeInfo = (userInfoResult as YandexApiResponse.SuccessUserInfo).data.toSmartHomeInfo()

            // Индивидуальное управление лампами с цветовыми сценами
            smartHomeInfo.devices.filter {
                it.type.type == DeviceType.LIGHT.codifiedEnum()
            }.forEach { device ->
                // Находим возможность управления цветовыми сценами
                val colorSettingCapability = device.capabilities.firstOrNull {
                    it.type.type == CapabilityType.COLOR_SETTING.codifiedEnum()
                }

                val colorSettingParams = colorSettingCapability?.parameters as? ColorSettingCapabilityParameterObject
                val colorScenesSupported = colorSettingParams?.colorScene?.scenes ?: emptyList()

                if (colorSettingCapability != null && colorScenesSupported.isNotEmpty()) {
                    val colorSettingStateObjectData = colorSettingCapability.state as? ColorSettingCapabilityStateObjectData
                    val currentScene = (colorSettingStateObjectData?.value as? ColorSettingCapabilityStateObjectValueObjectScene)?.value?.scene?.code()

                    println("Id лампы: ${device.id}; Имя ${device.name}")
                    println("Текущая цветовая сцена лампы ${device.name}: $currentScene")

                    // Переключаемся на три разные цветовые сцены
                    val scenesToTest = colorScenesSupported.take(3) // Берем первые три доступные сцены
                    scenesToTest.forEach { scene ->
                        println("Меняю цветовую сцену лампы ${device.name} на: ${scene.id.scene.code()}")

                        // Создаем новое состояние для цветовой сцены лампы
                        val newColorSettingState = colorSettingStateObjectData?.copy(
                            instance = ColorSettingCapabilityStateObjectInstanceWrapper(
                                ColorSettingCapabilityStateObjectInstance.SCENE.codifiedEnum()),
                            value = ColorSettingCapabilityStateObjectValueObjectScene(
                                SceneObjectWrapper(scene.id.scene)
                            )
                        ) ?: ColorSettingCapabilityStateObjectData(
                            instance = ColorSettingCapabilityStateObjectInstanceWrapper(
                                ColorSettingCapabilityStateObjectInstance.SCENE.codifiedEnum()),
                            value = ColorSettingCapabilityStateObjectValueObjectScene(
                                SceneObjectWrapper(scene.id.scene)
                            )
                        )

                        colorSettingCapability.state = newColorSettingState

                        // Отправляем запрос на изменение состояния лампы
                        val manageDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(listOf(
                            DeviceActionsObject(
                                device.id, device.capabilities.map { it.toCapabilityObject() }.toMutableList()
                            )
                        ).map { it.toJson() })

                        val manageResult = client.manageDeviceCapabilitiesState(manageDeviceRequest)
                        assertTrue(manageResult !is YandexApiResponse.Error,
                            "Не удалось изменить цветовую сцену лампы: ${device.name}")

                        delay(6.seconds)  // Задержка для проверки изменения состояния
                    }

                    // Возвращаем предыдущее значение цветовой сцены лампы
                    val revertScene = colorScenesSupported.find { it.id.scene.code() == currentScene }
                    if (revertScene != null) {
                        println("Возвращаю цветовую сцену лампы ${device.name} на: ${revertScene.id.scene.code()}")

                        val revertColorSettingState = colorSettingStateObjectData
                            ?.copy(value = ColorSettingCapabilityStateObjectValueObjectScene(SceneObjectWrapper(revertScene.id.scene)))
                            ?: ColorSettingCapabilityStateObjectData(
                                instance = ColorSettingCapabilityStateObjectInstanceWrapper(
                                    ColorSettingCapabilityStateObjectInstance.SCENE.codifiedEnum()),
                                value = ColorSettingCapabilityStateObjectValueObjectScene(SceneObjectWrapper(revertScene.id.scene))
                            )

                        colorSettingCapability.state = revertColorSettingState

                        // Отправляем запрос на изменение состояния лампы
                        val revertDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(listOf(
                            DeviceActionsObject(
                                device.id, device.capabilities.map { it.toCapabilityObject() }.toMutableList()
                            )
                        ).map { it.toJson() })

                        val revertResult = client.manageDeviceCapabilitiesState(revertDeviceRequest)
                        assertTrue(revertResult !is YandexApiResponse.Error,
                            "Не удалось вернуть цветовую сцену лампы: ${device.name}")
                        delay(6.seconds)
                    }
                }
            }
        }
    }

    @Test
    fun `test individual lamp HSV color modifications`() = runTest(timeout = 30.seconds) {
        launch(Dispatchers.Unconfined) {
            // Получение полной информации об умном доме пользователя
            val userInfoResult = client.getUserInfo()
            assertTrue(userInfoResult !is YandexApiResponse.Error, "Не удалось получить информацию о пользователе.")

            val smartHomeInfo = (userInfoResult as YandexApiResponse.SuccessUserInfo).data.toSmartHomeInfo()

            // Индивидуальное управление лампами с цветом HSV
            smartHomeInfo.devices.filter {
                it.type.type == DeviceType.LIGHT.codifiedEnum()
            }.forEach { device ->
                // Находим возможность управления цветом HSV
                val colorSettingCapability = device.capabilities.firstOrNull {
                    it.type.type == CapabilityType.COLOR_SETTING.codifiedEnum()
                }

                val colorSettingParams = colorSettingCapability?.parameters as? ColorSettingCapabilityParameterObject

                if (colorSettingCapability != null) {
                    val colorSettingStateObjectData = colorSettingCapability.state as? ColorSettingCapabilityStateObjectData
                    val currentHSV = (colorSettingStateObjectData?.value as? ColorSettingCapabilityStateObjectValueObjectHSV)?.value

                    println("Id лампы: ${device.id}; Имя ${device.name}")
                    println("Текущий цвет HSV лампы ${device.name}: $currentHSV")

                    val newHSV = HSVObject(120, 50, 100) // Пример нового значения HSV

                    println("Меняю цвет HSV лампы ${device.name} на: $newHSV")

                    // Создаем новое состояние для цвета HSV лампы
                    val newColorSettingState = colorSettingStateObjectData
                        ?.copy(instance = ColorSettingCapabilityStateObjectInstanceWrapper(
                            ColorSettingCapabilityStateObjectInstance.HSV.codifiedEnum()),
                            value = ColorSettingCapabilityStateObjectValueObjectHSV(newHSV))
                        ?: ColorSettingCapabilityStateObjectData(
                            instance = ColorSettingCapabilityStateObjectInstanceWrapper(
                                ColorSettingCapabilityStateObjectInstance.HSV.codifiedEnum()),
                            value = ColorSettingCapabilityStateObjectValueObjectHSV(newHSV)
                        )

                    colorSettingCapability.state = newColorSettingState

                    // Отправляем запрос на изменение состояния лампы
                    val manageDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(listOf(
                        DeviceActionsObject(
                            device.id, device.capabilities.map { it.toCapabilityObject() }.toMutableList()
                        )
                    ).map { it.toJson() })

                    val manageResult = client.manageDeviceCapabilitiesState(manageDeviceRequest)
                    assertTrue(manageResult !is YandexApiResponse.Error,
                        "Не удалось изменить цвет HSV лампы: ${device.name}")

                    delay(6.seconds)  // Задержка для проверки изменения состояния

                    println("Возвращаю лампу ${device.name} к предыдущему состоянию")

                    val revertColorSettingState = colorSettingStateObjectData?.copy()

                    colorSettingCapability.state = revertColorSettingState

                    // Отправляем запрос на изменение состояния лампы
                    val revertDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(listOf(
                        DeviceActionsObject(
                            device.id, device.capabilities.map { it.toCapabilityObject() }.toMutableList()
                        )
                    ).map { it.toJson() })

                    val revertResult = client.manageDeviceCapabilitiesState(revertDeviceRequest)
                    assertTrue(revertResult !is YandexApiResponse.Error,
                        "Не удалось вернуть цвет HSV лампы: ${device.name}")
                    delay(6.seconds)
                }
            }
        }
    }
}