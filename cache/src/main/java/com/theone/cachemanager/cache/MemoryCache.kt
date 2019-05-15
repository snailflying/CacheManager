package com.theone.cachemanager.cache

import com.theone.cachemanager.ext.DEFAULT_LIFE_TIME
import com.theone.cachemanager.ext.DEFAULT_MEMORY_MAX_SIZE
import com.theone.cachemanager.ext.logw
import com.theone.cachemanager.ext.sizeofAny

/**
 * @author   theone
 * @version  v1.0.0
 * @since    2018/9/27.
 */
class MemoryCache @JvmOverloads constructor(memoryMaxSize: Int = DEFAULT_MEMORY_MAX_SIZE
                                            , mode: SizeMode = SizeMode.Size) {

    //内存缓存模式
    var mSizeMode: SizeMode = mode
        set(value) {
            field = value
            //mode变化时清除内存缓存
            mLruCache.evictAll()
        }

    private val mLruCache: TimedLruCache<String, Any> by lazy {
        object : TimedLruCache<String, Any>(memoryMaxSize) {
            override fun sizeOf(key: String, value: Any): Int {
                return if (mSizeMode == SizeMode.Size) value.sizeofAny() else 1
            }
        }
    }

    fun <T> get(key: String): T? {
        return get(key, null)
    }

    /**
     * 从内存读取 [key] 的数据，如果为 null 返回 [defaultValue]
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, defaultValue: T): T? {
        try {
            val value = mLruCache.get(key)
            return if (value == null) defaultValue else value as T
        } catch (e: ClassCastException) {
            logw("CacheManager", e.toString())
        } catch (e: ClassNotFoundException) {
            logw("CacheManager", e.toString())
        }
        return null
    }

    /**
     * 保存数据
     *
     * @param key
     * @param value
     * @param lifeTime
     */
    @JvmOverloads
    fun put(key: String, value: Any, lifeTime: Long = DEFAULT_LIFE_TIME) {
        mLruCache.put(key, value, lifeTime)
    }

    /**
     * 移除数据
     *
     * @param key
     */
    fun remove(key: String) {
        mLruCache.remove(key)
    }

    /**
     * 清除所有数据
     */
    fun evictAll() {
        mLruCache.evictAll()
    }

    /**
     * 缓存大小
     */
    fun size(): Int {
        return mLruCache.size()
    }

    /**
     * 最大缓存大小
     */
    fun maxSize(): Int {
        return mLruCache.maxSize()
    }

    enum class SizeMode {
        Size, Count
    }

}