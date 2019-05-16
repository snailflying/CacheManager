package com.theone.rxcache

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.theone.cache.CacheManager
import com.theone.cache.cache.Cache
import com.theone.cache.ext.DEFAULT_LIFE_TIME
import io.reactivex.Observable
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019-05-16
 * @Description
 */
class RxCacheManager @JvmOverloads internal constructor(cache: Cache = CacheManager.getCache()) {

    private val mCacheManager = cache

    /*
      --------- putXXX() method -----------
     */

    /**
     * 缓存JSONObject对象
     *
     * @param key key
     * @param jsonObject JSONObject
     * @param password 加密密码
     * @param lifeTime 缓存时长
     */
    @JvmOverloads
    fun putJsonObj(
        key: String, jsonObject: JSONObject,
        lifeTime: Long = DEFAULT_LIFE_TIME
    ): Observable<JSONObject> {
        return toObservable(jsonObject) {
            it.putJsonObj(key, jsonObject, lifeTime)
        }
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
    fun putJsonArray(
        key: String, jsonArray: JSONArray,
        lifeTime: Long = DEFAULT_LIFE_TIME
    ): Observable<JSONArray> {
        return toObservable(jsonArray) {
            it.putJsonArray(key, jsonArray, lifeTime)
        }
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
    fun putBitmap(
        key: String, bitmap: Bitmap,
        lifeTime: Long = DEFAULT_LIFE_TIME
    ): Observable<Bitmap> {
        return toObservable(bitmap) {
            it.putBitmap(key, bitmap, lifeTime)
        }
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
    fun putDrawable(
        key: String, drawable: Drawable,
        lifeTime: Long = DEFAULT_LIFE_TIME
    ): Observable<Drawable> {
        return toObservable(drawable) {
            it.putDrawable(key, drawable, lifeTime)
        }
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
    fun putString(
        key: String, string: String,
        lifeTime: Long = DEFAULT_LIFE_TIME
    ): Observable<String> {
        return toObservable(string) {
            it.putString(key, string, lifeTime)
        }
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
    fun putByteArray(
        key: String, byteArray: ByteArray,
        lifeTime: Long = DEFAULT_LIFE_TIME
    ): Observable<ByteArray> {
        return toObservable(byteArray) {
            it.putByteArray(key, byteArray, lifeTime)
        }
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
    fun putSerializable(
        key: String, serializable: Serializable,
        lifeTime: Long = DEFAULT_LIFE_TIME
    ): Observable<Serializable> {
        return toObservable(serializable) {
            it.putSerializable(key, serializable, lifeTime)
        }
    }

    /*
      --------- getXXX() method -----------
     */


    /**
     * 从缓存中读取JSONObject
     *
     * @param key key
     * @param defaultValue default value
     * @param password 加密密码
     * @return JSONObject
     */
    @JvmOverloads
    fun getJsonObj(
        key: String, defaultValue: JSONObject? = JSONObject()
    ): Observable<JSONObject> {
        return toObservable {
            it.getJsonObj(key, defaultValue)
        }
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
    fun getJsonArray(
        key: String, defaultValue: JSONArray? = JSONArray()
    ): Observable<JSONArray> {
        return toObservable {
            it.getJsonArray(key, defaultValue)
        }
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
    fun getBitmap(
        key: String, defaultValue: Bitmap? = null
    ): Observable<Bitmap> {
        return toObservable {
            it.getBitmap(key, defaultValue)
        }
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
    fun getDrawable(
        key: String, defaultValue: Drawable? = null
    ): Observable<Drawable> {
        return toObservable {
            it.getDrawable(key, defaultValue)
        }
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
    fun getString(
        key: String, defaultValue: String = ""
    ): Observable<String> {
        return toObservable {
            it.getString(key, defaultValue)
        }
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
    fun getByteArray(
        key: String, defaultValue: ByteArray? = null
    ): Observable<ByteArray> {
        return toObservable {
            it.getByteArray(key, defaultValue)
        }
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
    fun <T : Any> getSerializable(
        key: String, defaultValue: T? = null
    ): Observable<T> {
        return toObservable {
            it.getSerializable(key, defaultValue)
        }
    }

    /**
     * get时候调用返回Observable<T>
     */
    private fun <T : Any> toObservable(block: (Cache) -> T?): Observable<T> {
        return Observable.create<T> {
            val value = block(mCacheManager)
            if (value != null) {
                it.onNext(value)
            } else {
                it.onError(Exception("result is null"))
            }
            it.onComplete()
        }
    }

    /**
     * put时候调用返回Observable<T>
     */
    private fun <T> toObservable(emitterValue: T, block: (Cache) -> Unit): Observable<T> {
        return Observable.create {
            block(mCacheManager)
            it.onNext(emitterValue)
        }
    }
}