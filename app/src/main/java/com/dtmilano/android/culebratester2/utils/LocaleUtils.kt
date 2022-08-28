package com.dtmilano.android.culebratester2.utils

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import java.util.*

class LocaleUtils {
    companion object {
        private const val TAG = "LocaleUtils"
        private const val DRY_RUN = false

        @SuppressLint("PrivateApi")
        fun changeLocale(locale: Locale) {
            if (!availableLocales().contains(locale)) {
                val msg = "Invalid locale detected: $locale. Should be one of ${availableLocales()}"
                throw RuntimeException(msg)
            }

            if (DRY_RUN) {
                println("changeLocale: dry run: locale=$locale")
                throw RuntimeException("dry run")
            }

            try {
                Log.d(TAG, "changeLocale: getting activity manager")
                val activityManager = Class.forName("android.app.ActivityManager")

                Log.d(TAG, "changeLocale: getting service")
                val getService = activityManager.getMethod("getService")
                getService.isAccessible = true
                Log.d(TAG, "changeLocale: invoking method: $getService")
                val activityManagerService = getService.invoke(activityManager)
                Log.d(TAG, "changeLocale: result: $activityManagerService")

                Log.d(TAG, "changeLocale: getting IActivityManager")
                val iActivityManager26plus: Class<*> = Class.forName("android.app.IActivityManager")

                val getConfiguration = iActivityManager26plus.getMethod("getConfiguration")
                getConfiguration.isAccessible = true
                Log.d(TAG, "changeLocale: getting configuration")
                val configuration = getConfiguration.invoke(activityManagerService) as Configuration?

                configuration?.let {

                    configuration.javaClass.getField("userSetLocale").setBoolean(configuration, true)

                    val updateConfiguration =
                        iActivityManager26plus.getMethod(
                            "updateConfiguration",
                            Configuration::class.java
                        )
                    updateConfiguration.isAccessible = true
                    updateConfiguration.invoke(activityManagerService, setConfigurationLocale(configuration, locale))
                    return
                }

                Log.e(TAG, "changeLocale: could get configuration")
            } catch (e: Exception) {
                Log.e(TAG, "Error in method changeLocale", e)
                throw RuntimeException("Error in method changeLocale", e)
            }
        }

        private fun setConfigurationLocale(
            configuration: Configuration,
            locale: Locale
        ): Configuration {
            configuration.setLocale(locale)
            return configuration
        }

        fun availableLocales(): List<Locale> {
            return Locale.getAvailableLocales().toList()
        }
    }
}