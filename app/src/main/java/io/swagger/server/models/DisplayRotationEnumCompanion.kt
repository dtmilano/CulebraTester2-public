package io.swagger.server.models

fun DisplayRotationEnum.Companion.of(value: Int): DisplayRotationEnum {
    try {
        return enumValues<DisplayRotationEnum>().first { it.value == value }
    } catch (e: NoSuchElementException) {
        val msg = "ð Cannot find the DisplayRotationEnum for value=${value}"
        println(msg)
        throw IllegalArgumentException(msg, e)
    }
}