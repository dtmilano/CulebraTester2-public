package com.dtmilano.android.culebratester2.location

import com.dtmilano.android.culebratester2.BuildConfig
import com.dtmilano.android.culebratester2.CulebraTesterApplication
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import io.ktor.http.cio.Response
import io.ktor.locations.*
import io.swagger.server.models.CulebraInfo
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
@Location("/culebra")
class Culebra {

    @Location("/help")
    class Help(private val parent: Culebra = Culebra()) {
        fun response(): io.swagger.server.models.Help {
            return io.swagger.server.models.Help("This is a sample help text")
        }

        @Location("/{api}")
        data class Query(val api: String?, private val parent: Help = Help()) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }

            fun response(): io.swagger.server.models.Help {
                val apiComponents = api?.split("/")!!
                if (apiComponents.size < 2) {
                    val extraMsg = if (!api.startsWith("/")) " and should start with \"/\"" else ""
                    val msg = "{api} must be specified${extraMsg}. Found '${api}'"
                    throw IllegalArgumentException(msg)
                }
                val restOfApiComponents = apiComponents.drop(2)
                return when (apiComponents[1]) { // there's an empty apiComponents[0] as it starts with `/`
                    "device" -> Device.help(restOfApiComponents, holder.targetContext)
                    "uiDevice" -> UiDevice.help(restOfApiComponents, holder.targetContext)
                    else -> io.swagger.server.models.Help("This is a sample help text for \"$api\"")
                }
            }
        }
    }

    @Location("/info")
    class Info(private val parent: Culebra = Culebra()) {
        fun response(): CulebraInfo {
            return CulebraInfo(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        }
    }

    @Location("/quit")
    class Quit(private val parent: Culebra = Culebra()) {
    }
}
