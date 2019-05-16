@file:JvmName("KCacheUtils")

package com.theone.cache.ext

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import com.jakewharton.disklrucache.DiskLruCache
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.InputStream
import java.io.ObjectOutputStream
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019-05-16
 * @Description
 */

/**
 * 默认的Disk缓存最大尺寸
 */
const val DEFAULT_DISK_MAX_SIZE: Long = 50 * 1024 * 1024L
/**
 * 默认的Memory缓存最大尺寸
 */
const val DEFAULT_MEMORY_MAX_SIZE: Int = 1024 * 1024
/**
 * 默认的过期时间
 */
const val DEFAULT_LIFE_TIME: Long = -1L

/**
 * 流自动关闭并在catch的时候执行[errorBlock]
 */
inline fun <T : Closeable?, R> T.use(block: (T) -> R, errorBlock: (T) -> R): R {
    return try {
        block(this)
    } catch (e: Throwable) {
        errorBlock(this)
    } finally {
        when {
            this == null -> {
            }
            else ->
                try {
                    close()
                } catch (closeException: Throwable) {
                    // cause.addSuppressed(closeException) // ignored here
                }
        }
    }
}

/**
 * log.w
 */
internal fun logw(tag: String, content: String) {
    Log.w(tag, content)
}

/**
 * log.d
 */
internal fun logd(tag: String, content: String) {
    Log.d(tag, content)
}

/**
 * DiskLruCache.Snapshot扩展函数获取过期时间
 */
internal fun DiskLruCache.Snapshot.getLifeTime(): Long {
    val string = getString(1) ?: return -1L
    return string.toLong()
}

/**
 * DiskLruCache.Editor扩展函数保存过期时间
 */
internal fun DiskLruCache.Editor.setLifTime(lifeTime: Long): DiskLruCache.Editor {
    val timestamp: Long = if (lifeTime == DEFAULT_LIFE_TIME) {
        DEFAULT_LIFE_TIME
    } else System.currentTimeMillis() + lifeTime
    this.set(1, timestamp.toString())
    return this
}

/**
 * InputStream扩展函数readText后自动关闭流
 */
fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
    return this.bufferedReader(charset).use { it.readText() }
}

/**
 * Bitmap转成ByteArray
 */
fun Bitmap.toByteArray(quality: Int = 100): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, quality, stream)
    return stream.toByteArray()
}

/**
 * Bitmap转成Drawable
 */
fun Bitmap.toDrawable(resources: Resources = Resources.getSystem()): Drawable {
    return BitmapDrawable(resources, this)
}

/**
 * ByteArray转成Bitmap
 */
fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

/**
 * Drawable转成Bitmap
 */
fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        if (this.bitmap != null) {
            return this.bitmap
        }
    }
    val bitmap = if (this.intrinsicWidth <= 0 || this.intrinsicHeight <= 0) {
        // Single color bitmap will be created of 1x1 pixel
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

/**
 * 序列化求对象大小
 */
fun Any.sizeof(): Int {
    val byteStream = ByteArrayOutputStream()
    ObjectOutputStream(byteStream).use({
        it.writeObject(this)
        it.flush()
        return byteStream.size()
    }) {
        return 1
    }
}

/**
 * 计算对象大小
 */
fun Any.sizeofAny(): Int {
    return when (this) {
        is String -> this.toByteArray().size
        is ByteArray -> this.size
        is Drawable -> this.toBitmap().byteCount
        is Bitmap -> this.byteCount
        else -> sizeof()
    }
}

fun String.md5(): String {
    if (TextUtils.isEmpty(this)) {
        return ""
    }
    var md5: MessageDigest? = null
    try {
        md5 = MessageDigest.getInstance("MD5")
        val bytes = md5!!.digest(this.toByteArray())
        var result = ""
        for (b in bytes) {
            var temp = Integer.toHexString(b.toInt() and 0xff)
            if (temp.length == 1) {
                temp = "0$temp"
            }
            result += temp
        }
        return result
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }

    return ""
}
