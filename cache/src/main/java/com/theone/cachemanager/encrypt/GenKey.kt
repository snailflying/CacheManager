package com.theone.cachemanager.encrypt

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @Author zhiqiang
 * @Date 2019-05-15
 * @Email liuzhiqiang@moretickets.com
 * @Description
 */
object GenKey {


    /**
     * SHA加密
     *
     * @param strText 明文
     * @return SHA-256
     */
    fun SHA(strText: String?): String? {
        // 返回值
        var strResult: String? = null
        // 是否是有效字符串
        if (strText != null && strText.length > 0) {
            try {
                // SHA 加密开始
                val messageDigest = MessageDigest.getInstance("SHA-256")
                // 传入要加密的字符串
                messageDigest.update(strText.toByteArray())
                val byteBuffer = messageDigest.digest()
                val strHexString = StringBuilder()
                for (aByteBuffer in byteBuffer) {
                    val hex = Integer.toHexString(0xff and aByteBuffer.toInt())
                    if (hex.length == 1) {
                        strHexString.append('0')
                    }
                    strHexString.append(hex)
                }
                strResult = strHexString.toString()
            } catch (e: NoSuchAlgorithmException) {
                Log.e("Exception", e.message)
            }

        }

        return strResult
    }
    public fun getKey(key:String,keySizeInBytes: Int): ByteArray {
        //加密随机字符串生成AES key
        return GenKey.SHA("$key#\$Zhi\$D%F^Qiang")!!.substring(0, keySizeInBytes).toByteArray()
    }

}
