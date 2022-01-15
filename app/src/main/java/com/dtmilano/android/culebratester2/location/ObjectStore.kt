package com.dtmilano.android.culebratester2.location

import androidx.test.uiautomator.StaleObjectException
import com.dtmilano.android.culebratester2.CulebraTesterApplication
import com.dtmilano.android.culebratester2.ObjectStore
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.swagger.server.models.StatusResponse
import javax.inject.Inject

@KtorExperimentalLocationsAPI
@Location("/objectStore")
class ObjectStore {
    @Location("/clear")
    /*inner*/ class Clear {
        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
        }

        fun response(): StatusResponse {
            objectStore.clear()
            return StatusResponse(StatusResponse.Status.OK)
        }
    }

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
                try {
                    a.add(OidObj(k, v.toString()))
                } catch (e: StaleObjectException) {
                    objectStore.remove(k)
                }
            }
            return a
        }
    }

    @Location("/remove")
    /*inner*/ class Remove(val oid: Int) {
        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
        }

        fun response(): Any {
            objectStore.remove(oid)
            return StatusResponse(StatusResponse.Status.OK)
        }
    }
}

// TODO: perhaps we should unify with ObjectRef
data class OidObj(val oid: Int, val obj: Any)
