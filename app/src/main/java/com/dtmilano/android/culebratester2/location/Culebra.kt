package com.dtmilano.android.culebratester2.location

import com.dtmilano.android.culebratester2.model.Help
import io.ktor.locations.Location

@Location("/culebra")
class Culebra {

    @Location("/help")
    class Help {
        fun response(): com.dtmilano.android.culebratester2.model.Help {
            return Help("This is a sample help text")
        }

        @Location("/{api}")
        data class Query(val api: String?) {

            fun response(): com.dtmilano.android.culebratester2.model.Help {
                return Help("This is a sample help text for $api")
            }
        }
    }
}
