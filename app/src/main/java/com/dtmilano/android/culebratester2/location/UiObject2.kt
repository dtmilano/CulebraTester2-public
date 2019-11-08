package com.dtmilano.android.culebratester2.location

import com.dtmilano.android.culebratester2.ObjectStore
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.swagger.experimental.HttpException
import io.swagger.server.models.Selector
import io.swagger.server.models.StatusResponse
import javax.inject.Inject

private const val TAG = "UiObject2"

@KtorExperimentalLocationsAPI
@Location("/uiObject2")
class UiObject2 {
    @Location("/{oid}/click")
    /*inner*/ class Click(val oid: Int) {
        @Inject
        lateinit var objectStore: ObjectStore

        fun response(): StatusResponse {
            uiObject2(oid, objectStore)?.let { it.click(); return@response StatusResponse.OK }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/dump")
    /*inner*/ class Dump(val oid: Int) {
        @Inject
        lateinit var objectStore: ObjectStore

        fun response(): Selector {
            uiObject2(oid, objectStore)?.let { return@response Selector(it) }
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
