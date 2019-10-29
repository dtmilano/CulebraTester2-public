package com.dtmilano.android.culebratester2

import android.content.Context
import android.view.WindowManager
import androidx.test.uiautomator.UiDevice
import java.io.File
import java.lang.ref.WeakReference

/**
 * Holds some important objects accessible from app and tests.
 */
object Holder {
    lateinit var targetContext: WeakReference<Context>
    lateinit var windowManager: WindowManager
    lateinit var cacheDir: File
    lateinit var uiDevice: UiDevice
}