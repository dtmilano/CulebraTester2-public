package com.dtmilano.android.culebratester2

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (shouldShowOnboarding(this)) {
            OnboardingActivity.start(this)
        } else {
            MainActivityOld.start(this)
        }
    }

    companion object {
        fun shouldShowOnboarding(context: Context): Boolean {
            val preferences = CulebraTesterApplication.getPreferences(context)
            return !(preferences.contains(CulebraTesterApplication.PREFERENCE_ONBOARDING_SHOWED) && preferences.getBoolean(
                    CulebraTesterApplication.PREFERENCE_ONBOARDING_SHOWED, false))
        }

        fun onboardingShowed(context: Context) {
            val preferences = CulebraTesterApplication.getPreferences(context)
            preferences.edit().putBoolean(CulebraTesterApplication.PREFERENCE_ONBOARDING_SHOWED, true).apply()
        }
    }
}
