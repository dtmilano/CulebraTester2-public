package com.dtmilano.android.culebratester2.utils

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SelectorUtilsTestRobolectric {

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
        println(
            "🔥 tokens $selector => ${JavaSelectorUtils.tokenize(
                selector,
                delimiters
            ).contentToString()}"
        )
        println("❄️ tokens $selector => $tokens")
        assertEquals("[clickable, true]", tokens)
    }

    @Test
    fun test_tokenize_complex_selector() {
        val selector =
            "clickable@true,depth@3,desc@something with spaces and \\@ special chars@index@3,text@$.*"
        val delimiters = "(?<=[^\\\\])@"
        val tokens = tokenize(selector, delimiters).contentToString()
        println(
            "🔥 tokens $selector => ${JavaSelectorUtils.tokenize(
                selector,
                delimiters
            ).contentToString()}"
        )
        println("❄️ tokens $selector => $tokens")
        assertEquals(
            "[clickable, true,depth, 3,desc, something with spaces and \\@ special chars, index, 3,text, \$.*]",
            tokens
        )
    }

    @Test
    fun `test unescapeSelectorChars`() {
        val str = "\\@\\,"
        assertEquals("@,", unescapeSelectorChars(str))
        println("🔥 unscape ${str} => ${JavaSelectorUtils.unescapeSelectorChars(str)}")
        println("❄️ unscape ${str} => ${unescapeSelectorChars(str)}")
        //assertEquals(unescapeSelectorChars(str), JavaSelectorUtils.unescapeSelectorChars(str))
    }

    @Test
    fun `test unescapeSelectorChars_multiple_occurrences`() {
        val str = "\\@\\@\\,\\,\\@"
        assertEquals("@@,,@", unescapeSelectorChars(str))
        println("🔥 unscape ${str} => ${JavaSelectorUtils.unescapeSelectorChars(str)}")
        println("❄️ unscape ${str} => ${unescapeSelectorChars(str)}")
        //assertEquals(unescapeSelectorChars(str), JavaSelectorUtils.unescapeSelectorChars(str))
    }

    @Test
    fun `test uiSelectorBundleFromString_missing_value`() {
        val str = "clickable"
        val bundle = uiSelectorBundleFromString(str)
        println("🔥 bundle ${str} => ${JavaSelectorUtils.uiSelectorBundleFromString(str)}")
        println("❄️ bundle ${str} => ${bundle}")
        assertEquals(str, bundle.selectorStr)
        assertNotNull(bundle.selector)
        println(bundle.selector)
    }

    @Test
    fun `test uiSelectorBundleFromString_clickable_true`() {
        val str = "clickable@true"
        val bundle = uiSelectorBundleFromString(str)
        println("🔥 bundle ${str} => ${JavaSelectorUtils.uiSelectorBundleFromString(str)}")
        println("❄️ bundle ${str} => ${bundle}")
        assertEquals(str, bundle.selectorStr)
        assertNotNull(bundle.selector)
        println(bundle.selector)
    }

    @Test
    fun `test uiSelectorBundleFromString_selector_with_spaces_and_special_chars`() {
        val selector = "desc@something with spaces and \\@ special chars"
        val bundle = uiSelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.uiSelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
        println(bundle.selector)
    }

    @Test
    fun `test uiSelectorBundleFromString_selector_with_spaces_and_special_chars_followed_by_another`() {
        val selector = "desc@something with spaces and \\@ special chars,index@3"
        val bundle = uiSelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.uiSelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
        println(bundle.selector)
    }

    @Test
    fun `test uiSelectorBundleFromString_complex_selector`() {
        val selector =
            "clickable@true,depth@3,desc@something with spaces and \\@ special chars@index@3,text@$.*"
        val bundle = uiSelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.uiSelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
        println(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString_missing_value`() {
        val selector = "clickable"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNull(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString_clickable_true`() {
        val str = "clickable@true"
        val bundle = bySelectorBundleFromString(str)
        println("🔥 bundle ${str} => ${JavaSelectorUtils.bySelectorBundleFromString(str)}")
        println("❄️ bundle ${str} => ${bundle}")
        assertEquals(str, bundle.selectorStr)
        assertNotNull(bundle.selector)
        println(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString_selector_with_spaces_and_special_chars`() {
        val selector = "desc@something with spaces and \\@ special chars"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
        println(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString selector re with spaces and special chars`() {
        val selector = "desc@\$something.* \\@ special chars"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
        println(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString selector clickable false and desc re with spaces and special chars`() {
        val selector = "clickable@false,desc@\$something.* \\@ special chars"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
        println(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString_selector_with_spaces_and_special_chars_followed_by_another`() {
        val selector = "desc@something with spaces and \\@ special chars,depth@3"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
        println(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString_complex_selector`() {
        val selector =
            "clickable@true,depth@3,desc@something with spaces and \\@ special chars,text@$.*"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
        println(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString_selector_clazz_re_checkable`() {
        val selector = "clazz@$.*dtm.*,checkable@true"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString_selector_checkable_clazz_re`() {
        val selector = "checkable@false,clazz@$.*dtm\\..*"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString_selector_clazz_checkable`() {
        val selector = "clazz@dtm.,checkable@true"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString selector checkable clazz`() {
        val selector = "checkable@false,clazz@dtm\\."
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString selector package res re scrollable clickable`() {
        val selector = "package@\$dtm.*,res@id=1,scrollable@false,clickable@true"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString selector package res scrollable clickable`() {
        val selector = "package@dtm.*,res@\$id=1,scrollable@true,clickable@true"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString selector depth`() {
        val selector = "depth@1"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString selector res`() {
        val selector = "res@something"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun `test bySelectorBundleFromString selector scrollable`() {
        val selector = "scrollable@false"
        val bundle = bySelectorBundleFromString(selector)
        println("🔥 bundle ${selector} => ${JavaSelectorUtils.bySelectorBundleFromString(selector)}")
        println("❄️ bundle ${selector} => ${bundle}")
        assertEquals(selector, bundle.selectorStr)
        assertNotNull(bundle.selector)
    }

    @Test
    fun eventConditionFromString() {
    }
}