package com.dtmilano.android.culebratester2.location

import androidx.test.uiautomator.Configurator
import io.ktor.locations.*
import io.swagger.server.models.StatusResponse
import io.swagger.server.models.Timeout

private const val TAG = "Configurator"

@KtorExperimentalLocationsAPI
@Location("/configurator")
class Configurator {
    @Location("/getWaitForIdleTimeout")
    /*inner*/ class GetWaitForIdleTimeout {

        fun response(): Timeout {
            return Timeout(Configurator.getInstance().waitForIdleTimeout)
        }
    }

    @Location("/setWaitForIdleTimeout")
    /*inner*/ class SetWaitForIdleTimeout(private val timeout: Long) {

        fun response(): StatusResponse {
            Configurator.getInstance().waitForIdleTimeout = timeout
            return StatusResponse(StatusResponse.Status.OK)
        }

    }
}
