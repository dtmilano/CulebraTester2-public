package com.dtmilano.android.culebratester2.location

import android.graphics.Point
import android.os.Build
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.model.DisplayRealSize
import io.ktor.locations.Location

@Location("/device")
class Device {
    @Location("/displayRealSize")
    class DisplayRealSize {
        fun response(): com.dtmilano.android.culebratester2.model.DisplayRealSize {
            val size = Point();
            Holder.windowManager.defaultDisplay.getRealSize(size)
            return DisplayRealSize(Build.DEVICE ?: "UNKNOWN", size.x, size.y)
        }
    }
}