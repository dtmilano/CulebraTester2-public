package com.dtmilano.android.culebratester2.location

import android.content.ComponentName
import android.content.Intent
import com.dtmilano.android.culebratester2.Holder
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.swagger.server.models.StatusResponse

@KtorExperimentalLocationsAPI
@Location("/targetContext")
class TargetContext {
    @Location("/startActivity")
    data class StartActivity(val pkg: String, val cls: String) {
        fun response(): Any {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.component = ComponentName(pkg, cls)
            Holder.targetContext.get()!!.startActivity(intent)
            Holder.uiDevice.waitForIdle(5000)
            return StatusResponse.OK
        }
    }
}