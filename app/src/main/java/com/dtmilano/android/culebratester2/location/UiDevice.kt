package com.dtmilano.android.culebratester2.location

import android.util.Log
import com.dtmilano.android.culebratester2.Holder
import io.ktor.locations.Location
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.RuntimeException

private const val TAG = "UiDevice"
private const val REMOVE_TEMP_FILE_DELAY = 2000L

@Location("/uiDevice")
class UiDevice {
    @Location("/dumpWindowHierarchy")
    data class DumpWindowHierarchy(val format: String = "JSON") {
        fun response(): String {
            val output = ByteArrayOutputStream()
            Holder.uiDevice.dumpWindowHierarchy(output)
            return output.toString()
        }
    }

    @Location("/screenshot")
    data class Screenshot(val scale: Float = 1.0F, val quality: Int = 90) {
        /**
         * Returns a screenshot as a [File] response.
         */
        fun response(): File {
            //Log.d(TAG, "getting screenshot")
            val tempFile = createTempFile()
            if (Holder.uiDevice.takeScreenshot(tempFile, scale, quality)) {
                //Log.d("UiDevice", "returning screenshot file: " + tempFile.absolutePath)
                GlobalScope.launch { delay(REMOVE_TEMP_FILE_DELAY); removeTempFile(tempFile) }
                return tempFile
            }
            throw RuntimeException("Cannot get screenshot")
        }

        /**
         * Removes a temporary file.
         */
        private fun removeTempFile(tempFile: File) {
            if (!tempFile.delete()) {
                Log.w(TAG, "Temporary file ${tempFile.absolutePath} couldn't be deleted.")
            }
        }

        /**
         * Creates a temporary file to hold the screenshot.
         */
        private fun createTempFile(): File {
            val tempDir = Holder.cacheDir
            return File.createTempFile("screenshot", "png", tempDir)
        }
    }

    @Location("/click")
    data class Click(val x: Int, val y: Int) {
        fun response() : String {
            //Log.d("UiDevice", "clicking on ($x,$y)")
            if (Holder.uiDevice.click(x, y)) {
                return "OK"
            }
            throw RuntimeException("Cannot click")
        }
    }
}