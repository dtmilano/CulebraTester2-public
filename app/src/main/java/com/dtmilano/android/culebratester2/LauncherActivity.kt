package com.dtmilano.android.culebratester2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = CulebraTesterApplication.getPreferences(this)
        if (!(preferences.contains(CulebraTesterApplication.PREFERENCE_WELCOME_SHOWED) && preferences.getBoolean(
                CulebraTesterApplication.PREFERENCE_WELCOME_SHOWED, false
            ))
        ) {
            WelcomeActivity.start(this)
        } else {
            MainActivity.start(this)
        }
    }
}
