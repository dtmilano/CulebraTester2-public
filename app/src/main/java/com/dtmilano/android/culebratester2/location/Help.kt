package com.dtmilano.android.culebratester2.location

import com.dtmilano.android.culebratester2.model.Help
import io.ktor.locations.Location

@Location("/help")
class Help {
    fun response(): com.dtmilano.android.culebratester2.model.Help {
        return Help("This is generic help")
    }
}