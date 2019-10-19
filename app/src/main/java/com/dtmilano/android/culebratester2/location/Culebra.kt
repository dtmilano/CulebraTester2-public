package com.dtmilano.android.culebratester2.location

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/culebra")
class Culebra {

    @Location("/help")
    class Help {
        fun response(): io.swagger.server.models.Help {
            return io.swagger.server.models.Help("This is a sample help text")
        }

        @Location("/{api}")
        data class Query(val api: String?) {

            fun response(): io.swagger.server.models.Help {
                return io.swagger.server.models.Help("This is a sample help text for $api")
            }
        }
    }
}
