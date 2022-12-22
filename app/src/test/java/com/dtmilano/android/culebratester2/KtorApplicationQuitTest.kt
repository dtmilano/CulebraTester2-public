package com.dtmilano.android.culebratester2

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.ClassRule
import org.junit.Ignore
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals


// We need Robolectric to run these tests because some classes use Log.[we]
@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 26, maxSdk = 33)
class KtorApplicationQuitTest {
    companion object {

        @ClassRule
        @JvmField
        val exit: ExpectedSystemExit = ExpectedSystemExit.none()
    }

    @Ignore("This tests stops the server and finishes, so no other tests run")
    @OptIn(KtorExperimentalLocationsAPI::class)
    @Test
    fun testQuit() {
        withTestApplication({ module(testing = true) }) {
            KtorApplicationQuitTest.exit.expectSystemExit()
            handleRequest(HttpMethod.Get, "/quit").apply {
                assertEquals(HttpStatusCode.Gone, response.status())
                // added sleep to try to alleviate an exception for "shutdown in progress"
                Thread.sleep(2000)
            }
        }
    }
}