package com.dtmilano.android.culebratester2.location

import com.dtmilano.android.culebratester2.DaggerApplicationComponent
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import com.dtmilano.android.culebratester2.ObjectStore
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.swagger.experimental.HttpException
import io.swagger.server.models.Selector
import io.swagger.server.models.StatusResponse
import io.swagger.server.models.Text
import javax.inject.Inject

private const val TAG = "UiObject2"

@KtorExperimentalLocationsAPI
@Location("/uiObject2")
class UiObject2 {
    @Location("/{oid}/click")
    /*inner*/ class Click(val oid: Int) {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            DaggerApplicationComponent.create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            uiObject2(oid, objectStore)?.let { it.click(); return@response StatusResponse.OK }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/dump")
    /*inner*/ class Dump(val oid: Int) {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            DaggerApplicationComponent.create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): Selector {
            uiObject2(oid, objectStore)?.let { return@response Selector(it) }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/setText")
    /*inner*/ class SetText(val oid: Int) {
        private var holder: Holder
        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            DaggerApplicationComponent.create().inject(this)
            holder = holderHolder.instance
        }

        fun response(text: Text): StatusResponse {
            uiObject2(oid, objectStore)?.let { it.text = text.text; return@response StatusResponse.OK }
            throw notFound(oid)
        }
    }

    companion object {
        /**
         * Gets an object by its [oid].
         */
        fun uiObject2(oid: Int, objectStore: ObjectStore) =
            objectStore[oid] as androidx.test.uiautomator.UiObject2?

        fun notFound(oid: Int): HttpException {
            return HttpException(HttpStatusCode.NotFound, "⚠️ Object with oid=${oid} not found")
        }
    }
}
