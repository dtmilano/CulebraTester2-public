package com.dtmilano.android.culebratester2.location

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import com.dtmilano.android.culebratester2.ApplicationComponent
import com.dtmilano.android.culebratester2.DaggerApplicationComponent
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.swagger.server.models.StatusResponse
import javax.inject.Inject

@KtorExperimentalLocationsAPI
@Location("/targetContext")
class TargetContext {

    @Location("/startActivity")
    /*inner*/ class StartActivity(val pkg: String, val cls: String, val uri: String?) {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        init {
            DaggerApplicationComponent.create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): Any {
            println("TargetContext.StartActivity: holder = $holder")
            val intent = Intent(Intent.ACTION_MAIN)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.component = ComponentName(pkg, cls)
            uri?.let {
                intent.data = Uri.parse(uri)
            }
            holder.targetContext.get()!!.startActivity(intent)
            holder.uiDevice.waitForIdle(5000)
            return StatusResponse(StatusResponse.Status.OK)
        }
    }
}