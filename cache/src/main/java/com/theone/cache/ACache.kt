package com.theone.cache

import android.os.Environment
import com.theone.cache.cache.Cache
import com.theone.cache.cache.DiskCache
import com.theone.cache.cache.MemoryCache
import com.theone.cache.encrypt.IEncrypt
import com.theone.cache.ext.DEFAULT_DISK_MAX_SIZE
import com.theone.cache.ext.DEFAULT_MEMORY_MAX_SIZE
import java.io.File

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019-05-16
 * @Description
 */
object ACache {

    private var mMemoryCache: MemoryCache? = null
    private var mDiskCache: DiskCache? = null
    private var cache: Cache? = null
    private var mCachePath: String = Environment.getExternalStorageDirectory().absolutePath + "/ACache"
    private var mAppVersion: Int = 1
    private var mDiskMaxSize: Long = DEFAULT_DISK_MAX_SIZE

    private var mEncryptStrategy: IEncrypt? = null
    private var mEncrypt: Boolean = false

    /**
     * 初始化默认的缓存参数
     */
    @JvmStatic
    fun init(
        cachePath: String,
        appVersion: Int = 1,
        diskMaxSize: Long = DEFAULT_DISK_MAX_SIZE,
        encryptStrategy: IEncrypt?,
        encrypt: Boolean = true
    ) {
        mCachePath = cachePath
        mAppVersion = appVersion
        mDiskMaxSize = diskMaxSize
        mEncryptStrategy = encryptStrategy
        mEncrypt = if (encryptStrategy != null) encrypt else false;
    }

    /**
     * 获取或者创建CacheManager对象
     */
    @JvmStatic
    @JvmOverloads
    fun getCache(
        memoryCache: MemoryCache = getMemoryCache(),
        diskCache: DiskCache = getDiskCache(encryptStrategy = mEncryptStrategy, encrypt = mEncrypt)
    ): Cache {
        return if (cache == null) {
            cache = Cache(memoryCache, diskCache, mEncrypt)
            cache!!
        } else cache!!
    }

    /**
     * 获取或者创建MemoryCache对象
     */
    @JvmStatic
    fun getMemoryCache(memoryMaxSize: Int = DEFAULT_MEMORY_MAX_SIZE,
        mode: MemoryCache.SizeMode = MemoryCache.SizeMode.Size
    ): MemoryCache {
        return if (mMemoryCache == null) {
            mMemoryCache = MemoryCache(memoryMaxSize, mode)
            mMemoryCache!!
        } else mMemoryCache!!
    }

    /**
     * 移除缓存的DiskCache对象
     */
    @JvmStatic
    @JvmOverloads
    fun getDiskCache(
        diskCachePath: String = mCachePath, appVersion: Int = mAppVersion,
        diskMaxSize: Long = mDiskMaxSize,
        encryptStrategy: IEncrypt? = null,
        encrypt: Boolean = mEncrypt
    ): DiskCache {
        val cacheFile = File(diskCachePath)
        if (cacheFile.exists()) {
            return if (mDiskCache == null) {
                mDiskCache =
                    DiskCache(cacheFile, appVersion, diskMaxSize, encryptStrategy, encrypt)
                mDiskCache!!
            } else mDiskCache!!
        } else {
            cacheFile.mkdirs()
            mDiskCache = DiskCache(cacheFile, appVersion, diskMaxSize, encryptStrategy, encrypt)
            return mDiskCache!!
        }

    }

}
