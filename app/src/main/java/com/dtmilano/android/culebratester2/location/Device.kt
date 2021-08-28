package com.dtmilano.android.culebratester2.location

import android.graphics.Point
import android.os.Build
import com.dtmilano.android.culebratester2.DaggerApplicationComponent
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import com.dtmilano.android.culebratester2.ObjectStore
import io.ktor.locations.*
import javax.inject.Inject

@KtorExperimentalLocationsAPI
@Location("/device")
class Device {
    @Location("/displayRealSize")
    class DisplayRealSize {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            DaggerApplicationComponent.factory().create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): io.swagger.server.models.DisplayRealSize {
            val size = Point()
            holder.windowManager.defaultDisplay.getRealSize(size)
            return io.swagger.server.models.DisplayRealSize(
                Build.DEVICE ?: "UNKNOWN",
                size.x,
                size.y
            )
        }
    }
}