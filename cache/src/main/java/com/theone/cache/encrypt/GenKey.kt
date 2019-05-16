package com.theone.cache.encrypt

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

    /**
     * Android ID在8.0后会根据签名计算，各APP不会相同，利用此特性作为key.
     *
     * @return Settings.Secure.ANDROID_ID or serial number if not available.
     */
    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        try {
            val deviceSerial = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

            return if (TextUtils.isEmpty(deviceSerial)) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && context.checkSelfPermission(
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Build.getSerial()
                } else {
                    Build::class.java.getField("SERIAL").get(null) as String
                }
            } else {
                deviceSerial
            }
        } catch (ignored: Exception) {
            // Fall back  to Android_ID
        }

        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }


}
