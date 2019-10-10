package com.dtmilano.android.culebratester2.model

data class Help(
    val text: String
)

data class DisplayRealSize(
    val device: String,
    val x: Int,
    val y: Int,
    val artWidth: Int?,
    val artHeight: Int?,
    val screenshotWidth: Int?,
    val screenshotX: Int?,
    val screenshotY: Int?
) {

    constructor(device: String, x: Int, y: Int) :
            this(device, x, y, null, null, null, null, null)
}

