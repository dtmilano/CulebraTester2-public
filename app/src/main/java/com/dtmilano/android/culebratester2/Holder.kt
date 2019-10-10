package com.dtmilano.android.culebratester2

import android.view.WindowManager
import androidx.test.uiautomator.UiDevice
import java.io.File

/**
 * Holds some important objects accessible from app and tests.
 */
object Holder {
    lateinit var windowManager: WindowManager
    lateinit var cacheDir: File
    lateinit var uiDevice: UiDevice
}