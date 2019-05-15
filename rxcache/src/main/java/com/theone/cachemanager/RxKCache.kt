package com.theone.cachemanager

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.theone.cachemanager.cache.Cache
import com.theone.cachemanager.ext.DEFAULT_LIFE_TIME
import io.reactivex.Observable
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.lang.Exception
import java.util.*

/**
 * @author   theone
 * @version  v1.0.0
 * @since    2018/9/28.
 */
class RxKCache @JvmOverloads internal constructor(cache: Cache = com.theone.cachemanager.CacheManager.getCache()) {

    private val mCacheManager = cache
    private val mEncryptPwd = mCacheManager.encryptPassword

    /*
      --------- putXXX() method -----------
     */

    /**
     * 缓存JSONObject对象
     *
     * @param key key
     * @param jsonObject JSONObject
     * @param lifeTime 缓存时长
     */
    fun putJsonObj(key: String, jsonObject: JSONObject, lifeTime: Long = DEFAULT_LIFE_TIME)
            : Observable<JSONObject> {
        return putJsonObj(key, jsonObject, mEncryptPwd, lifeTime)
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
    fun putJsonObj(key: String, jsonObject: JSONObject, password: String = mEncryptPwd,
                   lifeTime: Long = DEFAULT_LIFE_TIME): Observable<JSONObject> {
        return toObservable(jsonObject) {
            it.putJsonObj(key, jsonObject, password, lifeTime)
        }
    }

    /**
     * 缓存JSONArray对象
     *
     * @param key key
     * @param jsonArray JSONArray
     * @param lifeTime 缓存时长
     */
    fun putJsonArray(key: String, jsonArray: JSONArray, lifeTime: Long = DEFAULT_LIFE_TIME)
            : Observable<JSONArray> {
        return putJsonArray(key, jsonArray, mEncryptPwd, lifeTime)
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
    fun putJsonArray(key: String, jsonArray: JSONArray, password: String = mEncryptPwd,
                     lifeTime: Long = DEFAULT_LIFE_TIME): Observable<JSONArray> {
        return toObservable(jsonArray) {
            it.putJsonArray(key, jsonArray, password, lifeTime)
        }
    }

    /**
     * 缓存Bitmap对象
     *
     * @param key key
     * @param bitmap Bitmap
     * @param lifeTime 缓存时长
     */
    fun putBitmap(key: String, bitmap: Bitmap, lifeTime: Long = DEFAULT_LIFE_TIME)
            : Observable<Bitmap> {
        return putBitmap(key, bitmap, mEncryptPwd, lifeTime)
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
    fun putBitmap(key: String, bitmap: Bitmap, password: String = mEncryptPwd,
                  lifeTime: Long = DEFAULT_LIFE_TIME): Observable<Bitmap> {
        return toObservable(bitmap) {
            it.putBitmap(key, bitmap, password, lifeTime)
        }
    }

    /**
     * 缓存Drawable对象
     *
     * @param key key
     * @param drawable Drawable
     * @param lifeTime 缓存时长
     */
    fun putDrawable(key: String, drawable: Drawable, lifeTime: Long = DEFAULT_LIFE_TIME)
            : Observable<Drawable> {
        return putDrawable(key, drawable, mEncryptPwd, lifeTime)
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
    fun putDrawable(key: String, drawable: Drawable, password: String = mEncryptPwd,
                    lifeTime: Long = DEFAULT_LIFE_TIME): Observable<Drawable> {
        return toObservable(drawable) {
            it.putDrawable(key, drawable, password, lifeTime)
        }
    }

    /**
     * 缓存String对象
     *
     * @param key key
     * @param string String
     * @param lifeTime 缓存时长
     */
    fun putString(key: String, string: String, lifeTime: Long = DEFAULT_LIFE_TIME)
            : Observable<String> {
        return putString(key, string, mEncryptPwd, lifeTime)
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
    fun putString(key: String, string: String, password: String = mEncryptPwd,
                  lifeTime: Long = DEFAULT_LIFE_TIME): Observable<String> {
        return toObservable(string) {
            it.putString(key, string, password, lifeTime)
        }
    }

    /**
     * 缓存ByteArray
     *
     * @param key key
     * @param byteArray ByteArray
     * @param lifeTime 缓存时长
     */
    fun putByteArray(key: String, byteArray: ByteArray, lifeTime: Long = DEFAULT_LIFE_TIME)
            : Observable<ByteArray> {
        return putByteArray(key, byteArray, mEncryptPwd, lifeTime)
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
    fun putByteArray(key: String, byteArray: ByteArray, password: String = mEncryptPwd,
                     lifeTime: Long = DEFAULT_LIFE_TIME): Observable<ByteArray> {
        return toObservable(byteArray) {
            it.putByteArray(key, byteArray, password, lifeTime)
        }
    }

    /**
     * 缓存Serializable对象
     *
     * @param key key
     * @param serializable Serializable
     * @param lifeTime 缓存时长
     */
    fun putSerializable(key: String, serializable: Serializable, lifeTime: Long = DEFAULT_LIFE_TIME)
            : Observable<Serializable> {
        return putSerializable(key, serializable, mEncryptPwd, lifeTime)
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
    fun putSerializable(key: String, serializable: Serializable, password: String = mEncryptPwd,
                        lifeTime: Long = DEFAULT_LIFE_TIME): Observable<Serializable> {
        return toObservable(serializable) {
            it.putSerializable(key, serializable, password, lifeTime)
        }
    }

    /*
      --------- getXXX() method -----------
     */

    /**
     * 从缓存中读取JSONObject
     *
     * @param key key
     * @param password 加密密码
     * @return JSONObject
     */
    fun getJsonObj(key: String, password: String = mEncryptPwd): Observable<JSONObject> {
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
                   password: String = mEncryptPwd): Observable<JSONObject> {
        return toObservable {
            it.getJsonObj(key, defaultValue, password)
        }
    }

    /**
     * 从缓存中读取JSONArray
     *
     * @param key key
     * @param password 加密的密码
     * @return JSONArray object
     */
    fun getJsonArray(key: String, password: String = mEncryptPwd): Observable<JSONArray> {
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
                     password: String = mEncryptPwd): Observable<JSONArray> {
        return toObservable {
            it.getJsonArray(key, defaultValue, password)
        }
    }

    /**
     * 从缓存中读取Bitmap对象
     *
     * @param key key
     * @param password 加密密码
     * @return Bitmap object
     */
    fun getBitmap(key: String, password: String = mEncryptPwd): Observable<Bitmap> {
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
                  password: String = mEncryptPwd): Observable<Bitmap> {
        return toObservable {
            it.getBitmap(key, defaultValue, password)
        }
    }

    /**
     * 从缓存中读取Drawable对象
     *
     * @param key key
     * @param password 加密密码
     * @return Drawable Object
     */
    fun getDrawable(key: String, password: String = mEncryptPwd): Observable<Drawable> {
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
                    password: String = mEncryptPwd): Observable<Drawable> {
        return toObservable {
            it.getDrawable(key, defaultValue, password)
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
    fun getString(key: String, defaultValue: String = "",
                  password: String = mEncryptPwd): Observable<String> {
        return toObservable {
            it.getString(key, defaultValue, password)
        }
    }

    /**
     * 从缓存中读取ByteArray
     *
     * @param key key
     * @param password 加密密码
     * @return ByteArray
     */
    fun getByteArray(key: String, password: String = mEncryptPwd): Observable<ByteArray> {
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
                     password: String = mEncryptPwd): Observable<ByteArray> {
        return toObservable {
            it.getByteArray(key, defaultValue, password)
        }
    }

    /**
     * 从缓存中读取Serializable对象
     *
     * @param key key
     * @param password 加密密码
     * @return Serializable object
     */
    fun <T : Any> getSerializable(key: String, password: String = mEncryptPwd):
            Observable<T> {
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
    fun <T : Any> getSerializable(key: String, defaultValue: T? = null,
                                  password: String = mEncryptPwd): Observable<T> {
        return toObservable {
            it.getSerializable(key, defaultValue, password)
        }
    }

    /**
     * get时候调用返回Observable<T>
     */
    private fun <T : Any> toObservable(block: (Cache) -> T?): Observable<T> {
        return Observable.create<T> {
            val value = block(mCacheManager)
            if (value!= null){
                it.onNext(value)
            } else{
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