package com.dtmilano.android.culebratester2.location

import android.app.Notification
import android.app.UiAutomation
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.dtmilano.android.culebratester2.CulebraTesterApplication
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import com.dtmilano.android.culebratester2.ObjectStore
import com.dtmilano.android.culebratester2.R
import com.dtmilano.android.culebratester2.utils.LocaleUtils
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.swagger.server.models.Help
import io.swagger.server.models.Locale
import io.swagger.server.models.StatusResponse
import io.swagger.server.models.Text
import org.apache.commons.io.IOUtils
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
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

    @Location("/dumpsys")
    class Dumpsys(
        private val service: String,
        private val arg1: String? = null,
        private val arg2: String? = null,
        private val arg3: String? = null,
        private val parent: Device = Device()
    ) {

        fun response(): String {
            val command = mutableListOf("dumpsys", service)
            if (arg1 != null) {
                command.add(arg1)
            }
            if (arg2 != null) {
                command.add(arg2)
            }
            if (arg3 != null) {
                command.add(arg3)
            }
            println("Executing $command")
            val pb = ProcessBuilder(command)
            val p = pb.start()
            val stdOut = IOUtils.toString(p.inputStream, Charsets.UTF_8)
            val stdErr = IOUtils.toString(p.errorStream, Charsets.UTF_8)
            val exitStatus = p.waitFor(30, TimeUnit.SECONDS)
            println("dumpsys exit status: $exitStatus")
            return if (exitStatus) {
                stdOut
            } else {
                stdErr.ifEmpty {
                    "ERROR"
                }
            }
        }
    }

    @Location("/locale")
    /*inner*/ class Locale(private val parent: Device = Device()) {
        class Get(private val localeParent: Locale, val parent: Device = Device()) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }

            fun response(): io.swagger.server.models.Locale {
                val default = java.util.Locale.getDefault()
                return io.swagger.server.models.Locale(
                    default.language,
                    default.country,
                    default.variant
                )
            }
        }

        class Post(
            val locale: io.swagger.server.models.Locale? = null,
            val localeParent: Locale = Locale(),
            val parent: Device = Device()
        ) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }

            fun response(locale: io.swagger.server.models.Locale): StatusResponse {
                changeLocale(locale)
                return StatusResponse(StatusResponse.Status.OK)
            }

            private fun changeLocale(locale: io.swagger.server.models.Locale) {
                locale.country?.let {
                    locale.variant?.let {
                        return LocaleUtils.changeLocale(
                            java.util.Locale(
                                locale.language,
                                locale.country,
                                locale.variant
                            )
                        )
                    }
                    return LocaleUtils.changeLocale(
                        java.util.Locale(
                            locale.language,
                            locale.country
                        )
                    )
                }
                return LocaleUtils.changeLocale(java.util.Locale(locale.language))
            }
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

    companion object {
        fun help(apiComponents: List<String>, targetContext: WeakReference<Context>): Help {
            val res = when (apiComponents[0]) {
                "displayRealSize" -> R.string.help__device__display_real_size
                "waitForNewToast" -> R.string.help__device__wait_for_new_toast
                else -> -1
            }
            val helpMsg = targetContext.get()?.resources?.getString(res)
            return Help("This is Device help for $apiComponents: $helpMsg")
        }
    }
}