package com.dtmilano.android.culebratester2

import java.util.*

/**
 * Created by diego on 2016-02-26.
 */
class ObjectStore private constructor() {

    // do nothing
    private val nextOid: Int
        get() {
            var lastOid = 0
            try {
                lastOid = objectMap.lastKey()
            } catch (ex: NoSuchElementException) {
            }

            return lastOid + 1
        }

    fun size(): Int {
        return objectMap.size
    }

    fun lastKey(): Int? {
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
        val oid = nextOid
        put(oid, obj)
        return oid
    }

    private fun put(oid: Int, obj: Any) {
        objectMap[oid] = obj
    }

    companion object {
        val instance = ObjectStore()

        private val objectMap = TreeMap<Int, Any>()
    }

}
