package com.dtmilano.android.culebratester2

import android.content.Context
import android.view.WindowManager
import androidx.test.uiautomator.UiDevice
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds some important objects accessible from app and tests.
 */
object Holder {
    lateinit var targetContext: WeakReference<Context>
    lateinit var windowManager: WindowManager
    lateinit var cacheDir: File
    lateinit var uiDevice: UiDevice
}

// Didn't work, dagger was injecting different references even though this is annotated with
// singleton, as a workaround, HolderHolder was used
//@Singleton
//class Holder @Inject constructor() {
//    lateinit var targetContext: WeakReference<Context>
//    lateinit var windowManager: WindowManager
//    lateinit var cacheDir: File
//    lateinit var uiDevice: UiDevice
//}

@Singleton
class HolderHolder @Inject constructor() {
    val instance = Holder
}
