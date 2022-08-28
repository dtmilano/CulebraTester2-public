package com.dtmilano.android.culebratester2

import com.dtmilano.android.culebratester2.location.*
import dagger.Component
import io.ktor.locations.KtorExperimentalLocationsAPI
import javax.inject.Singleton

@KtorExperimentalLocationsAPI
@Singleton
@Component
interface ApplicationComponent {
    // Factory to create instances of the ApplicationComponent
    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        //fun create(@BindsInstance context: Context): ApplicationComponent
        fun create(): ApplicationComponent
    }

    fun holder(): HolderHolder
    fun objectStore(): ObjectStore
    fun inject(objectStore: com.dtmilano.android.culebratester2.location.ObjectStore)
    fun inject(list: com.dtmilano.android.culebratester2.location.ObjectStore.List)
    fun inject(startActivity: TargetContext.StartActivity)
    fun inject(dumpWindowHierarchy: UiDevice.DumpWindowHierarchy)
    fun inject(screenshot: UiDevice.Screenshot)
    fun inject(get: UiDevice.Click.Get)
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
    fun inject(get: UiObject2.SetText.Get)
    fun inject(post: UiObject2.SetText.Post)
    fun inject(longClick: UiObject2.LongClick)
    fun inject(get: UiDevice.FindObjects.Get)
    fun inject(getText: UiObject2.GetText)
    fun inject(pressRecentApps: UiDevice.PressRecentApps)
    fun inject(get: UiDevice.Swipe.Get)
    fun inject(post: UiDevice.Swipe.Post)
    fun inject(clear: com.dtmilano.android.culebratester2.location.ObjectStore.Clear)
    fun inject(remove: com.dtmilano.android.culebratester2.location.ObjectStore.Remove)
    fun inject(clear: UiObject2.Clear)
    fun inject(wait: UiDevice.Wait)
    fun inject(newWindow: Until.NewWindow)
    fun inject(clickAndWait: UiObject2.ClickAndWait)
    fun inject(clearLastTraversedText: UiDevice.ClearLastTraversedText)
    fun inject(drag: UiDevice.Drag)
    fun inject(freezeRotation: UiDevice.FreezeRotation)
    fun inject(get: UiDevice.HasObject.Get)
    fun inject(post: UiDevice.HasObject.Post)
    fun inject(waitForNewToast: Device.WaitForNewToast)
    fun inject(pixel: UiDevice.Pixel)
    fun inject(post: UiDevice.Click.Post)
    fun inject(unfreezeRotation: UiDevice.UnfreezeRotation)
    fun inject(isNaturalOrientation: UiDevice.IsNaturalOrientation)
    fun inject(isScreenOn: UiDevice.IsScreenOn)
    fun inject(query: Culebra.Help.Query)
    fun inject(dumpsys: Device.Dumpsys)
    fun inject(getContentDescription: UiObject2.GetContentDescription)
    fun inject(post: UiDevice.FindObjects.Post)
    fun inject(get: Until.FindObject.Get)
    fun inject(post: Until.FindObject.Post)
    fun inject(get: Until.FindObjects.Get)
    fun inject(post: Until.FindObjects.Post)
    fun inject(get: UiObject2.FindObject.Get)
    fun inject(post: UiObject2.FindObject.Post)
    fun inject(pressDPadCenter: UiDevice.PressDPadCenter)
    fun inject(pressDPadDown: UiDevice.PressDPadDown)
    fun inject(pressDPadLeft: UiDevice.PressDPadLeft)
    fun inject(pressDPadRight: UiDevice.PressDPadRight)
    fun inject(pressDPadUp: UiDevice.PressDPadUp)
    fun inject(get: Device.Locale.Get)
    fun inject(post: Device.Locale.Post)
//    fun inject(uiAutomatorHelper: UiAutomatorHelper)
}
