package com.dtmilano.android.culebratester2.location

import androidx.test.uiautomator.StaleObjectException
import com.dtmilano.android.culebratester2.CulebraTesterApplication
import com.dtmilano.android.culebratester2.ObjectStore
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
@Location("/objectStore")
class ObjectStore {
    @Location("/clear")
    /*inner*/ class Clear(private val parent: com.dtmilano.android.culebratester2.location.ObjectStore = com.dtmilano.android.culebratester2.location.ObjectStore()) {
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
    /*inner*/ class List(private val parent: com.dtmilano.android.culebratester2.location.ObjectStore = com.dtmilano.android.culebratester2.location.ObjectStore()) {
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
    /*inner*/ class Remove(
        val oid: Int,
        private val parent: com.dtmilano.android.culebratester2.location.ObjectStore = com.dtmilano.android.culebratester2.location.ObjectStore()
    ) {
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
