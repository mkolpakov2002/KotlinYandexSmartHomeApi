# Yandex Smart Home API Client

Этот проект представляет собой Android-приложение, написанное на Kotlin, которое взаимодействует с API Яндекс Умного Дома. Приложение позволяет управлять устройствами умного дома, получать информацию о состоянии устройств и группировать устройства для управления их состояниями. Только через Unit тесты, без Ui.

## Функционал

- Получение информации о умном доме пользователя
- Управление состоянием отдельных устройств
- Управление состоянием групп устройств
- Просмотр и изменение состояния различных типов устройств и их возможностей

## Стек технологий

- **Kotlin** - основной язык программирования
- **Kotlinx Serialization** - библиотека для сериализации и десериализации JSON
- **Ktor** - HTTP клиент для взаимодействия с API
- **JUnit 5** - фреймворк для модульного тестирования
- **MockK** - библиотека для создания моков в тестах

## Установка и настройка

1. Клонируйте репозиторий.

2. Откройте проект в Android Studio.

3. Добавьте свой токен API Яндекс Умного Дома в класс `TestConstants`:

```kotlin
val bearerToken = "YOUR_BEARER_TOKEN"
```

5. Постройте проект.

6. Запустите Unit тесты.

## Примеры использования

### Получение информации о умном доме

```kotlin
val userInfoResult = client.getUserInfo()
if (userInfoResult is YandexApiResponse.SuccessUserInfo) {
    val smartHomeInfo = userInfoResult.data.toSmartHomeInfo()
    // Обработка данных умного дома
}
```

### Управление состоянием устройства

```kotlin
val manageDeviceRequest = YandexManageDeviceCapabilitiesStateRequest(
    devices = listOf(
        DeviceActionsObject(
            id = "lamp-id-1",
            actions = listOf(
                CapabilityObject(
                    type = CapabilityTypeWrapper(CapabilityType.ON_OFF.codifiedEnum()),
                    state = OnOffCapabilityStateObjectData(
                        instance = OnOffCapabilityStateObjectInstanceWrapper(
                            OnOffCapabilityStateObjectInstance.ON.codifiedEnum()
                        ),
                        value = OnOffCapabilityStateObjectValue(true)
                    )
                )
            )
        ).toJson()
    )
)
val manageResult = client.manageDeviceCapabilitiesState(manageDeviceRequest)
if (manageResult is YandexApiResponse.SuccessManageDeviceCapabilitiesState) {
    // Обработка успешного изменения состояния устройства
}
```

### Управление состоянием группы устройств

```kotlin
val manageGroupRequest = YandexManageGroupCapabilitiesStateRequest(
    actions = listOf(
        GroupCapabilityObject(
            type = CapabilityTypeWrapper(CapabilityType.ON_OFF.codifiedEnum()),
            retrievable = true,
            parameters = OnOffCapabilityParameterObject(split = false),
            state = OnOffCapabilityStateObjectData(
                instance = OnOffCapabilityStateObjectInstanceWrapper(OnOffCapabilityStateObjectInstance.ON.codifiedEnum()),
                value = OnOffCapabilityStateObjectValue(true)
            )
        ).toJson()
    )
)
val manageGroupResult = client.manageGroupCapabilitiesState("group-id-1", manageGroupRequest)
if (manageGroupResult is YandexApiResponse.SuccessManageGroupCapabilitiesState) {
    // Обработка успешного изменения состояния группы устройств
}
```

## Тестирование

Проект использует JUnit 5 и MockK для модульного тестирования. Чтобы запустить тесты, используйте следующую команду:

```bash
./gradlew test
```

Для тестов может использоваться умный дом с одной или несколькими лампами. См. `YandexSmartHomeClientOnlineTest`.

## Текущие ошибки

Возникают ошибки при тестировании группового управления лампами.

## Вклад

Если вы хотите внести свой вклад в проект, пожалуйста, создайте pull request или откройте issue.

---

**Внимание**: Этот проект требует действительного токена API Яндекс Умного Дома для работы. Убедитесь, что у вас есть доступ к API перед использованием.
