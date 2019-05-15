package com.theone.cachemanager.cache

import android.graphics.Bitmap
import android.support.v4.util.LruCache
import com.theone.cachemanager.ext.DEFAULT_LIFE_TIME
import java.util.concurrent.ConcurrentHashMap

/**
 * 带有过期时间的LruCache
 *
 * @author   theone
 * @version  v1.0.0
 * @since    2018/9/20.
 */
open class TimedLruCache<K, V> @JvmOverloads constructor(maxSize: Int = 1024) {

    private val lruCache: LruCache<K, V>
    private var mLifeTimeMap: MutableMap<K, Long> = ConcurrentHashMap()

    init {
        lruCache = object : LruCache<K, V>(maxSize) {
            override fun sizeOf(key: K, value: V): Int {
                return this@TimedLruCache.sizeOf(key, value)
            }

            override fun entryRemoved(evicted: Boolean, key: K, oldValue: V, newValue: V?) {
                this@TimedLruCache.entryRemoved(evicted, key, oldValue, newValue)
            }
        }
    }

    /**
     * 保存数据
     * @param key
     * @param value
     * @param lifeTime 有效时间
     */
    @Synchronized
    fun put(key: K, value: V, lifeTime: Long = DEFAULT_LIFE_TIME): V? {
        if (key != null && value != null) {
            val oldValue: V? = lruCache.put(key, value)
            mLifeTimeMap[key] =
                    if (lifeTime == DEFAULT_LIFE_TIME) DEFAULT_LIFE_TIME else System.currentTimeMillis() + lifeTime
            return oldValue
        }
        return null
    }

    /**
     * 获取数据
     *
     * @param key
     */
    @Synchronized
    fun get(key: K): V? {
        if (key == null || !mLifeTimeMap.containsKey(key)) {
            return null
        }
        val expirationTime: Long? = mLifeTimeMap[key]
        return if (expirationTime!! == DEFAULT_LIFE_TIME ||
                System.currentTimeMillis() < expirationTime) {
            lruCache[key]
        } else {
            remove(key)
            null
        }
    }

    /**
     * 移除数据
     *
     * @param key
     */
    @Synchronized
    fun remove(key: K) {
        if (key != null) {
            lruCache.remove(key)
            mLifeTimeMap.remove(key)
        }
    }

    /**
     * 清除所有数据
     */
    @Synchronized
    fun evictAll() {
        lruCache.evictAll()
        mLifeTimeMap.clear()
    }

    /**
     * Returns the size of the entry for {@code key} and {@code value} in
     * user-defined units.  The default implementation returns 1 so that size
     * is the number of entries and max size is the maximum number of entries.
     *
     * <p>An entry's size must not change while it is in the cache.
     */
    open fun sizeOf(key: K, value: V): Int {
        return 1
    }

    /**
     * Called for entries that have been evicted or removed. This method is
     * invoked when a value is evicted to make space, removed by a call to
     * {@link #remove}, or replaced by a call to {@link #put}. The default
     * implementation does nothing.
     *
     * <p>The method is called without synchronization: other threads may
     * access the cache while this method is executing.
     *
     * @param evicted true if the entry is being removed to make space, false
     *     if the removal was caused by a {@link #put} or {@link #remove}.
     * @param newValue the new value for {@code key}, if it exists. If non-null,
     *     this removal was caused by a {@link #put}. Otherwise it was caused by
     *     an eviction or a {@link #remove}.
     */
    open fun entryRemoved(evicted: Boolean, key: K, oldValue: V, newValue: V?) {
        //释放空间导致的删除将 lifeTimeMap内一并移除
        if (evicted && mLifeTimeMap.containsKey(key)) {
            mLifeTimeMap.remove(key)
        }

        if (oldValue is Bitmap) {
            oldValue.recycle()
        }
    }

    /**
     * 缓存大小
     */
    fun size(): Int {
        return lruCache.size()
    }

    /**
     * 最大缓存大小
     */
    fun maxSize(): Int {
        return lruCache.maxSize()
    }
}