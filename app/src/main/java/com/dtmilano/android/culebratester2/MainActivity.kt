@file:JvmName("MainActivityKt")

package com.dtmilano.android.culebratester2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.dtmilano.android.culebratester2.ui.theme.CulebraTester2Theme
import com.dtmilano.android.culebratester2.utils.PackageUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }
}

@Composable
private fun checkInstrumentationPresent(): Boolean {
    val context = LocalContext.current
    val instrumentationInfo = PackageUtils.isInstrumentationPresent(context = context)
    return (instrumentationInfo != null)
}

@Preview(showBackground = true)
@Composable
private fun Content() {
    CulebraTester2Theme {
        Surface(color = MaterialTheme.colors.background) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MonitorImage()
                Message(checkInstrumentationPresent())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonitorImage() {
    Surface(color = MaterialTheme.colors.background) {
        Image(
            painter = painterResource(id = R.drawable.monitor),
            contentDescription = "monitor"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Message(isInstrumentationPresent: Boolean = false) {
    Text(
        text = stringResource(id = if (isInstrumentationPresent) R.string.msg_instrumentation_installed else R.string.msg_instrumentation_not_installed),
        Modifier
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CulebraTester2Theme {
        Content()
    }
}