package com.dtmilano.android.culebratester2.location

import android.app.Notification
import android.app.UiAutomation
import android.graphics.Point
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import com.dtmilano.android.culebratester2.CulebraTesterApplication
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import com.dtmilano.android.culebratester2.ObjectStore
import io.ktor.locations.*
import io.swagger.server.models.Text
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * See https://github.com/ktorio/ktor/issues/1660 for the reason why we need the extra parameter
 * in nested classes:
 *
 * "One of the problematic features is nested location classes and nested location objects.
 *
 * What we are thinking of to change:
 *
 * a nested location class should always have a property of the outer class or object
 * nested objects in objects are not allowed
 * The motivation for the first point is the fact that a location class nested to another, makes no
 * sense without the ability to refer to the outer class."
 */
@KtorExperimentalLocationsAPI
@Location("/device")
class Device {
    @Location("/displayRealSize")
    class DisplayRealSize(private val parent: Device = Device()) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
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
    /*inner*/ class WaitForNewToast(
        private val timeout: Long,
        private val parent: Device = Device()
    ) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        private var lastToastMessage: String? = null

        private val runnable: Runnable = Runnable { /* do nothing */ }

        init {
            CulebraTesterApplication().appComponent.inject(this)
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