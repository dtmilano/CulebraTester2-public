@file:JvmName("MainActivityKt")

package com.dtmilano.android.culebratester2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

class IsInstrumentationPresentPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(
        true,
        false
    )
}

@Preview(showBackground = true)
@Composable
fun MessagePreview(
    @PreviewParameter(IsInstrumentationPresentPreviewParameterProvider::class) isInstrumentationPresent: Boolean
) {
    Message(isInstrumentationPresent)
}


@Composable
fun Message(isInstrumentationPresent: Boolean = false) {
    Column(Modifier.absolutePadding(24.dp, 6.dp, 24.dp, 6.dp)) {
        if (isInstrumentationPresent) {
            Text(
                text = stringResource(id = R.string.msg_instrumentation_installed),
                Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.msg_start_server),
                Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.msg_start_server_command),
                    Modifier
                        .background(Color.DarkGray)
                        .absolutePadding(12.dp, 12.dp, 12.dp, 12.dp),
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.msg_activity_will_exit),
                Modifier
                    .fillMaxWidth()
                    .alpha(0.7f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        } else {
            Text(
                text = stringResource(id = R.string.msg_instrumentation_not_installed),
                Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CulebraTester2Theme {
        Content()
    }
}