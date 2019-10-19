package com.dtmilano.android.culebratester2.location

import io.ktor.locations.Location

@Location("/help")
class Help {
    fun response(): io.swagger.server.models.Help {
        return io.swagger.server.models.Help("This is generic help")
    }
}