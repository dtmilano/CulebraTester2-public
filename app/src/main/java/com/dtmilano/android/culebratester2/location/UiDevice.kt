package com.dtmilano.android.culebratester2.location

import android.util.Log
import androidx.test.uiautomator.By
import com.dtmilano.android.culebratester2.CulebraTesterApplication
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import com.dtmilano.android.culebratester2.convertWindowHierarchyDumpToJson
import com.dtmilano.android.culebratester2.utils.bySelectorBundleFromString
import com.dtmilano.android.culebratester2.utils.uiSelectorBundleFromString
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.swagger.experimental.HttpException
import io.swagger.server.models.DisplayRotationEnum
import io.swagger.server.models.ObjectRef
import io.swagger.server.models.Selector
import io.swagger.server.models.StatusResponse
import io.swagger.server.models.SwipeBody
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

private const val TAG = "UiDevice"
private const val REMOVE_TEMP_FILE_DELAY = 2000L

@KtorExperimentalLocationsAPI
@Location("/uiDevice")
class UiDevice {


    @Location("/dumpWindowHierarchy")
    /*inner*/ class DumpWindowHierarchy(private val format: String = "JSON") {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): String {
            val output = ByteArrayOutputStream()
            holder.uiDevice.dumpWindowHierarchy(output)
            return when (format.uppercase(Locale.ROOT)) {
                "JSON" -> convertWindowHierarchyDumpToJson(output.toString())
                "XML" -> output.toString()
                else -> throw HttpException(
                    HttpStatusCode.UnprocessableEntity,
                    "Unsupported format $format"
                )
            }
        }
    }

    @Location("/screenshot")
    /*inner*/ class Screenshot(private val scale: Float = 1.0F, private val quality: Int = 90) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        /**
         * Returns a screenshot as a [File] response.
         */
        fun response(): File {
            //Log.d(TAG, "getting screenshot")
            val tempFile = createTempFile()
            if (holder.uiDevice.takeScreenshot(tempFile, scale, quality)) {
                //Log.d("UiDevice", "returning screenshot file: " + tempFile.absolutePath)
                GlobalScope.launch { delay(REMOVE_TEMP_FILE_DELAY); removeTempFile(tempFile) }
                return tempFile
            }
            throw RuntimeException("Cannot get screenshot")
        }

        /**
         * Removes a temporary file.
         */
        private fun removeTempFile(tempFile: File) {
            if (!tempFile.delete()) {
                Log.w(TAG, "Temporary file ${tempFile.absolutePath} couldn't be deleted.")
            }
        }

        /**
         * Creates a temporary file to hold the screenshot.
         */
        private fun createTempFile(): File {
            val tempDir = holder.cacheDir
            return File.createTempFile("screenshot", "png", tempDir)
        }
    }

    @Location("/click")
    /*inner*/ class Click(private val x: Int, private val y: Int) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            //Log.d("UiDevice", "clicking on ($x,$y)")
            if (holder.uiDevice.click(x, y)) {
                return StatusResponse.OK
            }
            return StatusResponse(StatusResponse.Status.ERROR, errorMessage = "Cannot click")
        }
    }

    /**
     * Gets the current package name
     * Gets the current package name
     */
    @Location("/currentPackageName")
    /*inner*/ class CurrentPackageName {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): io.swagger.server.models.CurrentPackageName {
            return io.swagger.server.models.CurrentPackageName(holder.uiDevice.currentPackageName)
        }
    }

    /**
     * Gets the display height
     * Gets the display height
     */
    @Location("/displayHeight")
    /*inner*/ class DisplayHeight {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }


        fun response(): io.swagger.server.models.DisplayHeight {
            return io.swagger.server.models.DisplayHeight(holder.uiDevice.displayHeight)
        }
    }

    /**
     * Gets the display rotation
     * Gets the display rotation
     */
    @Location("/displayRotation")
    /*inner*/ class DisplayRotation {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }


        fun response(): io.swagger.server.models.DisplayRotation {
            return io.swagger.server.models.DisplayRotation(DisplayRotationEnum.of(holder.uiDevice.displayRotation))
        }
    }

    /**
     * Gets the display size in DP
     * Gets the display size in DP
     */
    @Location("/displaySizeDp")
    /*inner*/ class DisplaySizeDp {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }


        fun response(): io.swagger.server.models.DisplaySizeDp {
            val dp = holder.uiDevice.displaySizeDp
            return io.swagger.server.models.DisplaySizeDp(dp.x, dp.y)
        }
    }

    /**
     * Gets the display width
     * Gets the display width
     */
    @Location("/displayWidth")
    /*inner*/ class DisplayWidth {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }


        fun response(): io.swagger.server.models.DisplayWidth {
            return io.swagger.server.models.DisplayWidth(holder.uiDevice.displayWidth)
        }
    }

    @Location("/findObject")
    /*inner*/ class FindObject {
        /**
         * Finds an object
         * Finds an object. The object found, if any, can be later used in other call like API.click.
         * @param resourceId the resource id (optional)
         * @param uiSelector the selectorStr sets the resource name criteria for matching. A UI element will be considered a match if its resource name exactly matches the selectorStr parameter and all other criteria for this selectorStr are met. The format of the selectorStr string is &#x60;sel@[\$]value,...&#x60; Where &#x60;sel&#x60; can be one of -  clickable -  depth -  desc -  res -  text -  scrollable &#x60;@&#x60; replaces the &#x60;&#x3D;&#x60; sign that is used to separate parameters and values in the URL. If the first character of value is &#x60;$&#x60; then a &#x60;Pattern&#x60; is created. (optional)
         * @param bySelector the selectorStr sets the resource name criteria for matching. A UI element will be considered a match if its resource name exactly matches the selectorStr parameter and all other criteria for this selectorStr are met. The format of the selectorStr string is &#x60;sel@[\$]value,...&#x60; Where &#x60;sel&#x60; can be one of - clickable - depth - desc - res - text - scrollable &#x60;@&#x60; replaces the &#x60;&#x3D;&#x60; sign that is used to separate parameters and values in the URL. If the first character of value is &#x60;$&#x60; then a &#x60;Pattern&#x60; is created. (optional)
         */
        /*inner*/ class Get(
            private val resourceId: String? = null,
            private val uiSelector: String? = null,
            private val bySelector: String? = null
        ) {
            private var holder: Holder
            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }


            fun response(): Any {
                if (resourceId ?: uiSelector ?: bySelector == null) {
                    return StatusResponse(
                        StatusResponse.Status.ERROR,
                        StatusResponse.StatusCode.ARGUMENT_MISSING.value,
                        errorMessage = "A resource Id or selector must be specified"
                    )
                }

                resourceId?.let {
                    val selector = By.res(resourceId)
                    val obj = holder.uiDevice.findObject(selector)
                    if (obj != null) {
                        val oid = objectStore.put(obj)
                        return@response ObjectRef(oid, obj.className)
                    }
                }

                uiSelector?.let {
                    val usb = uiSelectorBundleFromString(it)
                    val obj = holder.uiDevice.findObject(usb.selector)
                    if (obj != null) {
                        val oid = objectStore.put(obj)
                        return@response ObjectRef(oid, obj.className)
                    }
                }

                bySelector?.let {
                    val bsb = bySelectorBundleFromString(it)
                    val obj = holder.uiDevice.findObject(bsb.selector)
                    if (obj != null) {
                        val oid = objectStore.put(obj)
                        return@response ObjectRef(oid, obj.className)
                    }
                }

                throw HttpException(
                    HttpStatusCode.NotFound,
                    StatusResponse.StatusCode.OBJECT_NOT_FOUND.message()
                )
            }
        }

        /**
         * Finds an object
         * Finds an object. The object found, if any, can be later used in other call like API.click.
         * @param body Selector
         */
        // WARNING: ktor is not passing this argument so the '?' and null are needed
        // see https://github.com/ktorio/ktor/issues/190
        /*inner*/ class Post(private val body: Selector? = null) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }


            fun response(selector: Selector): Any {

                val obj = holder.uiDevice.findObject(selector.toBySelector())
                println("🔮obj: $obj")

                obj?.let {
                    val oid = objectStore.put(it)
                    return ObjectRef(oid, it.className)
                }

                throw HttpException(
                    HttpStatusCode.NotFound,
                    StatusResponse.StatusCode.OBJECT_NOT_FOUND.message()
                )
            }
        }
    }

    @Location("/findObjects")
    /*inner*/ class FindObjects {
        /**
         * Finds objects
         * Finds all object matching selector. The object found, if any, can be later used in other call like API.click.
         * @param bySelector the selectorStr sets the resource name criteria for matching. A UI element will be considered a match if its resource name exactly matches the selectorStr parameter and all other criteria for this selectorStr are met. The format of the selectorStr string is &#x60;sel@[\$]value,...&#x60; Where &#x60;sel&#x60; can be one of - clickable - depth - desc - res - text - scrollable &#x60;@&#x60; replaces the &#x60;&#x3D;&#x60; sign that is used to separate parameters and values in the URL. If the first character of value is &#x60;$&#x60; then a &#x60;Pattern&#x60; is created. (optional)
         */
        /*inner*/ class Get(
            private val bySelector: String? = null
        ) {
            private var holder: Holder
            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }


            fun response(): Any {
                if (bySelector == null) {
                    return StatusResponse(
                        StatusResponse.Status.ERROR,
                        StatusResponse.StatusCode.ARGUMENT_MISSING.value,
                        errorMessage = "A resource Id or selector must be specified"
                    )
                }

                bySelector.let { selector ->
                    val bsb = bySelectorBundleFromString(selector)
                    val objs = holder.uiDevice.findObjects(bsb.selector)
                    if (objs.isNotEmpty()) {
                        return@response objs.map { ObjectRef(objectStore.put(it), it.className) }
                    }
                }

                throw HttpException(
                    HttpStatusCode.NotFound,
                    StatusResponse.StatusCode.OBJECT_NOT_FOUND.message()
                )
            }
        }

    }

    /**
     * Retrieves the text from the last UI traversal event received.
     * Retrieves the text from the last UI traversal event received.
     */
    @Location("/lastTraversedText")
    /*inner*/ class LastTraversedText {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }


        fun response(): io.swagger.server.models.LastTraversedText {
            return io.swagger.server.models.LastTraversedText(holder.uiDevice.lastTraversedText)
        }
    }

    companion object {
        fun pressKeyResponse(pressAny: () -> Boolean, name: String): StatusResponse {
            if (pressAny()) {
                return StatusResponse.OK
            }
            return StatusResponse(
                StatusResponse.Status.ERROR,
                errorMessage = "Cannot press $name"
            )
        }
    }

    /**
     * Simulates a short press on the BACK button.
     * Simulates a short press on the BACK button.
     */
    @Location("/pressBack")
    /*inner*/ class PressBack {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }


        fun response(): StatusResponse {
            return pressKeyResponse(holder.uiDevice::pressBack, "BACK")
        }
    }

    /**
     * Simulates a short press on the DELETE key.
     * Simulates a short press on the DELETE key.
     */
    @Location("/pressDelete")
    /*inner*/ class PressDelete {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }


        fun response(): StatusResponse {
            return pressKeyResponse(holder.uiDevice::pressDelete, "DELETE")
        }
    }

    /**
     * Simulates a short press on the ENTER key.
     * Simulates a short press on the ENTER key.
     */
    @Location("/pressEnter")
    /*inner*/ class PressEnter {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            return pressKeyResponse(holder.uiDevice::pressEnter, "ENTER")
        }
    }

    /**
     * Simulates a short press on the HOME button.
     * Simulates a short press on the HOME button.
     */
    @Location("/pressHome")
    /*inner*/ class PressHome {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            return pressKeyResponse(holder.uiDevice::pressHome, "HOME")
        }
    }

    /**
     * Simulates a short press using a key code.
     * Simulates a short press using a key code.
     * @param keyCode the key code of the event.
     * @param metaState an integer in which each bit set to 1 represents a pressed meta key (optional)
     */
    @Location("/pressKeyCode")
    /*inner*/ class PressKeyCode(private val keyCode: Int, private val metaState: Int = 0) {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }


        fun response(): StatusResponse {
            if (holder.uiDevice.pressKeyCode(keyCode, metaState)) {
                return StatusResponse.OK
            }
            return StatusResponse(
                StatusResponse.Status.ERROR,
                StatusResponse.StatusCode.INTERACTION_KEY.value,
                errorMessage = "Cannot press KeyCode"
            )
        }
    }

    /**
     * Simulates a short press on the Recent Apps button.
     * Simulates a short press on the Recent Apps button.
     */
    @Location("/pressRecentApps")
    /*inner*/ class PressRecentApps {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            return pressKeyResponse(holder.uiDevice::pressRecentApps, "RECENT_APPS")
        }
    }

    /**
     * Retrieves the product name of the device.
     * Retrieves the product name of the device.
     */
    @Location("/productName")
    /*inner*/ class ProductName {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): io.swagger.server.models.ProductName {
            return io.swagger.server.models.ProductName(holder.uiDevice.productName)
        }
    }

    /**
     * Performs a swipe.
     */
    @Location("/swipe")
    /*inner*/ class Swipe {

        /**
         * Performs a swipe from one coordinate to another using the number of steps to determine
         * smoothness and speed. Each step execution is throttled to 5ms per step. So for a 100 steps,
         * the swipe will take about 1/2 second to complete.
         */
        class Get(
            private val startX: Int,
            private val startY: Int,
            private val endX: Int,
            private val endY: Int,
            private val steps: Int
        ) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }

            fun response(): StatusResponse {
                //Log.d("UiDevice", "clicking on ($x,$y)")
                if (holder.uiDevice.swipe(startX, startY, endX, endY, steps)) {
                    return StatusResponse(StatusResponse.Status.OK)
                }
                return StatusResponse(StatusResponse.Status.ERROR, errorMessage = "Cannot swipe")
            }
        }

        /**
         * Performs a swipe between points in the Point array. Each step execution is throttled to
         * 5ms per step. So for a 100 steps, the swipe will take about 1/2 second to complete
         */
        // WARNING: ktor is not passing this argument so the '?' and null are needed
        // see https://github.com/ktorio/ktor/issues/190
        /*inner*/ class Post(val body: SwipeBody? = null) {
            private var holder: Holder
            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }

            fun response(body: SwipeBody): Any {
                val list = ArrayList<android.graphics.Point>()
                body.segments?.forEach {
                    list.add(android.graphics.Point(it.x!!, it.y!!))
                }
                val segments = arrayOfNulls<android.graphics.Point>(list.size)
                list.toArray(segments)
                if (holder.uiDevice.swipe(segments, body.segmentSteps!!)) {
                    return StatusResponse.OK
                }
                return StatusResponse(StatusResponse.Status.ERROR, errorMessage = "Cannot swipe")
            }
        }
    }

    /**
     * Waits for the current application to idle.
     * Waits for the current application to idle.
     * @param timeout in milliseconds (optional)
     */
    @Location("/waitForIdle")
    /*inner*/ class WaitForIdle(private val timeout: Long = 10_000) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }


        fun response(): StatusResponse {
            holder.uiDevice.waitForIdle(timeout)
            return StatusResponse.OK
        }
    }

    /**
     * Waits for a window content update event to occur.
     * If a package name for the window is specified, but the current window does not have the same package name, the function returns immediately.
     * @param timeout in milliseconds
     * @param packageName the specified window package name (can be null). If null, a window update from any front-end window will end the wait (optional)
     */
    @Location("/waitForWindowUpdate")
    /*inner*/ class WaitForWindowUpdate(
        private val timeout: Long,
        val packageName: String? = null
    ) {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore:com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            val t0 = System.currentTimeMillis()
            if (holder.uiDevice.waitForWindowUpdate(packageName, timeout)) {
                return StatusResponse.OK
            }
            val t1 = System.currentTimeMillis() - t0
            return StatusResponse(
                StatusResponse.Status.ERROR,
                StatusResponse.StatusCode.TIMEOUT_WINDOW_UPDATE.value,
                if (packageName != null && t1 < timeout) "Current window does not have the same package name" else "Timeout waiting for window update"
            )
        }
    }
}
