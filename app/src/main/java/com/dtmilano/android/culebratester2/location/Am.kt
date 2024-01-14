package com.dtmilano.android.culebratester2.location

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.swagger.server.models.StatusResponse
import org.apache.commons.io.IOUtils
import java.util.concurrent.TimeUnit

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
@Location("/am")
class Am {

    @Location("/forceStop")
    class ForceStop(
        private val pkg: String,
        private val parent: Am = Am()) {
        fun response(): io.swagger.server.models.StatusResponse {
            val command = listOf("am", "force-stop", pkg)
            println("Executing $command")
            val pb = ProcessBuilder(command)
            val p = pb.start()
            val stdOut = IOUtils.toString(p.inputStream, Charsets.UTF_8)
            val stdErr = IOUtils.toString(p.errorStream, Charsets.UTF_8)
            val exitStatus = p.waitFor(30, TimeUnit.SECONDS)
            println("$command: exit status: $exitStatus")
            return if (exitStatus) {
                StatusResponse(StatusResponse.Status.OK)
            } else {
                StatusResponse(StatusResponse.Status.ERROR, errorMessage = stdErr)
            }
        }
    }
}