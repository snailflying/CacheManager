package com.theone.cache.cache

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.jakewharton.disklrucache.DiskLruCache
import com.theone.cache.encrypt.IEncrypt
import com.theone.cache.ext.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream


/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019-05-16
 * @Description 带过期时间和加/解密的DiskLruCache
 */
class DiskCache @JvmOverloads constructor(
    dictionary: File,
    appVersion: Int,
    maxSize: Long = DEFAULT_DISK_MAX_SIZE,
    private val iEncrypt: IEncrypt? = null,
    private val mEncrypt: Boolean = false
) {

    private val mTag: String = "DiskCache"
    private val diskLruCache: DiskLruCache

    init {
        if (!dictionary.exists()){
            dictionary.mkdirs()
        }
        diskLruCache = DiskLruCache.open(
            dictionary,
            appVersion, 2, maxSize
        )
    }


    /**
     * 获取JSONObject类型数据
     *
     * @param key
     */
    @JvmOverloads
    fun getJsonObj(
        key: String, defaultValue: JSONObject? = JSONObject(), decrypt: Boolean = mEncrypt
    ): JSONObject? {
        return try {
            JSONObject(getString(key, decrypt = decrypt))
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
    @JvmOverloads
    fun putJsonObj(
        key: String, jsonObject: JSONObject?,
        lifeTime: Long = DEFAULT_LIFE_TIME, encrypt: Boolean = mEncrypt
    ) {
        putString(key, jsonObject?.toString(), lifeTime, encrypt)
    }

    /**
     * 获取JSONArray类型数据
     *
     * @param key
     */
    @JvmOverloads
    fun getJsonArray(
        key: String, defaultValue: JSONArray? = JSONArray(), decrypt: Boolean = mEncrypt
    ): JSONArray? {
        return try {
            JSONArray(getString(key, decrypt = decrypt))
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
    @JvmOverloads
    fun putJsonArray(
        key: String, jsonArray: JSONArray?,
        lifeTime: Long = DEFAULT_LIFE_TIME, encrypt: Boolean = mEncrypt
    ) {
        putString(key, jsonArray?.toString(), lifeTime, encrypt)
    }

    /**
     * 获取Bitmap类型数据
     *
     * @param key
     * @param defaultValue default value
     */
    @JvmOverloads
    fun getBitmap(key: String, defaultValue: Bitmap? = null, decrypt: Boolean = mEncrypt): Bitmap? {
        return getByteArray(key, defaultValue?.toByteArray(), decrypt)?.toBitmap()
    }


    /**
     * 保存Bitmap类型数据
     *
     * @param key
     * @param bitmap
     * @param lifeTime 有效时间
     */
    @JvmOverloads
    fun putBitmap(
        key: String, bitmap: Bitmap?,
        lifeTime: Long = DEFAULT_LIFE_TIME, encrypt: Boolean = mEncrypt
    ) {
        putByteArray(key, bitmap?.toByteArray(), lifeTime, encrypt)
    }

    /**
     * 获取Drawable类型数据
     *
     * @param key
     */
    @JvmOverloads
    fun getDrawable(key: String, defaultValue: Drawable? = null, decrypt: Boolean = mEncrypt): Drawable? {
        return getBitmap(key, defaultValue?.toBitmap(), decrypt)?.toDrawable()
    }

    /**
     * 保存Drawable类型数据
     *
     * @param key
     * @param drawable
     * @param lifeTime 有效时间
     */
    @JvmOverloads
    fun putDrawable(
        key: String, drawable: Drawable?,
        lifeTime: Long = DEFAULT_LIFE_TIME, encrypt: Boolean = mEncrypt
    ) {
        putBitmap(key, drawable?.toBitmap(), lifeTime, encrypt)
    }

    /**
     * 获取String类型数据
     *
     * @param key
     */
    @JvmOverloads
    fun getString(key: String, defaultValue: String = "", decrypt: Boolean = mEncrypt): String {
        try {
            val snapshot = get(key) ?: return defaultValue
            val lifeTime = snapshot.getLifeTime()
            if (lifeTime == -1L || System.currentTimeMillis() < lifeTime) {
                val inputStream = handleOrDecrypt(snapshot, decrypt) ?: return defaultValue
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
    @JvmOverloads
    fun putString(
        key: String, string: String?,
        lifeTime: Long = DEFAULT_LIFE_TIME,
        encrypt: Boolean = mEncrypt
    ) {
        try {
            val editor = diskLruCache.edit(key.md5()) ?: return
            val outputStream: OutputStream = handleOrEncrypt(editor, encrypt) ?: return
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
     * @param defaultValue default valude
     */
    @JvmOverloads
    fun getByteArray(key: String, defaultValue: ByteArray? = null, decrypt: Boolean = mEncrypt): ByteArray? {
        try {
            val snapshot = get(key) ?: return null
            val lifeTime = snapshot.getLifeTime()
            if (lifeTime == DEFAULT_LIFE_TIME || System.currentTimeMillis() < lifeTime) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                val inputStream = handleOrDecrypt(snapshot, decrypt) ?: return defaultValue
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
    @JvmOverloads
    fun putByteArray(
        key: String, byteArray: ByteArray?,
        lifeTime: Long = DEFAULT_LIFE_TIME,
        encrypt: Boolean = mEncrypt

    ) {
        try {
            val editor = diskLruCache.edit(key.md5()) ?: return
            val outputStream = handleOrEncrypt(editor, encrypt) ?: return
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
     * @param defaultValue default value
     */
    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <T> getSerializable(key: String, defaultValue: T? = null, decrypt: Boolean = mEncrypt): T? {
        try {
            val snapshot = get(key) ?: return null
            val lifeTime = snapshot.getLifeTime()
            if (lifeTime == DEFAULT_LIFE_TIME || System.currentTimeMillis() < lifeTime) {
                val inputStream = handleOrDecrypt(snapshot, decrypt) ?: return defaultValue
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
    @JvmOverloads
    fun putSerializable(
        key: String, serializable: Serializable?,
        lifeTime: Long = DEFAULT_LIFE_TIME,
        encrypt: Boolean = mEncrypt
    ) {
        try {
            val editor = diskLruCache.edit(key.md5()) ?: return
            val outputStream = handleOrEncrypt(editor, encrypt) ?: return
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
    private fun writeToString(value: String?, os: OutputStream): Boolean {
        if (value == null) return false
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
    private fun writeToBytes(byteArray: ByteArray?, os: OutputStream): Boolean {
        if (byteArray == null) return false
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
    private fun writeToSerializable(serializable: Serializable?, os: OutputStream): Boolean {
        if (serializable == null) return false
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
    private fun handleOrEncrypt(editor: DiskLruCache.Editor, encrypt: Boolean): OutputStream? {
        var outputStream = editor.newOutputStream(0) ?: return null
        //加密
        if (encrypt) {
            initCipher(true)?.run {
                outputStream = CipherOutputStream(outputStream, this)
            }
        }

        return outputStream
    }

    /**
     * 如果password不为空则解密
     *
     * @param snapshot snapshot
     * @param password 加密密码
     */
    private fun handleOrDecrypt(snapshot: DiskLruCache.Snapshot, decrypt: Boolean): InputStream? {
        //解密
        var inputStream = snapshot.getInputStream(0) ?: return null
        if (decrypt) {
            initCipher(false)?.run {
                inputStream = CipherInputStream(inputStream, this)
            }
        }

        return inputStream
    }

    /**
     * 初始化cipher
     *
     * @param key 加密Key
     */
    private fun initCipher(isEncrypt: Boolean): Cipher? {
        return try {
            val mode = if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE
            iEncrypt?.getCipher(null, mode)
        } catch (e: Exception) {
            logw(mTag, e.toString())
            null
        }
    }
}