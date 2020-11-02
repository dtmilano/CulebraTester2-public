package com.dtmilano.android.culebratester2.utils

import android.content.Context
import android.content.pm.InstrumentationInfo
import android.util.Log

private val DEBUG: Boolean = com.dtmilano.android.culebratester2.BuildConfig.DEBUG
private const val TAG = "PackageUtils"

class PackageUtils {
    companion object {
        fun isInstrumentationPresent(context: Context): InstrumentationInfo? {
            val flags = 0
            val packageName: String = context.packageName
            val list: List<InstrumentationInfo> =
                context.packageManager.queryInstrumentation(packageName, flags)
            if (DEBUG) {
                Log.w(TAG, "InstrumentationInfo=$list")
            }
            return if (list.isNotEmpty()) {
                for (ii in list) {
                    if ("$packageName.test" == ii.packageName) {
                        Log.d(TAG, "InstrumentationInfo found: $packageName.test")
                        return ii
                    }
                }
                Log.e(TAG, "Error getting InstrumentationInfo for $packageName.test")
                null
            } else {
                Log.e(TAG, "Error getting instrumentation for $packageName")
                null
            }
        }
    }

}