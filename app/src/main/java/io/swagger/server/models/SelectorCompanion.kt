package io.swagger.server.models

import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiSelector

/**
 * Constructor from [UiObject2].
 */
fun Selector(obj: UiObject2): Selector = Selector(
    obj.isCheckable, obj.className, obj.isClickable, null, obj.contentDescription,
    obj.applicationPackage, obj.resourceName, obj.isScrollable, obj.text, null, null
)

/**
 * Constructor from [UiObject].
 */
fun Selector(obj: UiObject): Selector = Selector(
    obj.isCheckable,
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
    var bySelector: BySelector? = null

    checkable?.let { bySelector = bySelector?.checkable(checkable) ?: By.checkable(checkable) }
    clazz?.let { bySelector = bySelector?.clazz(clazz) ?: By.clazz(clazz) }
    clickable?.let { bySelector = bySelector?.clickable(clickable) ?: By.clickable(clickable) }
    depth?.let { bySelector = bySelector?.depth(depth) ?: By.depth(depth) }
    pkg?.let { bySelector = bySelector?.pkg(pkg) ?: By.pkg(pkg) }
    res?.let { bySelector = bySelector?.res(res) ?: By.res(res) }
    scrollable?.let {
        bySelector = bySelector?.scrollable(scrollable) ?: By.scrollable(scrollable)
    }
    text?.let { bySelector = bySelector?.text(text) ?: By.text(text) }
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