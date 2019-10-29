package com.dtmilano.android.culebratester2.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SelectorUtilsTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun test_tokenize() {
        val selector = "clickable@true"
        val delimiters = "(?<=[^\\\\])@"
        val tokens = tokenize(selector, delimiters).contentToString()
        assertEquals("[clickable, true]", tokens)
    }

    @Test
    fun test_tokenize_complex_selector() {
        val selector =
            "clickable@true,depth@3,desc@something with spaces and \\@ special chars@index@3,text@$.*"
        val delimiters = "(?<=[^\\\\])@"
        val tokens = tokenize(selector, delimiters).contentToString()
        assertEquals(
            "[clickable, true,depth, 3,desc, something with spaces and \\@ special chars, index, 3,text, \$.*]",
            tokens
        )
    }

    @Test
    fun test_unescapeSelectorChars() {
        val str = "\\@\\,"
        assertEquals("@,", unescapeSelectorChars(str))
    }

    @Test
    fun test_unescapeSelectorChars_multiple_occurrences() {
        val str = "\\@\\@\\,\\,\\@"
        assertEquals("@@,,@", unescapeSelectorChars(str))
    }

    @Test
    fun test_uiSelectorBundleFromString_missing_value() {
        val str = "clickable"
        val bundle = uiSelectorBundleFromString(str)
        assertEquals(str, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun test_uiSelectorBundleFromString_clickable_true() {
        val str = "clickable@true"
        val bundle = uiSelectorBundleFromString(str)
        assertEquals(str, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun test_uiSelectorBundleFromString_selector_with_spaces_and_special_chars() {
        val selector = "desc@something with spaces and \\@ special chars"
        val bundle = uiSelectorBundleFromString(selector)
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun test_uiSelectorBundleFromString_selector_with_spaces_and_special_chars_followed_by_another() {
        val selector = "desc@something with spaces and \\@ special chars,index@3"
        val bundle = uiSelectorBundleFromString(selector)
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun test_uiSelectorBundleFromString_complex_selector() {
        val selector =
            "clickable@true,depth@3,desc@something with spaces and \\@ special chars@index@3,text@$.*"
        val bundle = uiSelectorBundleFromString(selector)
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test(expected = KotlinNullPointerException::class)
    fun test_bySelectorBundleFromString_missing_value() {
        val selector = "clickable"
        val bundle = bySelectorBundleFromString(selector)
    }

    @Test
    fun test_bySelectorBundleFromString_clickable_true() {
        val str = "clickable@true"
        val bundle = bySelectorBundleFromString(str)
        assertEquals(str, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun test_bySelectorBundleFromString_selector_with_spaces_and_special_chars() {
        val selector = "desc@something with spaces and \\@ special chars"
        val bundle = bySelectorBundleFromString(selector)
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun test_bySelectorBundleFromString_selector_with_spaces_and_special_chars_followed_by_another() {
        val selector = "desc@something with spaces and \\@ special chars,depth@3"
        val bundle = bySelectorBundleFromString(selector)
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun test_bySelectorBundleFromString_complex_selector() {
        val selector =
            "clickable@true,depth@3,desc@something with spaces and \\@ special chars,text@$.*"
        val bundle = bySelectorBundleFromString(selector)
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun eventConditionFromString() {
    }
}