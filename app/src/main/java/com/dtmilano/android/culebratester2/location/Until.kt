package com.dtmilano.android.culebratester2.location

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Until
import com.dtmilano.android.culebratester2.DaggerApplicationComponent
import com.dtmilano.android.culebratester2.Holder
import com.dtmilano.android.culebratester2.HolderHolder
import com.dtmilano.android.culebratester2.utils.bySelectorBundleFromString
import io.ktor.locations.*
import io.swagger.server.models.ObjectRef
import javax.inject.Inject

private const val TAG = "Until"

@KtorExperimentalLocationsAPI
@Location("/until")
class Until {
    @Location("/findObject")
    /*inner*/ class FindObject(private val bySelector: String) {
        private var holder: Holder

        @Inject
        lateinit var holderHolder: HolderHolder

        @Inject
        lateinit var objectStore: com.dtmilano.android.culebratester2.ObjectStore

        init {
            DaggerApplicationComponent.factory().create().inject(this)
            holder = holderHolder.instance
        }

        fun response(): ObjectRef {
            val bsb = bySelectorBundleFromString(bySelector)
            val searchCondition = Until.findObject(bsb.selector)
            val oid = objectStore.put(searchCondition)
            return ObjectRef(oid, searchCondition::class.simpleName)
        }
    }

}