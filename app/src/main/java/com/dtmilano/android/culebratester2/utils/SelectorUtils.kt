package com.dtmilano.android.culebratester2.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.test.uiautomator.*
import com.dtmilano.android.culebratester2.BuildConfig
import java.util.*
import java.util.regex.Pattern


/**
 *
 */

private const val TAG = "SelectorUtils"
private val DEBUG = BuildConfig.DEBUG

fun unescapeSelectorChars(selector: String): String {
    return selector.replace("\\@", "@").replace("\\,", ",")
}

/**
 * Converts the selectorStr String to a UiSlector object.
 *
 *
 * The format of the selectorStr string is
 *
 *
 * <pre>sel@[\$]value,...</pre>
 *
 *
 * Where sel can be one of
 *
 *  * clickable
 *  * depth
 *  * desc
 *  * index
 *  * instance
 *  * package
 *  * res
 *  * scrollable
 *  * text
 *
 * If the first character of value is '$' then a pattern-method is used.
 *
 */
@SuppressLint("LongLogTag")
fun uiSelectorBundleFromString(selectorStr: String): UiSelectorBundle {
    var uiSelector = UiSelector()
    val sb = StringBuilder("UiSelector()")
    // FIXME: we are still not unescaping special chars in selectorStr Patterns (when first char is '$')
    tokenize(selectorStr, "(?<!\\\\),").forEach { token ->
        val kv =
            token.split("(?<=[^\\\\])@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (kv.size != 2) {
            Log.e(
                TAG,
                "uiSelectorBundleFromString: Malformed selectorStr, kv length != 2: " + kv.contentToString()
            )
            Log.e(TAG, "uiSelectorBundleFromString: selectorStr=$selectorStr")
            Log.e(TAG, "uiSelectorBundleFromString: token: $token")
            return@forEach
        }
        when (kv[0]) {
            "clazz", "className" -> {
                val clazz = kv[1]
                if (clazz[0] == '$') {
                    uiSelector = uiSelector.classNameMatches(clazz.substring(1))
                    sb.append('.').append("classNameMatches(").append('"').append(clazz)
                        .append('"').append(')')
                } else {
                    uiSelector = uiSelector.className(clazz)
                    sb.append('.').append("className(").append('"').append(clazz).append('"')
                        .append(')')
                }
            }

            "clickable" -> {
                val isClickable = kv[1].toBoolean()
                uiSelector = uiSelector.checkable(isClickable)
                sb.append('.').append("clickable(").append(isClickable).append(')')
            }

            "depth" -> {
                val depth = kv[1].toInt()
            }

            "desc" -> {
                val contentDescription = kv[1]
                if (contentDescription[0] == '$') {
                    // FIXME: unescaping the regular expression is a much difficult task
                    uiSelector = uiSelector.descriptionMatches(contentDescription.substring(1))
                    sb.append('.').append("descriptionMatches(").append('"')
                        .append(contentDescription).append('"').append(')')
                } else {
                    val unescapedContentDescription = unescapeSelectorChars(contentDescription)
                    uiSelector = uiSelector.description(unescapedContentDescription)
                    sb.append('.').append("description(").append('"').append(contentDescription)
                        .append('"').append(')')
                }
            }

            "index" -> {
                val index = kv[1].toInt()
                uiSelector = uiSelector.index(index)
                sb.append('.').append("index(").append(index).append(')')
            }

            "instance" -> {
                val instance = kv[1].toInt()
                uiSelector = uiSelector.instance(instance)
                sb.append('.').append("instance").append(instance).append(')')
            }

            "package" -> {
                val packageName = kv[1]
                uiSelector = uiSelector.packageName(packageName)
                sb.append('.').append("packageName(").append('"').append(packageName)
                    .append('"').append(')')
            }

            "parentIndex" -> {
                val parentIndex = kv[1].toInt()
            }

            "res" -> {
                val resourceName = kv[1]
                uiSelector = uiSelector.resourceId(resourceName)
                sb.append('.').append("resourceId(").append('"').append(resourceName)
                    .append('"').append(')')
            }

            "scrollable" -> {
                val isScrollable = kv[1].toBoolean()
                uiSelector = uiSelector.scrollable(isScrollable)
                sb.append('.').append("scrollable(").append(isScrollable).append(')')
            }

            "text" -> {
                val text = kv[1]
                if (text[0] == '$') {
                    val pattern = text.substring(1)
                    uiSelector = uiSelector.textMatches(pattern)
                    sb.append('.').append("textMatches(").append('"').append(pattern)
                        .append('"').append(')')
                } else {
                    val unescapedText = unescapeSelectorChars(text)
                    uiSelector = uiSelector.text(unescapedText)
                    sb.append('.').append("text(").append('"').append(unescapedText).append('"')
                        .append(')')
                }
            }

            else -> Log.e(TAG, "Unknown selectorStr: " + kv.contentToString())
        }// No depth in UiSelector
        //uiSelector.depth(depth);
        // Unfortunately we don't have a simple way of specifying the parent index here
        //                    uiSelector = uiSelector.parentIndex(parentIndex);
        //                    sb.append('.').append("parentIndex(").append(parentIndex).append(')');
    }
    if (DEBUG) {
        Log.w(TAG, "uiSelectorBundleFromString: '$selectorStr' => $uiSelector")
    }

    return UiSelectorBundle(uiSelector, selectorStr, sb.toString())
}

/**
 * Converts the selectorStr String to a BySlector object.
 *
 *
 * The format of the selectorStr string is
 *
 *
 * <pre>sel@[\$]value,...</pre>
 *
 *
 *
 * Where sel can be one of
 *
 *  * checkable
 *  * clazz
 *  * clickable
 *  * depth
 *  * desc
 *  * package
 *  * res
 *  * scrollable
 *  * text
 *
 * If the first character of value is '$' then a Pattern is created.
 *
 */
@SuppressLint("LongLogTag")
fun bySelectorBundleFromString(selectorStr: String): BySelectorBundle {
    // TRICK: because BySelector constructor is not public we create an "always match" condition
    // FIXME: we can get rid of the checks for 'null'
    var bySelector: BySelector? = null //By.clazz(Pattern.compile(".*"));
    val sb = StringBuilder() // new StringBuilder("By.clazz(Pattern.compile(\".*\"))");
    // FIXME: we are still not unescaping special chars in selectorStr Patterns (when first char is '$')
    val tokens =
        tokenize(selectorStr, "(?<!\\\\),")
    for (token in tokens) {
        val kv =
            token.split("(?<=[^\\\\])@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (kv.size != 2) {
            Log.e(TAG, "Malformed selectorStr: " + kv.contentToString())
            continue
        }
        if (kv.any { it.isBlank() }) {
            val msg = "kv contains empty or null elements: " + kv.contentToString()
            Log.e(TAG, msg)
            continue
        }
        when (kv[0]) {
            "checkable" -> {
                val isCheckable = kv[1].toBoolean()
                if (bySelector == null) {
                    bySelector = By.checkable(isCheckable)
                    sb.append("By.checkable(").append(isCheckable).append(")")
                } else {
                    bySelector.checkable(isCheckable)
                    sb.append(".checkable(").append(isCheckable).append(')')
                }
            }

            "clazz" -> {
                val clazz = kv[1]
                if (clazz[0] == '$') {
                    val pattern = Pattern.compile(clazz.substring(1))
                    if (bySelector == null) {
                        bySelector = By.clazz(pattern)
                        sb.append("By.clazz(Pattern.compile(\"").append(pattern).append(")")
                    } else {
                        bySelector.clazz(pattern)
                        sb.append(".clazz(Pattern.compile(\"").append(pattern).append("\")")
                    }
                } else {
                    val unescapedClazz = unescapeSelectorChars(clazz)
                    if (bySelector == null) {
                        bySelector = By.clazz(unescapedClazz)
                        sb.append("By.clazz(\"").append(unescapedClazz).append("\")")
                    } else {
                        bySelector.clazz(unescapedClazz)
                        sb.append(".clazz(\"").append(unescapedClazz).append("\")")
                    }
                }
            }

            "clickable" -> {
                val isClickable = kv[1].toBoolean()
                if (bySelector == null) {
                    bySelector = By.clickable(isClickable)
                    sb.append("By.clickable(").append(isClickable).append(')')
                } else {
                    bySelector.clickable(isClickable)
                    sb.append(".clickable(").append(isClickable).append(')')
                }
            }

            "depth" -> {
                val depth = kv[1].toInt()
                if (bySelector == null) {
                    bySelector = By.depth(depth)
                    sb.append("By.depth(").append(depth).append(')')
                } else {
                    bySelector.depth(depth)
                    sb.append(".depth(").append(depth).append(')')
                }
            }

            "desc" -> {
                val contentDescription = kv[1]
                if (contentDescription[0] == '$') {
                    val pattern = Pattern.compile(contentDescription.substring(1))
                    if (bySelector == null) {
                        bySelector = By.desc(pattern)
                        sb.append("By.desc(Pattern.compile(\"").append(pattern).append("\")")
                    } else {
                        bySelector.desc(pattern)
                        sb.append(".desc(Pattern.compile(\"").append(pattern).append("\")")
                    }
                } else {
                    val unescapedContentDescription = unescapeSelectorChars(contentDescription)
                    if (bySelector == null) {
                        bySelector = By.desc(unescapedContentDescription)
                        sb.append("By.desc(\"").append(unescapedContentDescription)
                            .append("\")")
                    } else {
                        bySelector.desc(unescapedContentDescription)
                        sb.append(".desc(\"").append(unescapedContentDescription).append("\")")
                    }
                }
            }

            "package" -> {
                val packageName = kv[1]
                if (packageName[0] == '$') {
                    val pattern = Pattern.compile(packageName.substring(1))
                    if (bySelector == null) {
                        bySelector = By.pkg(pattern)
                        sb.append("By.pkg(Pattern.compile(\"").append(pattern).append("\")")
                    } else {
                        bySelector.pkg(pattern)
                        sb.append(".pkg(Pattern.compile(\"").append(pattern).append("\")")
                    }
                } else {
                    if (bySelector == null) {
                        bySelector = By.pkg(packageName)
                        sb.append("By.pkg(\"").append(packageName).append("\")")
                    } else {
                        bySelector.pkg(packageName)
                        sb.append(".pkg(\"").append(packageName).append("\")")
                    }
                }
            }

            "res" -> {
                val resourceName = kv[1]
                if (bySelector == null) {
                    bySelector = By.res(resourceName)
                    sb.append("By.res(\"").append(resourceName).append("\")")
                } else {
                    bySelector.res(resourceName)
                    sb.append(".res(\"").append(resourceName).append("\")")
                }
            }

            "scrollable" -> {
                val isScrollable = kv[1].toBoolean()
                if (bySelector == null) {
                    bySelector = By.scrollable(isScrollable)
                    sb.append("By.scrollable(").append(isScrollable).append(")")
                } else {
                    bySelector.scrollable(isScrollable)
                    sb.append(".scrollable(").append(isScrollable).append(")")
                }
            }

            "text" -> {
                val text = kv[1]
                if (text[0] == '$') {
                    val pattern = Pattern.compile(text.substring(1))
                    if (bySelector == null) {
                        bySelector = By.text(pattern)
                        sb.append("By.text(Pattern.compile(\"").append(pattern).append("\"))")
                    } else {
                        bySelector.text(pattern)
                        sb.append(".text(Pattern.compile(\"").append(pattern.toString())
                            .append("\"))")
                    }
                } else {
                    val unescapedText = unescapeSelectorChars(text)
                    if (bySelector == null) {
                        bySelector = By.text(unescapedText)
                        sb.append("By.text(\"").append(text).append("\")")
                    } else {
                        bySelector.text(unescapedText)
                        sb.append(".text(\"").append(text).append("\")")
                    }
                }
            }

            else -> Log.e(TAG, "Unknown selectorStr: " + kv.contentToString())
        }
    }
    if (DEBUG) {
        Log.w(TAG, "bySelectorFromString: '$selectorStr' => $bySelector")
    }
    return BySelectorBundle(bySelector!!, selectorStr, sb.toString())
}

/**
 * Converts the provided String into an [EventCondition].
 *
 * @param eventConditionStr the eventCondition String. The String can a be list of conditions
 * separated by `,` (comma).
 * The supported conditions are:
 *
 *  * until:
 *
 *  * newWindow
 *  * scrollFinished
 *
 *
 * @return the converted [EventCondition]
 */
fun eventConditionFromString(eventConditionStr: String): EventCondition<Boolean>? {
    var eventCondition: EventCondition<Boolean>? = null

    val tokens = tokenize(eventConditionStr, "(?<!\\\\),")
    for (token in tokens) {
        val ccv =
            token.split("(?<=[^\\\\]):".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        when (ccv[0]) {
            "until" -> {
                val until = Until()
                val cv =
                    ccv[1].split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                when (cv[0]) {
                    "newWindow" -> eventCondition = Until.newWindow()

                    "scrollFinished" -> eventCondition =
                        Until.scrollFinished(Direction.valueOf(cv[1]))
                }
            }
        }// Unknown condition
    }

    return eventCondition
}

fun tokenize(selector: String, delimiters: String) =
    selector.split(delimiters.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

