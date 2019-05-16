package com.theone.cache.cache

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.theone.cache.ext.DEFAULT_LIFE_TIME
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019-05-16
 * @Description 轻量的二级缓存，Bitmap Drawable对象不会缓存到内存中
 */
class Cache internal constructor(private val mMemoryCache: MemoryCache, private val mDiskCache: DiskCache) {

    /**
     * 从缓存中读取JSONObject
     *
     * @param key key
     * @param defaultValue default value
     * @return JSONObject
     */
    @JvmOverloads
    fun getJsonObj(key: String, defaultValue: JSONObject? = JSONObject()): JSONObject? {
        val value = mMemoryCache.get(key, defaultValue)
        if (value != null) {
            return value
        }
        return mDiskCache.getJsonObj(key, defaultValue)
    }

    /**
     * 缓存JSONObject对象
     *
     * @param key key
     * @param jsonObject JSONObject
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putJsonObj(key: String, jsonObject: JSONObject,
                   lifeTime: Long = DEFAULT_LIFE_TIME
    ) {
        mMemoryCache.put(key, jsonObject, lifeTime)
        mDiskCache.putJsonObj(key, jsonObject, lifeTime)
    }

    /**
     * 从缓存中读取JSONArray
     *
     * @param key key
     * @param defaultValue default value
     * @return JSONArray object
     */
    @JvmOverloads
    fun getJsonArray(key: String, defaultValue: JSONArray? = JSONArray()): JSONArray? {
        val value = mMemoryCache.get<JSONArray>(key)
        if (value != null) {
            return value
        }
        return mDiskCache.getJsonArray(key, defaultValue)
    }

    /**
     * 缓存JSONArray对象
     *
     * @param key key
     * @param jsonArray JSONArray
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putJsonArray(key: String, jsonArray: JSONArray,
                     lifeTime: Long = DEFAULT_LIFE_TIME
    ) {
        mMemoryCache.put(key, jsonArray, lifeTime)
        mDiskCache.putJsonArray(key, jsonArray, lifeTime)
    }

    /**
     * 从缓存中读取Bitmap对象
     *
     * @param key key
     * @param defaultValue default value
     * @return Bitmap object
     */
    @JvmOverloads
    fun getBitmap(key: String, defaultValue: Bitmap? = null): Bitmap? {
        if (mMemoryCache.mSizeMode == MemoryCache.SizeMode.Size) {
            val value = mMemoryCache.get<Bitmap>(key)
            if (value != null) {
                return value
            }
        } else {
            //mode 改变，将已有的bitmap移除
            mMemoryCache.remove(key)
        }
        return mDiskCache.getBitmap(key, defaultValue)
    }

    /**
     * 缓存Bitmap对象
     *
     * @param key key
     * @param bitmap Bitmap
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putBitmap(key: String, bitmap: Bitmap,
                  lifeTime: Long = DEFAULT_LIFE_TIME
    ) {
        if (mMemoryCache.mSizeMode == MemoryCache.SizeMode.Size) {
            mMemoryCache.put(key, bitmap, lifeTime)
        }
        mDiskCache.putBitmap(key, bitmap, lifeTime)
    }

    /**
     * 从缓存中读取Drawable对象
     *
     * @param key key
     * @param defaultValue default value
     * @return Drawable Object
     */
    @JvmOverloads
    fun getDrawable(key: String, defaultValue: Drawable? = null): Drawable? {
        if (mMemoryCache.mSizeMode == MemoryCache.SizeMode.Size) {
            val value = mMemoryCache.get<Drawable>(key)
            if (value != null) {
                return value
            }
        } else {
            mMemoryCache.remove(key)
        }
        return mDiskCache.getDrawable(key, defaultValue)
    }

    /**
     * 缓存Drawable对象
     *
     * @param key key
     * @param drawable Drawable
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putDrawable(key: String, drawable: Drawable,
                    lifeTime: Long = DEFAULT_LIFE_TIME
    ) {
        if (mMemoryCache.mSizeMode == MemoryCache.SizeMode.Size) {
            mMemoryCache.put(key, drawable, lifeTime)
        }
        mDiskCache.putDrawable(key, drawable, lifeTime)
    }

    /**
     * 从缓存中读取String对象
     *
     * @param key key
     * @param defaultValue default value
     * @return String object
     */
    @JvmOverloads
    fun getString(key: String, defaultValue: String = ""): String {
        val value = mMemoryCache.get<String>(key)
        if (value != null) {
            return value
        }
        return mDiskCache.getString(key, defaultValue)
    }

    /**
     * 缓存String对象
     *
     * @param key key
     * @param string String
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putString(key: String, string: String,
                  lifeTime: Long = DEFAULT_LIFE_TIME
    ) {
        mMemoryCache.put(key, string, lifeTime)
        mDiskCache.putString(key, string, lifeTime)
    }

    /**
     * 从缓存中读取ByteArray
     *
     * @param key key
     * @param defaultValue default value
     * @return ByteArray
     */
    @JvmOverloads
    fun getByteArray(key: String, defaultValue: ByteArray? = null): ByteArray? {
        val value = mMemoryCache.get<ByteArray>(key)
        if (value != null) {
            return value
        }
        return mDiskCache.getByteArray(key, defaultValue)
    }


    /**
     * 缓存ByteArray
     *
     * @param key key
     * @param byteArray ByteArray
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putByteArray(key: String, byteArray: ByteArray,
                     lifeTime: Long = DEFAULT_LIFE_TIME
    ) {
        mMemoryCache.put(key, byteArray, lifeTime)
        mDiskCache.putByteArray(key, byteArray, lifeTime)
    }

    /**
     * 从缓存中读取Serializable对象
     *
     * @param key key
     * @param defaultValue default value
     * @return Serializable object
     */
    @JvmOverloads
    fun <T> getSerializable(key: String, defaultValue: T? = null): T? {
        val value = mMemoryCache.get<T>(key)
        if (value != null) {
            return value
        }
        return mDiskCache.getSerializable(key, defaultValue)
    }


    /**
     * 缓存Serializable对象
     *
     * @param key key
     * @param serializable Serializable
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putSerializable(key: String, serializable: Serializable,
                        lifeTime: Long = DEFAULT_LIFE_TIME
    ) {
        mMemoryCache.put(key, serializable, lifeTime)
        mDiskCache.putSerializable(key, serializable, lifeTime)
    }

    /**
     * 移除指定key的所有数据数据
     */
    fun remove(key: String) {
        removeFromMemory(key)
        removeFromDisk(key)
    }

    /**
     * 移除指定key的内存数据
     */
    fun removeFromMemory(key: String) {
        mMemoryCache.remove(key)
    }

    /**
     * 移除指定key的磁盘数据
     */
    fun removeFromDisk(key: String) {
        mDiskCache.remove(key)
    }

    /**
     * 内存已使用缓存大小
     */
    fun memorySize(): Int {
        return mMemoryCache.size()
    }

    /**
     * 文件已使用缓存大小
     */
    fun diskSize(): Long {
        return mDiskCache.size()
    }

    /**
     * 内存缓存最大缓存大小
     */
    fun memoryMaxSize(): Int {
        return mMemoryCache.maxSize()
    }

    /**
     * 文件缓存最大缓存大小
     */
    fun diskMaxSize(): Long {
        return mDiskCache.maxSize()
    }

    /**
     * 删除所有缓存
     */
    fun evictAll() {
        evictMemoryAll()
        evictDiskAll()
    }

    /**
     * 删除所有内存缓存
     */
    fun evictMemoryAll() {
        mMemoryCache.evictAll()
    }

    /**
     * 删除所有磁盘缓存
     */
    fun evictDiskAll() {
        mDiskCache.evictAll()
    }
}
