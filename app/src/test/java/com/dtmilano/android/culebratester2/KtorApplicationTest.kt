package com.dtmilano.android.culebratester2

import android.graphics.Point
import android.view.Display
import android.view.WindowManager
import androidx.test.uiautomator.UiDevice
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.*
import io.swagger.server.models.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.anyInt
import java.io.File
import java.io.OutputStream
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


/**
 * Ktor Application Test.
 */
@KtorExperimentalLocationsAPI
class KtorApplicationTest {

    companion object {

        @ClassRule
        @JvmField
        val exit: ExpectedSystemExit = ExpectedSystemExit.none()

        private val realSize = mapOf("x" to 1080, "y" to 2400)

        fun setDisplaySize(point: Point) {
            println("🖥 setDisplaySize called")
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
            on { getRealSize(argThat(matcher = MatchPoint())) } doAnswer {}
        }

        val windowManager = mock<WindowManager> {
            on { defaultDisplay } doReturn display
        }

        val uiDevice = mock<UiDevice> {
            val x = realSize["x"] ?: error("x not defined")
            val y = realSize["y"] ?: error("y not defined")
            val p = Point()
            setDisplaySize(p)

            on { click(any(), any()) } doReturn true
            on { displayWidth } doReturn x
            on { displayHeight } doReturn y
            on { displaySizeDp } doReturn p
            on { dumpWindowHierarchy(argThat(MatchOutputStream())) } doAnswer {}
            on { pressBack() } doReturn true
            on { pressEnter() } doReturn true
            on { pressDelete() } doReturn true
            on { pressHome() } doReturn true
            on { pressKeyCode(anyInt(), anyInt()) } doReturn true
            on { takeScreenshot(any(), any(), any()) } doReturn true
        }

        val uiDeviceNoScreenshot = mock<UiDevice> {
            on { takeScreenshot(any(), any(), any()) } doReturn false
        }

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            Holder.windowManager = windowManager
            Holder.cacheDir = File("/tmp")
        }
    }

    @Before
    fun setup() {
        Holder.uiDevice = uiDevice
    }

    private inline fun <reified T> TestApplicationCall.jsonResponse() =
        Gson().fromJson<T>(response.content, T::class.java)

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
                assertEquals("UNKNOWN", displayRealSize.device)
                assertEquals(realSize["x"], displayRealSize.x)
                assertEquals(realSize["y"], displayRealSize.y)
            }
        }
    }

    @Test
    fun `test find object no selectors`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/findObject").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val statusResponse = jsonResponse<StatusResponse>()
                assertEquals("ERROR", statusResponse.status.value, statusResponse.errorMessage)
            }
        }
    }

    @Test
    fun `test find object resourceId selector`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/findObject?resourceId=1").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val statusResponse = jsonResponse<StatusResponse>()
                // TODO: not implemented yet
                assertEquals("ERROR", statusResponse.status.value, statusResponse.errorMessage)
            }
        }
    }

    @Test
    fun `test find object post selector`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/v2/uiDevice/findObject") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    Gson().toJson(
                        Selector(
                            clazz = "android.widget.Button",
                            depth = 1,
                            desc = "Equal"
                        )
                    )
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val statusResponse = jsonResponse<StatusResponse>()
                // TODO: not implemented yet
                assertEquals("ERROR", statusResponse.status.value, statusResponse.errorMessage)
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
                val statusResponse = jsonResponse<StatusResponse>()
                assertEquals(StatusResponse.Status.OK, statusResponse.status)
            }
        }
    }

    @Test
    fun `test get current package name`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/currentPackageName").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val currentPackageName = jsonResponse<CurrentPackageName>()
                assertEquals(null, currentPackageName.currentPackageName)
            }
        }
    }

    @Test
    fun `test display height`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/displayHeight").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val displayHeight = jsonResponse<DisplayHeight>()
                assertEquals(realSize["y"], displayHeight.displayHeight)
            }
        }
    }

    @Test
    fun `test display width`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/displayWidth").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val displayWidth = jsonResponse<DisplayWidth>()
                assertEquals(realSize["x"], displayWidth.displayWidth)
            }
        }
    }

    @Test
    fun `test get display rotation`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/displayRotation").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val displayRotation = jsonResponse<DisplayRotation>()
                assertEquals(0, displayRotation.displayRotation?.value)
            }
        }
    }

    @Test
    fun `test get display size dp`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/displaySizeDp").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val displaySizeDp = jsonResponse<DisplaySizeDp>()
                assertEquals(realSize["x"], displaySizeDp.displaySizeDpX)
                assertEquals(realSize["y"], displaySizeDp.displaySizeDpY)
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
    fun `test get last traversed text`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/lastTraversedText").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val lastTraversedText = jsonResponse<LastTraversedText>()
                assertEquals(null, lastTraversedText.lastTraversedText)
            }
        }
    }

    @Test
    fun `test press back`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/pressBack").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val statusResponse = jsonResponse<StatusResponse>()
                assertEquals("OK", statusResponse.status.value)
            }
        }
    }

    @Test
    fun `test press delete`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/pressDelete").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val statusResponse = jsonResponse<StatusResponse>()
                assertEquals("OK", statusResponse.status.value)
            }
        }
    }

    @Test
    fun `test press enter`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/pressEnter").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val statusResponse = jsonResponse<StatusResponse>()
                assertEquals("OK", statusResponse.status.value)
            }
        }
    }

    @Test
    fun `test press home`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/pressHome").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val statusResponse = jsonResponse<StatusResponse>()
                assertEquals("OK", statusResponse.status.value)
            }
        }
    }

    @Test
    fun `test press key code`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/pressKeyCode?keyCode=10").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val statusResponse = jsonResponse<StatusResponse>()
                assertEquals("OK", statusResponse.status.value)
            }
        }
    }

    @Test
    fun `test get product name`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/productName").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val productName = jsonResponse<ProductName>()
                assertEquals(null, productName.productName)
            }
        }
    }

    @Test
    fun `test wait for idle`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/waitForIdle").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val statusResponse = jsonResponse<StatusResponse>()
                assertEquals("OK", statusResponse.status.value)
            }
        }
    }

    @Test
    fun `test wait for window update`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/waitForWindowUpdate?timeout=10").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val statusResponse = jsonResponse<StatusResponse>()
                // No window update will happen, so we expect ERROR
                assertEquals("ERROR", statusResponse.status.value, statusResponse.errorMessage)
            }
        }
    }

    @Test
    fun `test wait for window update with package name`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(
                HttpMethod.Get,
                "/v2/uiDevice/waitForWindowUpdate?timeout=10&packageName=com.android.systemui"
            ).apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val statusResponse = jsonResponse<StatusResponse>()
                // No window update will happen, so we expect ERROR
                assertEquals("ERROR", statusResponse.status.value, statusResponse.errorMessage)
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
