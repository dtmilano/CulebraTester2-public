package com.dtmilano.android.culebratester2.location

import com.dtmilano.android.culebratester2.BuildConfig
import io.ktor.locations.*
import io.swagger.server.models.CulebraInfo

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
        data class Query(val api: String?, private val help: Help = Help()) {

            fun response(): io.swagger.server.models.Help {
                return io.swagger.server.models.Help("This is a sample help text for \"$api\"")
            }
        }
    }

    @Location("/info")
    class Info(private val parent: Culebra = Culebra()) {
        fun response(): CulebraInfo {
            return CulebraInfo(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        }
    }
}
