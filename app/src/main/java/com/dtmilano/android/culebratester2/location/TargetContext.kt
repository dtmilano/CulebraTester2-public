package com.dtmilano.android.culebratester2.location

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import com.dtmilano.android.culebratester2.CulebraTesterApplication
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import io.ktor.locations.*
import io.swagger.server.models.StatusResponse
import javax.inject.Inject

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
@Location("/targetContext")
class TargetContext {

    @Location("/startActivity")
    /*inner*/ class StartActivity(
        private val pkg: String,
        private val cls: String,
        private val uri: String? = null,
        private val parent: TargetContext = TargetContext()
    ) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
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