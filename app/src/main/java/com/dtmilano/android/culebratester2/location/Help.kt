package com.dtmilano.android.culebratester2.location

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/help")
class Help {
    fun response(): io.swagger.server.models.Help {
        return io.swagger.server.models.Help("This is generic help")
    }
}