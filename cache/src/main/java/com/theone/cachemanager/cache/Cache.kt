package com.theone.cachemanager.cache

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.theone.cachemanager.ext.DEFAULT_LIFE_TIME
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

/**
 * 轻量的二级缓存，Bitmap Drawable对象不会缓存到内存中
 *
 * @author   theone
 * @version  v1.0.0
 * @since    2018/9/21.
 */
class Cache internal constructor(memoryCache: MemoryCache, diskCache: DiskCache,
                                 encryptPwd: String = "") {

    /**
     * [encryptPassword]有值 则所有文件都加密
     */
    val encryptPassword: String = encryptPwd

    /**
     * 懒加载文件缓存对象 [mDiskCache]
     */
    private val mDiskCache: DiskCache = diskCache
    private val mMemoryCache = memoryCache

    /**
     * 从缓存中读取JSONObject
     *
     * @param key key
     * @param password 加密密码
     * @return JSONObject
     */
    fun getJsonObj(key: String, password: String = encryptPassword): JSONObject? {
        return getJsonObj(key, null, password)
    }

    /**
     * 从缓存中读取JSONObject
     *
     * @param key key
     * @param defaultValue default value
     * @param password 加密密码
     * @return JSONObject
     */
    @JvmOverloads
    fun getJsonObj(key: String, defaultValue: JSONObject? = JSONObject(),
                   password: String = encryptPassword): JSONObject? {
        val value = mMemoryCache.get(key, defaultValue)
        if (value != null) {
            return value
        }
        return mDiskCache.getJsonObj(key, defaultValue, password)
    }

    /**
     * 缓存JSONObject对象
     *
     * @param key key
     * @param jsonObject JSONObject
     * @param lifeTime 缓存时长
     */
    fun putJsonObj(key: String, jsonObject: JSONObject, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putJsonObj(key, jsonObject, encryptPassword, lifeTime)
    }

    /**
     * 缓存JSONObject对象
     *
     * @param key key
     * @param jsonObject JSONObject
     * @param password 加密密码
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putJsonObj(key: String, jsonObject: JSONObject, password: String = encryptPassword,
                   lifeTime: Long = DEFAULT_LIFE_TIME) {
        mMemoryCache.put(key, jsonObject, lifeTime)
        mDiskCache.putJsonObj(key, jsonObject, password, lifeTime)
    }

    /**
     * 从缓存中读取JSONArray
     *
     * @param key key
     * @param password 加密的密码
     * @return JSONArray object
     */
    fun getJsonArray(key: String, password: String = encryptPassword): JSONArray? {
        return getJsonArray(key, null, password)
    }

    /**
     * 从缓存中读取JSONArray
     *
     * @param key key
     * @param defaultValue default value
     * @param password 加密的密码
     * @return JSONArray object
     */
    @JvmOverloads
    fun getJsonArray(key: String, defaultValue: JSONArray? = JSONArray(),
                     password: String = encryptPassword): JSONArray? {
        val value = mMemoryCache.get<JSONArray>(key)
        if (value != null) {
            return value
        }
        return mDiskCache.getJsonArray(key, defaultValue, password)
    }

    /**
     * 缓存JSONArray对象
     *
     * @param key key
     * @param jsonArray JSONArray
     * @param lifeTime 缓存时长
     */
    fun putJsonArray(key: String, jsonArray: JSONArray, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putJsonArray(key, jsonArray, encryptPassword, lifeTime)
    }

    /**
     * 缓存JSONArray对象
     *
     * @param key key
     * @param jsonArray JSONArray
     * @param password 加密密码
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putJsonArray(key: String, jsonArray: JSONArray, password: String = encryptPassword,
                     lifeTime: Long = DEFAULT_LIFE_TIME) {
        mMemoryCache.put(key, jsonArray, lifeTime)
        mDiskCache.putJsonArray(key, jsonArray, password, lifeTime)
    }

    /**
     * 从缓存中读取Bitmap对象
     *
     * @param key key
     * @param password 加密密码
     * @return Bitmap object
     */
    fun getBitmap(key: String, password: String = encryptPassword): Bitmap? {
        return getBitmap(key, null, password)
    }

    /**
     * 从缓存中读取Bitmap对象
     *
     * @param key key
     * @param defaultValue default value
     * @param password 加密密码
     * @return Bitmap object
     */
    @JvmOverloads
    fun getBitmap(key: String, defaultValue: Bitmap? = null,
                  password: String = encryptPassword): Bitmap? {
        if (mMemoryCache.mSizeMode == MemoryCache.SizeMode.Size) {
            val value = mMemoryCache.get<Bitmap>(key)
            if (value != null) {
                return value
            }
        } else {
            //mode 改变，将已有的bitmap移除
            mMemoryCache.remove(key)
        }
        return mDiskCache.getBitmap(key, defaultValue, password)
    }

    /**
     * 缓存Bitmap对象
     *
     * @param key key
     * @param bitmap Bitmap
     * @param lifeTime 缓存时长
     */
    fun putBitmap(key: String, bitmap: Bitmap, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putBitmap(key, bitmap, encryptPassword, lifeTime)
    }

    /**
     * 缓存Bitmap对象
     *
     * @param key key
     * @param bitmap Bitmap
     * @param password 加密密码
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putBitmap(key: String, bitmap: Bitmap, password: String = encryptPassword,
                  lifeTime: Long = DEFAULT_LIFE_TIME) {
        if (mMemoryCache.mSizeMode == MemoryCache.SizeMode.Size) {
            mMemoryCache.put(key, bitmap, lifeTime)
        }
        mDiskCache.putBitmap(key, bitmap, password, lifeTime)
    }

    /**
     * 从缓存中读取Drawable对象
     *
     * @param key key
     * @param password 加密密码
     * @return Drawable Object
     */
    fun getDrawable(key: String, password: String = encryptPassword): Drawable? {
        return getDrawable(key, null, password)
    }

    /**
     * 从缓存中读取Drawable对象
     *
     * @param key key
     * @param defaultValue default value
     * @param password 加密密码
     * @return Drawable Object
     */
    @JvmOverloads
    fun getDrawable(key: String, defaultValue: Drawable? = null,
                    password: String = encryptPassword): Drawable? {
        if (mMemoryCache.mSizeMode == MemoryCache.SizeMode.Size) {
            val value = mMemoryCache.get<Drawable>(key)
            if (value != null) {
                return value
            }
        } else {
            mMemoryCache.remove(key)
        }
        return mDiskCache.getDrawable(key, defaultValue, password)
    }

    /**
     * 缓存Drawable对象
     *
     * @param key key
     * @param drawable Drawable
     * @param lifeTime 缓存时长
     */
    fun putDrawable(key: String, drawable: Drawable, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putDrawable(key, drawable, encryptPassword, lifeTime)
    }

    /**
     * 缓存Drawable对象
     *
     * @param key key
     * @param drawable Drawable
     * @param password 加密密码
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putDrawable(key: String, drawable: Drawable, password: String = encryptPassword,
                    lifeTime: Long = DEFAULT_LIFE_TIME) {
        if (mMemoryCache.mSizeMode == MemoryCache.SizeMode.Size) {
            mMemoryCache.put(key, drawable, lifeTime)
        }
        mDiskCache.putDrawable(key, drawable, password, lifeTime)
    }

    /**
     * 从缓存中读取String对象
     *
     * @param key key
     * @param defaultValue default value
     * @param password 加密密码
     * @return String object
     */
    @JvmOverloads
    fun getString(key: String, defaultValue: String = "",
                  password: String = encryptPassword): String {
        val value = mMemoryCache.get<String>(key)
        if (value != null) {
            return value
        }
        return mDiskCache.getString(key, defaultValue, password)
    }

    /**
     * 缓存String对象
     *
     * @param key key
     * @param string String
     * @param lifeTime 缓存时长
     */
    fun putString(key: String, string: String, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putString(key, string, encryptPassword, lifeTime)
    }

    /**
     * 缓存String对象
     *
     * @param key key
     * @param string String
     * @param password 加密密码
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putString(key: String, string: String, password: String = encryptPassword,
                  lifeTime: Long = DEFAULT_LIFE_TIME) {
        mMemoryCache.put(key, string, lifeTime)
        mDiskCache.putString(key, string, password, lifeTime)
    }

    /**
     * 从缓存中读取ByteArray
     *
     * @param key key
     * @param password 加密密码
     * @return ByteArray
     */
    fun getByteArray(key: String, password: String = encryptPassword): ByteArray? {
        return getByteArray(key, null, password)
    }

    /**
     * 从缓存中读取ByteArray
     *
     * @param key key
     * @param defaultValue default value
     * @param password 加密密码
     * @return ByteArray
     */
    @JvmOverloads
    fun getByteArray(key: String, defaultValue: ByteArray? = null,
                     password: String = encryptPassword): ByteArray? {
        val value = mMemoryCache.get<ByteArray>(key)
        if (value != null) {
            return value
        }
        return mDiskCache.getByteArray(key, defaultValue, password)
    }

    /**
     * 缓存ByteArray
     *
     * @param key key
     * @param byteArray ByteArray
     * @param lifeTime 缓存时长
     */
    fun putByteArray(key: String, byteArray: ByteArray, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putByteArray(key, byteArray, encryptPassword, lifeTime)
    }

    /**
     * 缓存ByteArray
     *
     * @param key key
     * @param byteArray ByteArray
     * @param password 加密密码
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putByteArray(key: String, byteArray: ByteArray, password: String = encryptPassword,
                     lifeTime: Long = DEFAULT_LIFE_TIME) {
        mMemoryCache.put(key, byteArray, lifeTime)
        mDiskCache.putByteArray(key, byteArray, password, lifeTime)
    }

    /**
     * 从缓存中读取Serializable对象
     *
     * @param key key
     * @param password 加密密码
     * @return Serializable object
     */
    fun <T> getSerializable(key: String, password: String = encryptPassword): T? {
        return getSerializable(key, null, password)
    }

    /**
     * 从缓存中读取Serializable对象
     *
     * @param key key
     * @param defaultValue default value
     * @param password 加密密码
     * @return Serializable object
     */
    @JvmOverloads
    fun <T> getSerializable(key: String, defaultValue: T? = null,
                            password: String = encryptPassword): T? {
        val value = mMemoryCache.get<T>(key)
        if (value != null) {
            return value
        }
        return mDiskCache.getSerializable(key, defaultValue, password)
    }

    /**
     * 缓存Serializable对象
     *
     * @param key key
     * @param serializable Serializable
     * @param lifeTime 缓存时长
     */
    fun putSerializable(key: String, serializable: Serializable, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putSerializable(key, serializable, encryptPassword, lifeTime)
    }

    /**
     * 缓存Serializable对象
     *
     * @param key key
     * @param serializable Serializable
     * @param password 加密密码
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putSerializable(key: String, serializable: Serializable, password: String = encryptPassword,
                        lifeTime: Long = DEFAULT_LIFE_TIME) {
        mMemoryCache.put(key, serializable, lifeTime)
        mDiskCache.putSerializable(key, serializable, password, lifeTime)
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
