package com.dtmilano.android.culebratester2

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import io.ktor.locations.*

@HiltAndroidApp
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

    // Instance of the ApplicationComponent that will be used by all the Activities in the project
    @KtorExperimentalLocationsAPI
    val appComponent: ApplicationComponent by lazy {
        initializeComponent()
    }

    @KtorExperimentalLocationsAPI
    fun initializeComponent(): ApplicationComponent {
        // Creates an instance of ApplicationComponent using its factory method
        // We pass the applicationContext that will be used as Context in the graph
        //return DaggerApplicationComponent.factory().create(applicationContext)
        return DaggerApplicationComponent.factory().create()
    }
}