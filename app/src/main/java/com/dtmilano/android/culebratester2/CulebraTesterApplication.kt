package com.dtmilano.android.culebratester2

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class CulebraTesterApplication : Application() {
    companion object {

        /**
         * Preferences store whether the welcome activity was showed.
         */
        const val PREFERENCE_ONBOARDING_SHOWED =
            "com.dtmilano.android.culebratester2.PREFERENCE_ONBOARDING_SHOWED"

        /**
         * Gets the default shared preferences for this application.
         *
         * @param context the context
         * @return the shared preferences
         */
        fun getPreferences(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        }
    }

    val appComponent = DaggerApplicationComponent.create()
}