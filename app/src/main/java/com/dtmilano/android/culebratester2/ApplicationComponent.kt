package com.dtmilano.android.culebratester2

import com.dtmilano.android.culebratester2.location.Device
import com.dtmilano.android.culebratester2.location.TargetContext
import com.dtmilano.android.culebratester2.location.UiDevice
import com.dtmilano.android.culebratester2.location.UiObject2
import dagger.Component
import io.ktor.locations.KtorExperimentalLocationsAPI
import javax.inject.Singleton

@KtorExperimentalLocationsAPI
@Singleton
@Component
interface ApplicationComponent {
    fun holder(): HolderHolder
    fun objectStore(): ObjectStore
    fun inject(objectStore: com.dtmilano.android.culebratester2.location.ObjectStore)
    fun inject(list: com.dtmilano.android.culebratester2.location.ObjectStore.List)
    fun inject(startActivity: TargetContext.StartActivity)
    fun inject(dumpWindowHierarchy: UiDevice.DumpWindowHierarchy)
    fun inject(screenshot: UiDevice.Screenshot)
    fun inject(click: UiDevice.Click)
    fun inject(currentPackageName: UiDevice.CurrentPackageName)
    fun inject(displayHeight: UiDevice.DisplayHeight)
    fun inject(displayRotation: UiDevice.DisplayRotation)
    fun inject(displaySizeDp: UiDevice.DisplaySizeDp)
    fun inject(displayWidth: UiDevice.DisplayWidth)
    fun inject(findObject: UiDevice.FindObject)
    fun inject(get: UiDevice.FindObject.Get)
    fun inject(post: UiDevice.FindObject.Post)
    fun inject(lastTraversedText: UiDevice.LastTraversedText)
    fun inject(pressBack: UiDevice.PressBack)
    fun inject(pressDelete: UiDevice.PressDelete)
    fun inject(pressEnter: UiDevice.PressEnter)
    fun inject(pressHome: UiDevice.PressHome)
    fun inject(pressKeyCode: UiDevice.PressKeyCode)
    fun inject(productName: UiDevice.ProductName)
    fun inject(waitForIdle: UiDevice.WaitForIdle)
    fun inject(waitForWindowUpdate: UiDevice.WaitForWindowUpdate)
    fun inject(displayRealSize: Device.DisplayRealSize)
    fun inject(click: UiObject2.Click)
    fun inject(dump: UiObject2.Dump)
    fun inject(setText: UiObject2.SetText)
    fun inject(longClick: UiObject2.LongClick)
    fun inject(get: UiDevice.FindObjects.Get)
    fun inject(getText: UiObject2.GetText)
    fun inject(pressRecentApps: UiDevice.PressRecentApps)
    fun inject(get: UiDevice.Swipe.Get)
    fun inject(post: UiDevice.Swipe.Post)
//    fun inject(uiAutomatorHelper: UiAutomatorHelper)
}