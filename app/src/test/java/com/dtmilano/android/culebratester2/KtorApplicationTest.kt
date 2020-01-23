package com.dtmilano.android.culebratester2

import android.graphics.Point
import android.view.Display
import android.view.WindowManager
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import com.dtmilano.android.culebratester2.location.OidObj
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
import kotlin.text.Regex.Companion.escape


/**
 * Ktor Application Test.
 */
@KtorExperimentalLocationsAPI
// We don't need Robolectric to run these test for now
//@RunWith(RobolectricTestRunner::class)
class KtorApplicationTest {

    companion object {

        @ClassRule
        @JvmField
        val exit: ExpectedSystemExit = ExpectedSystemExit.none()

        val appComponent = DaggerApplicationComponent.create()

        var holder: Holder = appComponent.holder().instance

        var objectStore: ObjectStore = appComponent.objectStore()

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

        // <?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
        const val WINDOW_HIERARCHY = """
            <hierarchy rotation="0">
              <node index="0" text="" resource-id="com.android.systemui:id/navigation_bar_frame" class="android.widget.FrameLayout" package="com.android.systemui" content-desc="" checkable="false" checked="false" clickable="false" enabled="true" focusable="false" focused="false" scrollable="false" long-clickable="false" password="false" selected="false" visible-to-user="true" bounds="[0,1794][1080,1920]">
              </node>
            </hierarchy>
        """

        class MatchOutputStream : ArgumentMatcher<OutputStream> {
            override fun matches(output: OutputStream?): Boolean {
                output?.write(WINDOW_HIERARCHY.trimIndent().toByteArray())
                return true
            }
        }

        const val MATCHES = "MATCHES"
        const val DOES_NOT_MATCH = "DOES_NOT_MATCH"

        class BySelectorMatcher : ArgumentMatcher<BySelector> {
            override fun matches(selector: BySelector?): Boolean {
                // There's no way of comparing other than via String
                val res = "BySelector [RES='\\Q${DOES_NOT_MATCH}\\E']"
                if (selector.toString().matches(Regex(escape(res)))) {
                    return false
                }
                return true
            }
        }

        val display: Display = mock {
            on { getRealSize(argThat(matcher = MatchPoint())) } doAnswer {}
        }

        val windowManager = mock<WindowManager> {
            on { defaultDisplay } doReturn display
        }

        private const val MOCK_CLASS_NAME = "MockClassName"

        val uiObject2 = mock<UiObject2> {
            on { className } doReturn MOCK_CLASS_NAME
            on { text } doReturn "Hello Culebra!"
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
            on { findObject(argThat(BySelectorMatcher())) } doReturn uiObject2
            on { pressBack() } doReturn true
            on { pressEnter() } doReturn true
            on { pressDelete() } doReturn true
            on { pressHome() } doReturn true
            on { pressKeyCode(anyInt(), anyInt()) } doReturn true
            on { pressRecentApps() } doReturn true
            on { takeScreenshot(any(), any(), any()) } doReturn true
        }

        val uiDeviceNoScreenshot = mock<UiDevice> {
            on { takeScreenshot(any(), any(), any()) } doReturn false
        }

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            holder.windowManager = windowManager
            holder.cacheDir = File("/tmp")
        }
    }

    @Before
    fun setup() {
        holder.uiDevice = uiDevice
        objectStore.clear()
    }

    private inline fun <reified T> TestApplicationCall.jsonResponse(content: String? = response.content) =
        Gson().fromJson<T>(content, T::class.java)

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
    fun `test culebra info`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/culebra/info").apply {
                assertEquals(HttpStatusCode.OK, response.status())
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
                val displayRealSize = jsonResponse<DisplayRealSize>()
                assertEquals("UNKNOWN", displayRealSize.device)
                assertEquals(realSize["x"], displayRealSize.x)
                assertEquals(realSize["y"], displayRealSize.y)
            }
        }
    }

    @Test
    fun `test object store list`() {
        objectStore.put("Some object")
        objectStore.put("Another object")
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/objectStore/list").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                println(response.content)
                assertEquals(
                    "[{\"oid\":1,\"obj\":\"Some object\"},{\"oid\":2,\"obj\":\"Another object\"}]",
                    response.content
                )
            }
        }
    }

    @Test
    fun `test object store list with uiobject2`() {
        val uio21 = mock<UiObject2> {}
        val uio22 = mock<UiObject2> {}
        objectStore.put(uio21)
        objectStore.put(uio22)
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/objectStore/list").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                println(response.content)
                val list = jsonResponse<Array<OidObj>>()
                assertEquals(2, list.size)
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
            assertEquals(0, objectStore.size())
            handleRequest(
                HttpMethod.Get,
                "/v2/uiDevice/findObject?resourceId=$DOES_NOT_MATCH"
            ).apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals(
                    StatusResponse.StatusCode.OBJECT_NOT_FOUND.message() + "\n",
                    response.content
                )
            }
            assertEquals(0, objectStore.size())
        }
    }

    @Ignore
    @Test
    fun `test find object uiSelector selector`() {
        withTestApplication({ module(testing = true) }) {
            assertEquals(0, objectStore.size())
            handleRequest(
                HttpMethod.Get,
                "/v2/uiDevice/findObject?uiSelector=clazz@$MATCHES"
            ).apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            assertEquals(1, objectStore.size())
        }
    }

    @Test
    fun `test find object bySelector selector`() {
        withTestApplication({ module(testing = true) }) {
            assertEquals(0, objectStore.size())
            handleRequest(
                HttpMethod.Get,
                "/v2/uiDevice/findObject?bySelector=clazz@$MATCHES"
            ).apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            assertEquals(1, objectStore.size())
        }
    }

    @Test
    fun `test find object should not store object when not found`() {
        withTestApplication({ module(testing = true) }) {
            assertEquals(0, objectStore.size())
            handleRequest(
                HttpMethod.Get,
                "/v2/uiDevice/findObject?resourceId=$DOES_NOT_MATCH"
            ).apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals(
                    StatusResponse.StatusCode.OBJECT_NOT_FOUND.message() + "\n",
                    response.content
                )
                assertEquals(0, objectStore.size())
            }
        }
    }

    @Test
    fun `test find object should store object when found`() {
        withTestApplication({ module(testing = true) }) {
            assertEquals(0, objectStore.size())
            handleRequest(HttpMethod.Get, "/v2/uiDevice/findObject?resourceId=$MATCHES").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val objectRef = jsonResponse<ObjectRef>()
                assertTrue(objectRef.oid > 0)
                assertEquals(MOCK_CLASS_NAME, objectRef.className)
                assertEquals(1, objectStore.size())
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
                val objectRef = jsonResponse<ObjectRef>()
                assertTrue(objectRef.oid > 0)
                assertEquals(MOCK_CLASS_NAME, objectRef.className)
            }
        }
    }

    @Test
    fun `test find object post selector not matching should respond not found`() {
        withTestApplication({ module(testing = true) }) {
            assertEquals(0, objectStore.size())
            handleRequest(HttpMethod.Post, "/v2/uiDevice/findObject") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    Gson().toJson(
                        Selector(
                            res = DOES_NOT_MATCH
                        )
                    )
                )
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals(
                    StatusResponse.StatusCode.OBJECT_NOT_FOUND.message() + "\n",
                    response.content
                )
                assertEquals(0, objectStore.size())
            }
        }
    }

    @Test
    fun `test get text`() {
        assertEquals(0, objectStore.size())
        val oid = objectStore.put(uiObject2)
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiObject2/$oid/getText").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val text = jsonResponse<Text>()
                assertEquals("Hello Culebra!", text.text)
            }
        }
    }

    @Test
    fun `test set text`() {
        assertEquals(0, objectStore.size())
        val oid = objectStore.put(uiObject2)
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/v2/uiObject2/$oid/setText") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    Gson().toJson(
                        Text(text = "Some text")
                    )
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
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
    fun `test swipe`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(
                HttpMethod.Get,
                "/v2/uiDevice/swipe?startX=0&startY=0&endX=100&endY=100&steps=10"
            ).apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `test swipe post`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/v2/uiDevice/swipe") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    Gson().toJson(
                        SwipeBody(
                            arrayOf(
                                io.swagger.server.models.Point(0, 0),
                                io.swagger.server.models.Point(5, 5),
                                io.swagger.server.models.Point(10, 10)
                            ), 10
                        )
                    )
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
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
        holder.uiDevice = uiDeviceNoScreenshot
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
    fun `test dump window hierarchy with default format`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/dumpWindowHierarchy").apply {
                println(response.content)
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `test dump window hierarchy json`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/dumpWindowHierarchy?format=JSON").apply {
                println("✈️")
                println(response.content)
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `test dump window hierarchy xml`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/dumpWindowHierarchy?format=XML").apply {
                println(response.content)
                assertEquals(
                    "<hierarchy rotation=\"0\">\n" +
                            "  <node index=\"0\" text=\"\" resource-id=\"com.android.systemui:id/navigation_bar_frame\" class=\"android.widget.FrameLayout\" package=\"com.android.systemui\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" visible-to-user=\"true\" bounds=\"[0,1794][1080,1920]\">\n" +
                            "  </node>\n" +
                            "</hierarchy>", response.content
                )
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `test dump window hierarchy invalid`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/dumpWindowHierarchy?format=INVALID").apply {
                assertEquals(HttpStatusCode.UnprocessableEntity, response.status())
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
    fun `test press recent app`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/v2/uiDevice/pressRecentApps").apply {
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
