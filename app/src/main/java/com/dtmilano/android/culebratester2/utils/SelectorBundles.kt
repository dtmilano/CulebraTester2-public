package com.dtmilano.android.culebratester2.utils

import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiSelector

interface SelectorBundle<T> {
    val selector: T
    val selectorStr: String
    val toString: String
}

data class BySelectorBundle(
    override val selector: BySelector,
    override val selectorStr: String,
    override val toString: String
) : SelectorBundle<BySelector>

data class UiSelectorBundle(
    override val selector: UiSelector,
    override val selectorStr: String,
    override val toString: String
) : SelectorBundle<UiSelector>
