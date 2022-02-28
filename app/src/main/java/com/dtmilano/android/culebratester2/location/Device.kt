package com.dtmilano.android.culebratester2.location

import android.app.Notification
import android.app.UiAutomation
import android.graphics.Point
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import com.dtmilano.android.culebratester2.DaggerApplicationComponent
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import com.dtmilano.android.culebratester2.ObjectStore
import io.ktor.locations.*
import io.swagger.server.models.Text
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@KtorExperimentalLocationsAPI
@Location("/device")
class Device {
    @Location("/displayRealSize")
    class DisplayRealSize {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            DaggerApplicationComponent.factory().create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): io.swagger.server.models.DisplayRealSize {
            val size = Point()
            holder.windowManager.defaultDisplay.getRealSize(size)
            return io.swagger.server.models.DisplayRealSize(
                Build.DEVICE ?: "UNKNOWN",
                size.x,
                size.y
            )
        }
    }

    @Location("/waitForNewToast")
    /*inner*/ class WaitForNewToast(private val timeout: Long) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

        private var lastToastMessage: String? = null

        private val runnable: Runnable = Runnable { /* do nothing */ }

        init {
            DaggerApplicationComponent.factory().create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): Text {
            if (waitForNewToast()) {
                return Text(text = lastToastMessage)
            }
            throw TimeoutException("No new Toast found after ${timeout}ms")
        }

        private fun waitForNewToast(): Boolean {
            val automation: UiAutomation = holder.uiAutomation
            println("😻 waiting for Toast...")
            return try {
                val event = automation.executeAndWaitForEvent(
                    runnable,
                    { e: AccessibilityEvent -> isToastShowing(e) },
                    timeout
                )
                event.recycle()
                true
            } catch (e: TimeoutException) {
                println("😻 timeout waiting for Toast.")
                false
            }
        }

        private fun isToastShowing(event: AccessibilityEvent): Boolean {
            if (event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                println("😻 accessibility event: $event")
                val parcelable = event.parcelableData
                if (parcelable !is Notification) { // if not a Notification then it's Toast
                    lastToastMessage = "" + event.text[0]
                    println("😻 Toast: $lastToastMessage")
                    return true
                }
            }
            return false
        }
    }
}