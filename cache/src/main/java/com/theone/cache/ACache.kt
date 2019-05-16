package com.theone.cache

import android.os.Environment
import android.support.v4.util.ArrayMap
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

    private val mMemoryCacheMap: ArrayMap<String, MemoryCache> = ArrayMap()
    private val mDiskCacheMap: ArrayMap<String, DiskCache> = ArrayMap()
    private val M_CACHE_MAP: ArrayMap<String, Cache> = ArrayMap()
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
        diskMaxSize: Long= DEFAULT_DISK_MAX_SIZE,
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
        val cacheKey = "$memoryCache._$diskCache"
        val cacheManager = M_CACHE_MAP[cacheKey]
        return if (cacheManager == null) {
            val newCacheManager = Cache(memoryCache, diskCache, mEncrypt)
            M_CACHE_MAP[cacheKey] = newCacheManager
            newCacheManager
        } else cacheManager
    }

    /**
     * 获取或者创建MemoryCache对象
     */
    @JvmOverloads
    @JvmStatic
    fun getMemoryCache(
        memoryMaxSize: Int = DEFAULT_MEMORY_MAX_SIZE,
        mode: MemoryCache.SizeMode = MemoryCache.SizeMode.Size
    ): MemoryCache {
        return getMemoryCache(memoryMaxSize.toString(), memoryMaxSize, mode)
    }

    /**
     * 获取或者创建MemoryCache对象
     */
    @JvmStatic
    fun getMemoryCache(
        cacheKey: String, memoryMaxSize: Int = DEFAULT_MEMORY_MAX_SIZE,
        mode: MemoryCache.SizeMode = MemoryCache.SizeMode.Size
    ): MemoryCache {
        val memoryCache = mMemoryCacheMap[cacheKey]
        return if (memoryCache == null) {
            val newMemoryCache = MemoryCache(memoryMaxSize, mode)
            mMemoryCacheMap[cacheKey] = newMemoryCache
            newMemoryCache
        } else memoryCache
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
        val cacheKey = "${diskCachePath}_$diskMaxSize"
        if (cacheFile.exists()) {
            val diskCache = mDiskCacheMap[cacheKey]
            return if (diskCache == null) {
                val newDiskCache =
                    DiskCache(File(diskCachePath), appVersion, diskMaxSize, encryptStrategy, encrypt)
                mDiskCacheMap[cacheKey] = newDiskCache
                newDiskCache
            } else diskCache
        }
        val disk = DiskCache(File(diskCachePath), appVersion, diskMaxSize, encryptStrategy, encrypt)
        mDiskCacheMap[cacheKey] = disk
        return disk
    }

}
