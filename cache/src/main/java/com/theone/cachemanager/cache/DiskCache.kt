package com.theone.cachemanager.cache

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.jakewharton.disklrucache.DiskLruCache
import com.theone.cachemanager.encrypt.ext.DEFAULT_AES_TRANSFORMATION
import com.theone.cachemanager.encrypt.ext.generateSecretKey
import com.theone.cachemanager.ext.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream


/**
 * 带过期时间和加/解密的DiskLruCache
 *
 * @author   theone
 * @version  v1.0.0
 * @since    2018/9/20.
 */
class DiskCache @JvmOverloads constructor(dictionary: File, appVersion: Int,
                                          maxSize: Long = DEFAULT_DISK_MAX_SIZE) {

    private val mTag: String = "DiskCache"
    private val diskLruCache: DiskLruCache = DiskLruCache.open(dictionary,
            appVersion, 2, maxSize)
    /**
     * [encryptPwd]有值 则所有文件都加密
     */
    private var encryptPwd: String = ""

    /**
     * 获取JSONObject类型数据
     *
     * @param key
     * @param password 加密Key
     */
    fun getJsonObj(key: String, password: String = encryptPwd): JSONObject? {
        return getJsonObj(key, null, password)
    }

    /**
     * 获取JSONObject类型数据
     *
     * @param key
     * @param password 加密Key
     */
    @JvmOverloads
    fun getJsonObj(key: String, defaultValue: JSONObject? = JSONObject(),
                   password: String = encryptPwd): JSONObject? {
        return try {
            JSONObject(getString(key, password))
        } catch (e: Throwable) {
            logw(mTag, e.toString())
            defaultValue
        }
    }

    /**
     * 保存JSONObject类型数据
     *
     * @param key
     * @param jsonObject
     * @param lifeTime 有效时间
     */
    fun putJsonObj(key: String, jsonObject: JSONObject, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putJsonObj(key, jsonObject, encryptPwd, lifeTime)
    }

    /**
     * 保存JSONObject类型数据
     *
     * @param key
     * @param jsonObject
     * @param password 加密密码
     * @param lifeTime 有效时间
     */
    @JvmOverloads
    fun putJsonObj(key: String, jsonObject: JSONObject, password: String = encryptPwd,
                   lifeTime: Long = DEFAULT_LIFE_TIME) {
        putString(key, jsonObject.toString(), password, lifeTime)
    }

    /**
     * 获取JSONArray类型数据
     *
     * @param key
     */
    fun getJsonArray(key: String, password: String = encryptPwd): JSONArray? {
        return getJsonArray(key, null, password)
    }

    /**
     * 获取JSONArray类型数据
     *
     * @param key
     */
    @JvmOverloads
    fun getJsonArray(key: String, defaultValue: JSONArray? = JSONArray(),
                     password: String = encryptPwd): JSONArray? {
        return try {
            JSONArray(getString(key, password))
        } catch (e: JSONException) {
            logw(mTag, e.toString())
            defaultValue
        }
    }

    /**
     * 保存JSONArray类型数据
     *
     * @param key
     * @param jsonArray
     * @param lifeTime 有效时间
     */
    fun putJsonArray(key: String, jsonArray: JSONArray, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putJsonArray(key, jsonArray, encryptPwd, lifeTime)
    }

    /**
     * 保存JSONArray类型数据
     *
     * @param key
     * @param jsonArray
     * @param password 加密密码
     * @param lifeTime 有效时间
     */
    @JvmOverloads
    fun putJsonArray(key: String, jsonArray: JSONArray, password: String = encryptPwd,
                     lifeTime: Long = DEFAULT_LIFE_TIME) {
        putString(key, jsonArray.toString(), password, lifeTime)
    }

    /**
     * 获取Bitmap类型数据
     *
     * @param key
     * @param password
     */
    fun getBitmap(key: String, password: String = encryptPwd): Bitmap? {
        return getBitmap(key, null, password)
    }

    /**
     * 获取Bitmap类型数据
     *
     * @param key
     * @param defaultValue default value
     * @param password
     */
    @JvmOverloads
    fun getBitmap(key: String, defaultValue: Bitmap? = null,
                  password: String = encryptPwd): Bitmap? {
        return getByteArray(key, defaultValue?.toByteArray(), password)?.toBitmap()
    }

    /**
     * 保存Bitmap类型数据
     *
     * @param key
     * @param bitmap
     * @param lifeTime 有效时间
     */
    fun putBitmap(key: String, bitmap: Bitmap, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putBitmap(key, bitmap, encryptPwd, lifeTime)
    }

    /**
     * 保存Bitmap类型数据
     *
     * @param key
     * @param bitmap
     * @param password 加密密码
     * @param lifeTime 有效时间
     */
    @JvmOverloads
    fun putBitmap(key: String, bitmap: Bitmap, password: String = encryptPwd,
                  lifeTime: Long = DEFAULT_LIFE_TIME) {
        putByteArray(key, bitmap.toByteArray(), password, lifeTime)
    }

    /**
     * 获取Drawable类型数据
     *
     * @param key
     */
    @JvmOverloads
    fun getDrawable(key: String, defaultValue: Drawable? = null, password: String = encryptPwd): Drawable? {
        return getBitmap(key, defaultValue?.toBitmap(), password)?.toDrawable()
    }

    /**
     * 保存Drawable类型数据
     *
     * @param key
     * @param drawable
     * @param lifeTime 有效时间
     */
    fun putDrawable(key: String, drawable: Drawable, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putDrawable(key, drawable, encryptPwd, lifeTime)
    }

    /**
     * 保存Drawable类型数据
     *
     * @param key
     * @param drawable
     * @param password 加密密码
     * @param lifeTime 有效时间
     */
    @JvmOverloads
    fun putDrawable(key: String, drawable: Drawable, password: String = encryptPwd,
                    lifeTime: Long = DEFAULT_LIFE_TIME) {
        putBitmap(key, drawable.toBitmap(), password, lifeTime)
    }

    /**
     * 获取String类型数据
     *
     * @param key
     * @param password
     */
    @JvmOverloads
    fun getString(key: String, defaultValue: String = "",
                  password: String = encryptPwd): String {
        try {
            val snapshot = get(key) ?: return defaultValue
            val lifeTime = snapshot.getLifeTime()
            if (lifeTime == -1L || System.currentTimeMillis() < lifeTime) {
                val inputStream = handleOrDecrypt(snapshot, password) ?: return defaultValue
                return inputStream.readTextAndClose()
            } else {
                //remove key
                remove(key)
                return defaultValue
            }
        } catch (e: IOException) {
            logw(mTag, e.toString())
            return defaultValue
        }
    }

    /**
     * 保存String类型数据
     *
     * @param key
     * @param string
     * @param lifeTime 有效时间
     */
    fun putString(key: String, string: String, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putString(key, string, encryptPwd, lifeTime)
    }

    /**
     * 保存String类型数据
     *
     * @param key
     * @param string
     * @param password 加密密码
     * @param lifeTime 有效时间
     */
    @JvmOverloads
    fun putString(key: String, string: String, password: String = encryptPwd,
                  lifeTime: Long = DEFAULT_LIFE_TIME) {
        try {
            val editor = diskLruCache.edit(key.md5()) ?: return
            val outputStream: OutputStream = handleOrEncrypt(editor, password) ?: return

            if (writeToString(string, outputStream)) {
                editor.setLifTime(lifeTime)
                        .commit()
            } else {
                editor.abort()
            }
        } catch (e: IOException) {
            logw(mTag, e.toString())
        }
    }

    /**
     * 获取ByteArray类型数据
     *
     * @param key
     * @param password
     */
    fun getByteArray(key: String, password: String = encryptPwd): ByteArray? {
        return getByteArray(key, null, password)
    }

    /**
     * 获取ByteArray类型数据
     *
     * @param key
     * @param defaultValue default valude
     * @param password
     */
    @JvmOverloads
    fun getByteArray(key: String, defaultValue: ByteArray? = null,
                     password: String = encryptPwd): ByteArray? {
        try {
            val snapshot = get(key) ?: return null
            val lifeTime = snapshot.getLifeTime()
            if (lifeTime == DEFAULT_LIFE_TIME || System.currentTimeMillis() < lifeTime) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                val inputStream = handleOrDecrypt(snapshot, password) ?: return defaultValue
                inputStream.use { input ->
                    byteArrayOutputStream.use {
                        input.copyTo(it, 512)
                    }
                }
                return byteArrayOutputStream.toByteArray()
            } else {
                remove(key)
                return defaultValue
            }
        } catch (e: IOException) {
            logw(mTag, e.toString())
            return defaultValue
        }
    }

    /**
     * 保存ByteArray类型数据
     *
     * @param key
     * @param byteArray
     * @param lifeTime 有效时间
     */
    fun putByteArray(key: String, byteArray: ByteArray, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putByteArray(key, byteArray, encryptPwd, lifeTime)
    }

    /**
     * 保存ByteArray类型数据
     *
     * @param key
     * @param byteArray
     * @param password 加密密码
     * @param lifeTime 有效时间
     */
    @JvmOverloads
    fun putByteArray(key: String, byteArray: ByteArray, password: String = encryptPwd,
                     lifeTime: Long = DEFAULT_LIFE_TIME) {
        try {
            val editor = diskLruCache.edit(key.md5()) ?: return
            val outputStream = handleOrEncrypt(editor, password) ?: return
            if (writeToBytes(byteArray, outputStream)) {
                editor.setLifTime(lifeTime)
                        .commit()
            } else {
                editor.abort()
            }
        } catch (e: IOException) {
            logw(mTag, e.toString())
        }
    }

    /**
     * 获取Serializable类型的数据
     *
     * @param key
     * @param password
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getSerializable(key: String, password: String = encryptPwd): T? {
        return getSerializable(key, null, password)
    }

    /**
     * 获取Serializable类型的数据
     *
     * @param key
     * @param defaultValue default value
     * @param password
     */
    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <T> getSerializable(key: String, defaultValue: T? = null,
                            password: String = encryptPwd): T? {
        try {
            val snapshot = get(key) ?: return null
            val lifeTime = snapshot.getLifeTime()
            if (lifeTime == DEFAULT_LIFE_TIME || System.currentTimeMillis() < lifeTime) {
                val inputStream = handleOrDecrypt(snapshot, password) ?: return defaultValue
                return ObjectInputStream(inputStream)
                        .readObject() as T
            } else {
                remove(key)
                return defaultValue
            }
        } catch (e: IOException) {
            logw(mTag, e.toString())
        } catch (classNotFound: ClassNotFoundException) {
            logw(mTag, classNotFound.toString())
        } catch (castException: ClassCastException) {
            logw(mTag, castException.toString())
        }
        return defaultValue
    }

    /**
     * 保存Serializable类型的数据
     *
     * @param key
     * @param serializable
     * @param lifeTime 有效时间
     */
    fun putSerializable(key: String, serializable: Serializable, lifeTime: Long = DEFAULT_LIFE_TIME) {
        putSerializable(key, serializable, encryptPwd, lifeTime)
    }

    /**
     * 保存Serializable类型的数据
     *
     * @param key
     * @param serializable
     * @param password 加密密码
     * @param lifeTime 有效时间
     */
    @JvmOverloads
    fun putSerializable(key: String, serializable: Serializable, password: String = encryptPwd,
                        lifeTime: Long = DEFAULT_LIFE_TIME) {
        try {
            val editor = diskLruCache.edit(key.md5()) ?: return
            val outputStream = handleOrEncrypt(editor, password) ?: return
            if (writeToSerializable(serializable, outputStream)) {
                editor.setLifTime(lifeTime)
                        .commit()
            } else {
                editor.abort()
            }
        } catch (e: IOException) {
            logw(mTag, e.toString())
        }
    }

    /**
     * 通用的get
     *
     * @param key
     */
    fun get(key: String): DiskLruCache.Snapshot? {
        return try {
            diskLruCache.get(key.md5())
        } catch (e: IOException) {
            logw(mTag, e.toString())
            null
        }
    }

    /**
     * 移除数据
     *
     * @param key
     */
    fun remove(key: String): Boolean {
        return try {
            diskLruCache.remove(key.md5())
        } catch (e: IOException) {
            logw(mTag, e.toString())
            false
        }
    }

    /**
     * 删除所有缓存
     */
    fun evictAll() {
        diskLruCache.delete()
        diskLruCache.directory.delete()
    }

    /**
     * 缓存大小
     */
    fun size(): Long {
        return diskLruCache.size()
    }

    /**
     * 最大缓存
     */
    fun maxSize(): Long {
        return diskLruCache.maxSize
    }

    /**
     * OutputStream写入String类型数据
     */
    private fun writeToString(value: String, os: OutputStream): Boolean {
        BufferedWriter(OutputStreamWriter(os))
                .use({
                    it.write(value)
                    return true
                }, {
                    return false
                })
    }

    /**
     * OutputStream写入ByteArray
     */
    private fun writeToBytes(byteArray: ByteArray, os: OutputStream): Boolean {
        os.use({
            it.write(byteArray)
            it.flush()
            return true
        }) {
            return false
        }
    }

    /**
     * OutputStream写入Serializable
     *
     * @param serializable serializable
     * @param os outputStream
     */
    private fun writeToSerializable(serializable: Serializable, os: OutputStream): Boolean {
        ObjectOutputStream(os).use({
            it.writeObject(serializable)
            it.flush()
            return true
        }) {
            return false
        }
    }

    /**
     * 如果password不为空则加密
     *
     * @param editor editor
     * @param password 加密密码
     */
    private fun handleOrEncrypt(editor: DiskLruCache.Editor, password: String): OutputStream? {
        var outputStream = editor.newOutputStream(0) ?: return null
        //加密
        if (password.isNotEmpty()) {
            val encryptCipher = initCipher(password, true)
            outputStream = CipherOutputStream(outputStream, encryptCipher)
        }
        return outputStream
    }

    /**
     * 如果password不为空则解密
     *
     * @param snapshot snapshot
     * @param password 加密密码
     */
    private fun handleOrDecrypt(snapshot: DiskLruCache.Snapshot, password: String): InputStream? {
        //解密
        var inputStream = snapshot.getInputStream(0) ?: return null
        if (password.isNotEmpty()) {
            val decryptCipher = initCipher(password, false)
            inputStream = CipherInputStream(inputStream, decryptCipher)
        }
        return inputStream
    }

    /**
     * 初始化cipher
     *
     * @param key 加密Key
     */
    private fun initCipher(key: String, isEncrypt: Boolean): Cipher? {
        return try {
            val secretKey = generateSecretKey(key, 32)
            com.theone.cachemanager.encrypt.ext.initCipher(
                secretKey,
                DEFAULT_AES_TRANSFORMATION,
                isEncrypt
            )
        } catch (e: Exception) {
            logw(mTag, e.toString())
            null
        }
    }
}