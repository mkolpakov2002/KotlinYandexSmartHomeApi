package ru.hse.miem.yandexsmarthomeapi.entity.common.extensions

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import pl.brightinventions.codified.Codified
import pl.brightinventions.codified.enums.CodifiedEnum
import pl.brightinventions.codified.enums.codifiedEnum

fun JsonArray.toMutableListOfStrings(): MutableList<String> {
    return this.map { it.jsonPrimitive.content }.toMutableList()
}

inline fun <reified T> String.codifiedEnumOrNull(): CodifiedEnum<T, String>? where T : Enum<T>, T : Codified<String> {
    return try {
        this.codifiedEnum<T>()
    } catch (e: IllegalArgumentException) {
        null
    }
}