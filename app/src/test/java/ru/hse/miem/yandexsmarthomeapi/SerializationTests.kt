package ru.hse.miem.yandexsmarthomeapi

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.hse.miem.yandexsmarthomeapi.entity.api.*

class SerializationTests {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testSkillsDescriptionSocketSerialization() {
        val deserialized = json.decodeFromString<YandexUserInfoResponse>(TestConstants.SKILLS_DESCRIPTION_SOCKET)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.SKILLS_DESCRIPTION_SOCKET), json.parseToJsonElement(serialized))
    }

    @Test
    fun testSkillsDescriptionLampSerialization() {
        val deserialized = json.decodeFromString<YandexUserInfoResponse>(TestConstants.SKILLS_DESCRIPTION_LAMP)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.SKILLS_DESCRIPTION_LAMP), json.parseToJsonElement(serialized))
    }

    @Test
    fun testSkillsDescriptionCameraSerialization() {
        val deserialized = json.decodeFromString<YandexUserInfoResponse>(TestConstants.SKILLS_DESCRIPTION_CAMERA)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.SKILLS_DESCRIPTION_CAMERA), json.parseToJsonElement(serialized))
    }

    @Test
    fun testSkillsDescriptionACSerialization() {
        val deserialized = json.decodeFromString<YandexUserInfoResponse>(TestConstants.SKILLS_DESCRIPTION_AC)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.SKILLS_DESCRIPTION_AC), json.parseToJsonElement(serialized))
    }

    @Test
    fun testSkillsDescriptionLampsSerialization() {
        val deserialized = json.decodeFromString<YandexUserInfoResponse>(TestConstants.SKILLS_DESCRIPTION_LAMPS)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.SKILLS_DESCRIPTION_LAMPS), json.parseToJsonElement(serialized))
    }

    @Test
    fun testSkillsDescriptionHumidifierSerialization() {
        val deserialized = json.decodeFromString<YandexUserInfoResponse>(TestConstants.SKILLS_DESCRIPTION_HUMIDIFIER)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.SKILLS_DESCRIPTION_HUMIDIFIER), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionResponseSocketSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateResponse>(TestConstants.DEVICE_ACTION_RESPONSE_SOCKET)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_RESPONSE_SOCKET), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionResponseLampsSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateResponse>(TestConstants.DEVICE_ACTION_RESPONSE_LAMPS)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_RESPONSE_LAMPS), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionResponseCameraSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateResponse>(TestConstants.DEVICE_ACTION_RESPONSE_CAMERA)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_RESPONSE_CAMERA), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionResponseACSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateResponse>(TestConstants.DEVICE_ACTION_RESPONSE_AC)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_RESPONSE_AC), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionResponseBrightnessSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateResponse>(TestConstants.DEVICE_ACTION_RESPONSE_BRIGHTNESS)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_RESPONSE_BRIGHTNESS), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionResponseHumidifierSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateResponse>(TestConstants.DEVICE_ACTION_RESPONSE_HUMIDIFIER)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_RESPONSE_HUMIDIFIER), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceQueryResponseLampsSerialization() {
        val deserialized = json.decodeFromString<YandexDeviceStateResponse>(TestConstants.DEVICE_QUERY_RESPONSE_LAMPS)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_QUERY_RESPONSE_LAMPS), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceQueryResponseCameraSerialization() {
        val deserialized = json.decodeFromString<YandexDeviceStateResponse>(TestConstants.DEVICE_QUERY_RESPONSE_CAMERA)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_QUERY_RESPONSE_CAMERA), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceQueryResponseACSerialization() {
        val deserialized = json.decodeFromString<YandexDeviceStateResponse>(TestConstants.DEVICE_QUERY_RESPONSE_AC)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_QUERY_RESPONSE_AC), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceQueryResponseBrightnessSerialization() {
        val deserialized = json.decodeFromString<YandexDeviceStateResponse>(TestConstants.DEVICE_QUERY_RESPONSE_BRIGHTNESS)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_QUERY_RESPONSE_BRIGHTNESS), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceQueryResponseHumidifierSerialization() {
        val deserialized = json.decodeFromString<YandexDeviceStateResponse>(TestConstants.DEVICE_QUERY_RESPONSE_HUMIDIFIER)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_QUERY_RESPONSE_HUMIDIFIER), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionRequestSocketSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateRequest>(TestConstants.DEVICE_ACTION_REQUEST_SOCKET)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_REQUEST_SOCKET), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionRequestLampsSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateRequest>(TestConstants.DEVICE_ACTION_REQUEST_LAMPS)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_REQUEST_LAMPS), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionRequestCameraSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateRequest>(TestConstants.DEVICE_ACTION_REQUEST_CAMERA)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_REQUEST_CAMERA), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionRequestACSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateRequest>(TestConstants.DEVICE_ACTION_REQUEST_AC)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_REQUEST_AC), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionRequestBrightnessSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateRequest>(TestConstants.DEVICE_ACTION_REQUEST_BRIGHTNESS)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_REQUEST_BRIGHTNESS), json.parseToJsonElement(serialized))
    }

    @Test
    fun testDeviceActionRequestHumidifierSerialization() {
        val deserialized = json.decodeFromString<YandexManageDeviceCapabilitiesStateRequest>(TestConstants.DEVICE_ACTION_REQUEST_HUMIDIFIER)
        val serialized = json.encodeToString(deserialized)
        assertEquals(json.parseToJsonElement(TestConstants.DEVICE_ACTION_REQUEST_HUMIDIFIER), json.parseToJsonElement(serialized))
    }
}