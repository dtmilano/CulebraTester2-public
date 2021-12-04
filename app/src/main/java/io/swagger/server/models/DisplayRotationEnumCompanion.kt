package io.swagger.server.models

fun DisplayRotationEnum.Companion.of(value: Int): DisplayRotationEnum {
    return enumValues<DisplayRotationEnum>().first { it.value == value }
}