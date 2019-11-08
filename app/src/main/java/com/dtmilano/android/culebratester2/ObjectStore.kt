package com.dtmilano.android.culebratester2

import dagger.Component
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by diego on 2016-02-26.
 */
@Singleton
//class ObjectStore private constructor() {
class ObjectStore @Inject constructor() {

    private val nextOid: Int
        get() = try {
            objectMap.lastKey()
        } catch (e: NoSuchElementException) {
            0
        } + 1

    fun size(): Int {
        return objectMap.size
    }

    fun lastKey(): Int {
        return objectMap.lastKey()
    }

    operator fun get(oid: Int): Any? {
        return objectMap[oid]
    }

    fun remove(oid: Int) {
        objectMap.remove(oid)
    }

    fun list(): SortedMap<Int, Any> {
        return objectMap
    }

    fun put(obj: Any): Int {
        nextOid.let { put(it, obj); return@put it }
    }

    fun clear() {
        objectMap.clear()
    }

    private fun put(oid: Int, obj: Any) {
        objectMap[oid] = obj
    }

    companion object {
        //val instance = ObjectStore()

        private val objectMap = TreeMap<Int, Any>()
    }

}
