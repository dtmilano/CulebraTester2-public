package com.dtmilano.android.culebratester2.location

import com.dtmilano.android.culebratester2.ObjectStore
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/objectStore")
class ObjectStore {
    @Location("/list")
    class List {
        fun response(): Any {
            val a = ArrayList<Any>()
            ObjectStore.instance.list().forEach { (k, v) ->
                a.add(OidObj(k, v))
            }
            return a
        }
    }

    data class OidObj(val oid: Int, val obj: Any)
}