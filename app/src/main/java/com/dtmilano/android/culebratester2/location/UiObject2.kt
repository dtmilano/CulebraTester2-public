package com.dtmilano.android.culebratester2.location

import androidx.test.uiautomator.EventCondition
import com.dtmilano.android.culebratester2.CulebraTesterApplication
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import com.dtmilano.android.culebratester2.ObjectStore
import com.dtmilano.android.culebratester2.utils.BySelectorBundle
import com.dtmilano.android.culebratester2.utils.bySelectorBundleFromString
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.swagger.experimental.*
import io.swagger.server.models.NumberResponse
import io.swagger.server.models.ObjectRef
import io.swagger.server.models.Selector
import io.swagger.server.models.StatusResponse
import io.swagger.server.models.Text
import io.swagger.server.models.toBySelector
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.reflect.jvm.jvmName

private const val TAG = "UiObject2"

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
@Location("/uiObject2")
class UiObject2 {
    @Location("/{oid}/clear")
    /*inner*/ class Clear(val oid: Int, private val parent: UiObject2 = UiObject2()) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
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
    /*inner*/ class Click(val oid: Int, private val parent: UiObject2 = UiObject2()) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
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
        private val timeout: Long = 10000,
        private val parent: UiObject2 = UiObject2()
    ) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): StatusResponse {
            uiObject2(oid, objectStore)?.let {
                val eventCondition: EventCondition<*> =
                    objectStore[eventConditionRef] as EventCondition<*>
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
    /*inner*/ class Dump(val oid: Int, private val parent: UiObject2 = UiObject2()) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): Selector {
            uiObject2(oid, objectStore)?.let { return@response Selector(it) }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/findObject")
    /*inner*/ class FindObject(val oid: Int) {
        class Get(
            val bySelector: String,
            private val findObject: FindObject,
            private val parent: UiObject2 = UiObject2()
        ) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }

            fun response(): ObjectRef {
                uiObject2(findObject.oid, objectStore)?.let {
                    val bsb = bySelectorBundleFromString(bySelector)
                    it.findObject(bsb.selector)?.let { uiObject2Child ->
                        val oid = objectStore.put(uiObject2Child)
                        return@response ObjectRef(oid, uiObject2Child::class.jvmName)
                    }
                    throw notFound(bsb)
                }
                throw notFound(findObject.oid)
            }
        }

        // WARNING: ktor is not passing this argument so the '?' and null are needed
        // see https://github.com/ktorio/ktor/issues/190
        class Post(
            val selector: Selector? = null,
            private val findObject: FindObject,
            private val parent: UiObject2 = UiObject2()
        ) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }

            fun response(selector: Selector): ObjectRef {
                uiObject2(findObject.oid, objectStore)?.let {
                    it.findObject(selector.toBySelector())?.let { uiObject2Child ->
                        val oid = objectStore.put(uiObject2Child)
                        return@response ObjectRef(oid, uiObject2Child.className)
                    }
                    throw notFound(selector)
                }
                throw notFound(findObject.oid)
            }
        }
    }

    @Location("/{oid}/getChildCount")
    /*inner*/ class GetChildCount(
        val oid: Int,
        private val parent: UiObject2 = UiObject2()
    ) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): NumberResponse {
            uiObject2(oid, objectStore)?.let {
                return@response NumberResponse("childCount", BigDecimal(it.childCount))
            }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/getChildren")
    /*inner*/ class GetChildren(
        val oid: Int,
        private val parent: UiObject2 = UiObject2()
    ) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): List<ObjectRef> {
            uiObject2(oid, objectStore)?.let { uiObject2 ->
                val list = mutableListOf<ObjectRef>()
                uiObject2.children.forEach { child ->
                    val oid = objectStore.put(child)
                    list.add(ObjectRef(oid, child.className))
                }

                return@response list
            }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/getContentDescription")
    /*inner*/ class GetContentDescription(
        val oid: Int,
        private val parent: UiObject2 = UiObject2()
    ) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): Text {
            uiObject2(oid, objectStore)?.let {
                return@response Text(it.contentDescription)
            }
            throw notFound(oid)
        }
    }

    @Location("/{oid}/getText")
    /*inner*/ class GetText(val oid: Int, private val parent: UiObject2 = UiObject2()) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
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
    /*inner*/ class LongClick(val oid: Int, private val parent: UiObject2 = UiObject2()) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
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
        class Get(
            val text: String,
            private val setText: SetText,
            private val parent: UiObject2 = UiObject2()
        ) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
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
        class Post(
            val text: Text? = null,
            private val setText: SetText,
            private val parent: UiObject2 = UiObject2()
        ) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
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

        fun notFound(selector: Selector): HttpException {
            return HttpException(
                HttpStatusCode.NotFound,
                "⚠️ UiObject2 matching $selector not found"
            )
        }

        private fun notFound(bsb: BySelectorBundle): Throwable {
            return HttpException(HttpStatusCode.NotFound, "⚠️ UiObject2 matching $bsb not found")
        }
    }
}
