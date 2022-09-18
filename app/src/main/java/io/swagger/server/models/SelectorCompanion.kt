package io.swagger.server.models

import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiSelector
import com.google.gson.Gson

/**
 * Constructor from [UiObject2].
 */
fun Selector(obj: UiObject2): Selector = Selector(
    obj.isCheckable, obj.isChecked, obj.className, obj.isClickable, null, obj.contentDescription,
    obj.applicationPackage, obj.resourceName, obj.isScrollable, obj.text, null, null
)

/**
 * Constructor from [UiObject].
 */
fun Selector(obj: UiObject): Selector = Selector(
    obj.isCheckable,
    obj.isChecked,
    obj.className,
    obj.isClickable,
    null,
    obj.contentDescription,
    obj.packageName,
    null,
    obj.isScrollable,
    obj.text,
    null,
    null
)

/**
 * Returns a [BySelector] from Selector values
 */
fun Selector.toBySelector(): BySelector {
    /*
     * As there is a problem with `swagger-codegen` generating OAS3.x `oneOf` we are using an
     * internal encoding for now to support Patterns for `desc` and `text`. We use an special string
     * that starts with a literal `Pattern:` followed by the actual pattern.
     * For example `Pattern:^[a-z]+$`
     */
    val patternPrefix = "Pattern:"
    var bySelector: BySelector? = null

    checkable?.let { bySelector = bySelector?.checkable(checkable) ?: By.checkable(checkable) }
    checked?.let { bySelector = bySelector?.checked(checked) ?: By.checked(checked) }
    clazz?.let { bySelector = bySelector?.clazz(clazz) ?: By.clazz(clazz) }
    clickable?.let { bySelector = bySelector?.clickable(clickable) ?: By.clickable(clickable) }
    depth?.let { bySelector = bySelector?.depth(depth) ?: By.depth(depth) }
    desc?.let {
        bySelector = if (desc.startsWith(patternPrefix)) {
            val pattern: java.util.regex.Pattern = java.util.regex.Pattern.compile(
                desc.substring(
                    patternPrefix.length
                )
            )
            bySelector?.desc(pattern) ?: By.desc(pattern)
        } else {
            bySelector?.desc(desc) ?: By.desc(desc)
        }
    }
    // FIXME:
    // missing enabled
    // missing focusable
    // missing focused
    // missing hasChild
    pkg?.let { bySelector = bySelector?.pkg(pkg) ?: By.pkg(pkg) }
    res?.let { bySelector = bySelector?.res(res) ?: By.res(res) }
    // missing selected
    scrollable?.let {
        bySelector = bySelector?.scrollable(scrollable) ?: By.scrollable(scrollable)
    }
    text?.let {
        bySelector = if (text.startsWith(patternPrefix)) {
            val pattern: java.util.regex.Pattern = java.util.regex.Pattern.compile(
                text.substring(
                    patternPrefix.length
                )
            )
            bySelector?.text(pattern) ?: By.text(pattern)
        } else {
            bySelector?.text(text) ?: By.text(text)
        }
    }
    // items not present in BySelector:
    // no index in BySelector
    // no instance in BySelector

    return bySelector!!
}


/**
 * Returns a [UiSelector] from Selector values.
 */
fun Selector.toUiSelector(): UiSelector {
    val uiSelector = UiSelector()
    checkable?.let { uiSelector.checkable(checkable) }
    checked?.let { uiSelector.checked(checked) }
    clazz?.let { uiSelector.className(clazz) }
    clickable?.let { uiSelector.clickable(clickable) }
    // no depth in UiSelector
    desc?.let { uiSelector.description(desc) }
    pkg?.let { uiSelector.packageName(pkg) }
    res?.let { uiSelector.resourceId(res) }
    scrollable?.let { uiSelector.scrollable(scrollable) }
    text?.let { uiSelector.text(text) }
    index?.let { uiSelector.index(index) }
    instance?.let { uiSelector.instance(instance) }

    return uiSelector
}

/**
 * Returns a JSON string created from this [Selector].
 */
fun Selector.toJson(): String {
    return Gson().toJson(this)
}