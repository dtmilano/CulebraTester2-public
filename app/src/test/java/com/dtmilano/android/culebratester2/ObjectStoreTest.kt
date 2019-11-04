package com.dtmilano.android.culebratester2

import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class ObjectStoreTest {

    @Before
    fun setUp() {
        ObjectStore.instance.clear()
    }

    @After
    fun tearDown() {
        ObjectStore.instance.clear()
    }

    @Test
    fun size() {
        assertThat(ObjectStore.instance.size(), `is`(0))
        ObjectStore.instance.put("S")
        assertThat(ObjectStore.instance.size(), `is`(1))
    }

    @Test
    fun lastKey() {
        try {
            ObjectStore.instance.lastKey()
            fail("No exception")
        } catch (e: NoSuchElementException) {
            // do nothing
        }

        val oid = ObjectStore.instance.put("A")
        assertThat(ObjectStore.instance.lastKey(), `is`(oid))
    }

    @Test
    fun get() {
        val obj = "G"
        val oid = ObjectStore.instance.put(obj)
        assertThat(ObjectStore.instance[oid] as String, `is`(obj))
    }

    @Test
    fun remove() {
        val obj = "R"
        val oid1 = ObjectStore.instance.put(obj)
        val oid2 = ObjectStore.instance.put("Q")
        assertThat(ObjectStore.instance.size(), `is`(2))
        ObjectStore.instance.remove(oid2)
        assertThat(ObjectStore.instance.size(), `is`(1))
        assertThat(ObjectStore.instance[oid1] as String, `is`(obj))
    }

    @Test
    fun list() {
        val n = 5
        generateSequence(1) { it + 1 }
            .take(n)
            .toList()
            .map(ObjectStore.instance::put)
        val sm = ObjectStore.instance.list()
        assertThat(sm.size, `is`(n))
    }

    @Test
    fun put() {
        assertThat(ObjectStore.instance.size(), `is`(0))
        val oid1 = ObjectStore.instance.put("P")
        assertThat(ObjectStore.instance.size(), `is`(1))
        val oid2 = ObjectStore.instance.put("Q")
        assertThat(ObjectStore.instance.size(), `is`(2))
        assertThat(ObjectStore.instance[oid1] as String, `is`("P"))
        assertThat(ObjectStore.instance[oid2] as String, `is`("Q"))
    }
}