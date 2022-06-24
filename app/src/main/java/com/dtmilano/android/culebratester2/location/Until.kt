package com.dtmilano.android.culebratester2.location

import androidx.test.uiautomator.Until
import com.dtmilano.android.culebratester2.CulebraTesterApplication
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import com.dtmilano.android.culebratester2.utils.bySelectorBundleFromString
import io.ktor.locations.*
import io.swagger.server.models.ObjectRef
import io.swagger.server.models.Selector
import io.swagger.server.models.toBySelector
import javax.inject.Inject

private const val TAG = "Until"

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
@Location("/until")
class Until {
    @Location("/findObject")
    /*inner*/ class FindObject(
        private val parent: com.dtmilano.android.culebratester2.location.Until = com.dtmilano.android.culebratester2.location.Until()) {

        class Get(
            private val bySelector: String,
            private val parent: FindObject = FindObject()
        ) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }

            fun response(): ObjectRef {
                val bsb = bySelectorBundleFromString(bySelector)
                val searchCondition = Until.findObject(bsb.selector)
                val oid = objectStore.put(searchCondition)
                return ObjectRef(oid, searchCondition::class.simpleName)
            }
        }

        class Post(
            private val selector: Selector? = null,
            private val parent: FindObject = FindObject()
        ) {
            private var holder: Holder

            @Inject
            lateinit var holderHolder: HolderHolder

            @Inject
            lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

            init {
                CulebraTesterApplication().appComponent.inject(this)
                holder = holderHolder.instance
            }

            fun response(selector: Selector): ObjectRef {
                val searchCondition = Until.findObject(selector.toBySelector())
                val oid = objectStore.put(searchCondition)
                return ObjectRef(oid, searchCondition::class.simpleName)
            }
        }
    }

    @Location("/newWindow")
    /*inner*/ class NewWindow(private val parent: com.dtmilano.android.culebratester2.location.Until = com.dtmilano.android.culebratester2.location.Until()) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

        init {
            CulebraTesterApplication().appComponent.inject(this)
            holder = holderHolder.instance
        }

        fun response(): ObjectRef {
            val eventCondition = Until.newWindow()
            val oid = objectStore.put(eventCondition)
            return ObjectRef(oid, eventCondition::class.simpleName)
        }
    }

}