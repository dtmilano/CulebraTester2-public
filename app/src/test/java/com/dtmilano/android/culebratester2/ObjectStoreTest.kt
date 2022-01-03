package com.dtmilano.android.culebratester2

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

class ObjectStoreTest {
    @Inject
    lateinit var objectStore: ObjectStore

    @Before
    fun setUp() {
        objectStore = DaggerApplicationComponent.create().objectStore()
        objectStore.clear()
    }

    @After
    fun tearDown() {
        objectStore.clear()
    }

    @Test
    fun size() {
        assertThat(objectStore.size(), `is`(0))
        objectStore.put("S")
        assertThat(objectStore.size(), `is`(1))
    }

    @Test
    fun lastKey() {
        try {
            objectStore.lastKey()
            fail("No exception")
        } catch (e: NoSuchElementException) {
            // do nothing
        }

        val oid = objectStore.put("A")
        assertThat(objectStore.lastKey(), `is`(oid))
    }

    @Test
    fun get() {
        val obj = "G"
        val oid = objectStore.put(obj)
        assertThat(objectStore[oid] as String, `is`(obj))
    }

    @Test
    fun remove() {
        val obj = "R"
        val oid1 = objectStore.put(obj)
        val oid2 = objectStore.put("Q")
        assertThat(objectStore.size(), `is`(2))
        objectStore.remove(oid2)
        assertThat(objectStore.size(), `is`(1))
        assertThat(objectStore[oid1] as String, `is`(obj))
    }

    @Test
    fun list() {
        val n = 5
        generateSequence(1) { it + 1 }
            .take(n)
            .toList()
            .map(objectStore::put)
        val sm = objectStore.list()
        assertThat(sm.size, `is`(n))
    }

    @Test
    fun put() {
        assertThat(objectStore.size(), `is`(0))
        val oid1 = objectStore.put("P")
        assertThat(objectStore.size(), `is`(1))
        val oid2 = objectStore.put("Q")
        assertThat(objectStore.size(), `is`(2))
        assertThat(objectStore[oid1] as String, `is`("P"))
        assertThat(objectStore[oid2] as String, `is`("Q"))
    }
}