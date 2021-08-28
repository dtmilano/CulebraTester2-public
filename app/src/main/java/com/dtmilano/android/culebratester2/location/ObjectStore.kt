package com.dtmilano.android.culebratester2.location

import com.dtmilano.android.culebratester2.CulebraTesterApplication
import com.dtmilano.android.culebratester2.ObjectStore
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import javax.inject.Inject

@KtorExperimentalLocationsAPI
@Location("/objectStore")
class ObjectStore {
    @Location("/list")
    /*inner*/ class List {
        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
        }

        fun response(): Any {
            val a = ArrayList<Any>()
            objectStore.list().forEach { (k, v) ->
                a.add(OidObj(k, v.toString()))
            }
            return a
        }
    }
}

// TODO: perhaps we should unify with ObjectRef
data class OidObj(val oid: Int, val obj: Any)
