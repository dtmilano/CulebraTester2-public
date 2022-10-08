package com.dtmilano.android.culebratester2.location

import android.graphics.Point
import com.dtmilano.android.culebratester2.CulebraTesterApplication
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import com.dtmilano.android.culebratester2.ObjectStore
import com.dtmilano.android.culebratester2.utils.BySelectorBundle
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.swagger.experimental.HttpException
import io.swagger.server.models.PerformTwoPointerGestureBody
import io.swagger.server.models.Selector
import io.swagger.server.models.StatusResponse
import javax.inject.Inject

private const val TAG = "UiObject2"

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
@Location("/uiObject")
class UiObject {
    @Location("/{oid}/performTwoPointerGesture")
    /*inner*/ class PerformTwoPointerGesture(val oid: Int) {
        // WARNING: ktor is not passing this argument so the '?' and null are needed
        // see https://github.com/ktorio/ktor/issues/190
        class Post(
            val body: PerformTwoPointerGestureBody? = null,
            private val performTwoPointerGesture: PerformTwoPointerGesture,
            val parent: UiObject = UiObject()
        ) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }

            fun response(body: PerformTwoPointerGestureBody): StatusResponse {
                uiObject(performTwoPointerGesture.oid, objectStore)?.let {
                    val s1 = Point(body.startPoint1.x!!, body.startPoint1.y!!)
                    val s2 = Point(body.startPoint2.x!!, body.startPoint2.y!!)
                    val e1 = Point(body.endPoint1.x!!, body.endPoint1.y!!)
                    val e2 = Point(body.endPoint2.x!!, body.endPoint2.y!!)
                    if (it.performTwoPointerGesture(s1, s2, e1, e2, body.steps)) {
                        return@response StatusResponse(
                            StatusResponse.Status.OK
                        )
                    }
                    return StatusResponse(StatusResponse.Status.ERROR)
                }
                throw notFound(performTwoPointerGesture.oid)
            }
        }
    }

    @Location("/{oid}/pinchIn")
    /*inner*/ class PinchIn(
        val oid: Int,
        private val percentage: Int,
        private val steps: Int,
        private val parent: UiObject2 = UiObject2()
    ) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            uiObject(oid, objectStore)?.let {
                if (it.pinchIn(percentage, steps)) {
                    return@response StatusResponse(StatusResponse.Status.OK)
                }
                return StatusResponse(StatusResponse.Status.ERROR)
            }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/pinchOut")
    /*inner*/ class PinchOut(
        val oid: Int,
        private val percentage: Int,
        private val steps: Int,
        private val parent: UiObject2 = UiObject2()
    ) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            uiObject(oid, objectStore)?.let {
                if (it.pinchOut(percentage, steps)) {
                    return@response StatusResponse(StatusResponse.Status.OK)
                }
                return StatusResponse(StatusResponse.Status.ERROR)
            }
            throw notFound(oid)
        }
    }

    companion object {
        /**
         * Gets an object by its [oid].
         */
        fun uiObject(oid: Int, objectStore: ObjectStore) =
            objectStore[oid] as androidx.test.uiautomator.UiObject?

        fun notFound(oid: Int): HttpException {
            return HttpException(HttpStatusCode.NotFound, "⚠️ Object with oid=${oid} not found")
        }

        fun notFound(selector: Selector): HttpException {
            return HttpException(
                HttpStatusCode.NotFound,
                "⚠️ UiObject matching $selector not found"
            )
        }

        private fun notFound(bsb: BySelectorBundle): Throwable {
            return HttpException(HttpStatusCode.NotFound, "⚠️ UiObject matching $bsb not found")
        }
    }
}