package com.dtmilano.android.culebratester2.location

import androidx.test.uiautomator.Configurator
import io.ktor.locations.*
import io.swagger.server.models.StatusResponse
import io.swagger.server.models.Timeout

private const val TAG = "Configurator"

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
@Location("/configurator")
class Configurator {
    @Location("/getWaitForIdleTimeout")
    /*inner*/ class GetWaitForIdleTimeout(private val parent: com.dtmilano.android.culebratester2.location.Configurator = Configurator()) {

        fun response(): Timeout {
            return Timeout(Configurator.getInstance().waitForIdleTimeout)
        }
    }

    @Location("/setWaitForIdleTimeout")
    /*inner*/ class SetWaitForIdleTimeout(
        private val timeout: Long,
        private val parent: com.dtmilano.android.culebratester2.location.Configurator = Configurator()
    ) {

        fun response(): StatusResponse {
            Configurator.getInstance().waitForIdleTimeout = timeout
            return StatusResponse(StatusResponse.Status.OK)
        }

    }
}
