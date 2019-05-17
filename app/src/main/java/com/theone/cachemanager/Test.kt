package com.theone.cachemanager

import java.io.Serializable

/**
 * @author haohao on 2017/6/23 11:39
 * @version v1.0
 */
class Test internal constructor(private val index: Int, private val name: String):Serializable {

    override fun toString(): String {
        return "{index:$index   name:$name}"
    }
}
