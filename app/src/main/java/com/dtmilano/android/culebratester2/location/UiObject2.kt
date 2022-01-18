package com.dtmilano.android.culebratester2.location

import androidx.test.uiautomator.EventCondition
import com.dtmilano.android.culebratester2.DaggerApplicationComponent
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import com.dtmilano.android.culebratester2.ObjectStore
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.swagger.experimental.*
import io.swagger.server.models.Selector
import io.swagger.server.models.StatusResponse
import io.swagger.server.models.Text
import javax.inject.Inject

private const val TAG = "UiObject2"

@KtorExperimentalLocationsAPI
@Location("/uiObject2")
class UiObject2 {
    @Location("/{oid}/clear")
    /*inner*/ class Clear(val oid: Int) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            DaggerApplicationComponent.factory().create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            uiObject2(oid, objectStore)?.let {
                it.clear(); return@response StatusResponse(
                StatusResponse.Status.OK
            )
            }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/click")
    /*inner*/ class Click(val oid: Int) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            DaggerApplicationComponent.factory().create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            uiObject2(oid, objectStore)?.let {
                it.click(); return@response StatusResponse(
                StatusResponse.Status.OK
            )
            }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/clickAndWait")
    /*inner*/ class ClickAndWait(
        val oid: Int,
        private val eventConditionRef: Int,
        private val timeout: Long = 10000
    ) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            DaggerApplicationComponent.factory().create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            uiObject2(oid, objectStore)?.let {
                val eventCondition: EventCondition<*> =
                    objectStore.get(eventConditionRef) as EventCondition<*>
                val result = it.clickAndWait(eventCondition, timeout)
                if (result is Boolean) {
                    if (result) {
                        return@response StatusResponse(
                            StatusResponse.Status.OK
                        )
                    } else {
                        return@response StatusResponse(
                            StatusResponse.Status.ERROR
                        )
                    }
                }

                // We don't know how to treat an EventCondition<R> when R != Boolean...
                return@response StatusResponse(
                    StatusResponse.Status.OK
                )
            }
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
            DaggerApplicationComponent.factory().create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): Selector {
            uiObject2(oid, objectStore)?.let { return@response Selector(it) }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/getText")
    /*inner*/ class GetText(val oid: Int) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            DaggerApplicationComponent.factory().create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): Text {
            uiObject2(oid, objectStore)?.let {
                return@response Text(it.text)
            }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/longClick")
    /*inner*/ class LongClick(val oid: Int) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            DaggerApplicationComponent.factory().create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            uiObject2(oid, objectStore)?.let {
                it.longClick(); return@response StatusResponse(
                StatusResponse.Status.OK
            )
            }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/setText")
    /*inner*/ class SetText(val oid: Int) {
        class Get(val text: String, val setText: SetText) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: ObjectStore

            init {
                DaggerApplicationComponent.factory().create().inject(this)
                holder = holderHolder.instance
            }

            fun response(): StatusResponse {
                uiObject2(setText.oid, objectStore)?.let {
                    it.text = text
                    return@response StatusResponse(StatusResponse.Status.OK)
                }
                throw notFound(setText.oid)
            }
        }

        // WARNING: ktor is not passing this argument so the '?' and null are needed
        // see https://github.com/ktorio/ktor/issues/190
        class Post(val text: Text? = null, val setText: SetText) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: ObjectStore

            init {
                DaggerApplicationComponent.factory().create().inject(this)
                holder = holderHolder.instance
            }

            fun response(text: Text): StatusResponse {
                uiObject2(setText.oid, objectStore)?.let {
                    it.text = text.text
                    return@response StatusResponse(StatusResponse.Status.OK)
                }
                throw notFound(setText.oid)
            }
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
