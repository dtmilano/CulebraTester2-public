package com.dtmilano.android.culebratester2

import android.graphics.Point
import android.view.Display
import android.view.WindowManager
import androidx.test.uiautomator.UiDevice
import com.dtmilano.android.culebratester2.model.DisplayRealSize
import com.dtmilano.android.culebratester2.model.Help
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.*
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Before
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.mockito.ArgumentMatcher
import java.io.File
import java.io.OutputStream
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class KtorApplicationTest {

    companion object {

        @ClassRule
        @JvmField
        val exit: ExpectedSystemExit = ExpectedSystemExit.none()

        private val realSize = mapOf("x" to 1080, "y" to 2400)

        fun setDisplaySize(point: Point) {
            println("🚒 setDisplaySize called")
            point.x = realSize["x"] ?: error("missing value")
            point.y = realSize["y"] ?: error("missing value")
        }

        class MatchPoint : ArgumentMatcher<Point> {
            override fun matches(point: Point): Boolean {
                setDisplaySize(point)
                return true
            }
        }

        class MatchOutputStream : ArgumentMatcher<OutputStream> {
            override fun matches(output: OutputStream?): Boolean {
                output?.write("<hierarchy></hierarchy>".toByteArray())
                return true
            }
        }

        val display: Display = mock {
            on { getRealSize(argThat(MatchPoint())) } doAnswer {}
        }

        val windowManager = mock<WindowManager> {
            on { defaultDisplay } doReturn display
        }

        val uiDevice = mock<UiDevice> {
            on { takeScreenshot(any(), any(), any()) } doReturn true
            on { click(any(), any())} doReturn true
            on { dumpWindowHierarchy(argThat(MatchOutputStream()))} doAnswer {}
        }

        val uiDeviceNoScreenshot = mock<UiDevice> {
            on {takeScreenshot(any(), any(), any())} doReturn false
        }

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            Holder.windowManager = windowManager
            Holder.cacheDir = File("/tmp")
            Holder.uiDevice = uiDevice
        }
    }

    @Before
    fun setup() {
        Holder.uiDevice = uiDevice
    }

    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(
                    "CulebraTester2: Go to http://localhost:80/help for usage details.\n",
                    response.content
                )
            }
        }
    }

    @Test
    fun testQuit() {
        withTestApplication({ module(testing = true) }) {
            exit.expectSystemExit()
            handleRequest(HttpMethod.Get, "/quit").apply {
                assertEquals(HttpStatusCode.Gone, response.status())
            }
        }
    }

    @Test
    fun `test non-existent url`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/DOES_NOT_EXIST").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun testJsonGson() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/json/gson").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val json = Gson().fromJson(response.content, Map::class.java)
                assertEquals(json["hello"], "world")
            }
        }
    }

    @Ignore
    @Test
    fun `test static image`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/static/ktor_logo.svg").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(response.contentType(), ContentType.parse("image/svg+xml"))
            }
        }
    }

    @Test
    fun `test culebra generic help`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/help").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val help = Gson().fromJson<Help>(response.content, Help::class.java)
                assertTrue { help.text.matches(Regex(".*generic.*")) }
            }
        }
    }

    @Test
    fun `test culebra help for some api`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/culebra/help/someapi").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val help = Gson().fromJson<Help>(response.content, Help::class.java)
                assertTrue { help.text.matches(Regex(".*someapi.*")) }
            }
        }
    }

    @Test
    fun `test culebra help for some api with query`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/culebra/help").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val help = Gson().fromJson<Help>(response.content, Help::class.java)
                println(help)
                assertTrue { help.text.matches(Regex(".*sample help.*")) }
            }
        }
    }

    @Test
    fun `test device display real size`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/device/displayRealSize").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val displayRealSize =
                    Gson().fromJson<DisplayRealSize>(response.content, DisplayRealSize::class.java)
                assertEquals(displayRealSize.device, "UNKNOWN")
                assertEquals(displayRealSize.x, realSize["x"])
                assertEquals(displayRealSize.y, realSize["y"])
            }
        }
    }

    @Test
    fun `test obtain screenshot`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/screenshot").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(response.headers["Content-Type"], "image/png")
            }
        }
    }

    @Test
    fun `test obtain screenshot with params`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/screenshot?scale=0.5&quality=50").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(response.contentType(), ContentType.Image.PNG)
            }
        }
    }

    @Test
    fun `test cannot obtain screenshot`() {
        Holder.uiDevice = uiDeviceNoScreenshot
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/screenshot?scale=0.5&quality=50").apply {
                assertEquals(HttpStatusCode.InternalServerError, response.status())
            }
        }
    }

    @Test
    fun `test click with params`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/click?x=100&y=50").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(response.content, "OK")
            }
        }
    }

    @Test
    fun `test dump window hierarchy`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/dumpWindowHierarchy").apply {
                println(response.content)
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `test click missing param`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/click?x=100").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                println(response)
                println(response.status())
                println(response.content)
            }
        }
    }
}
