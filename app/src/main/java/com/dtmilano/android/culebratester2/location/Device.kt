package com.dtmilano.android.culebratester2.location

import android.graphics.Point
import android.os.Build
import com.dtmilano.android.culebratester2.Holder
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/device")
class Device {
    @Location("/displayRealSize")
    class DisplayRealSize {
        fun response(): io.swagger.server.models.DisplayRealSize {
            val size = Point();
            Holder.windowManager.defaultDisplay.getRealSize(size)
            return io.swagger.server.models.DisplayRealSize(
                Build.DEVICE ?: "UNKNOWN",
                size.x,
                size.y
            )
        }
    }
}