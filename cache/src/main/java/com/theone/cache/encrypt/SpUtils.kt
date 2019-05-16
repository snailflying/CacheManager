package com.theone.cache.encrypt

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

/**
 * @Author zhiqiang
 * @Email liuzhiqiang@moretickets.com
 * @Date 2019-05-15
 * @Description
 */
object SpUtils {

    private const val ENCRYPT_INFO = "encrypt_info"

    private fun getSpSetting(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(ENCRYPT_INFO, Context.MODE_PRIVATE)
    }


    ////*************************************************////
    fun setAESKey(context: Context, value: String) {
        if (TextUtils.isEmpty(value)) return
        val spUser = getSpSetting(context)
        val editor = spUser!!.edit()
        editor.putString("aes_key", value)
        editor.apply()
    }

    fun getAESKey(context: Context): String? {
        val spUser = getSpSetting(context)
        return spUser!!.getString("aes_key", "")
    }

    fun setIV(context: Context, value: String) {
        if (TextUtils.isEmpty(value)) return
        val spUser = getSpSetting(context)
        val editor = spUser!!.edit()
        editor.putString("security_iv", value)
        editor.apply()
    }

    fun getIV(context: Context): String? {
        val spUser = getSpSetting(context)
        return spUser!!.getString("security_iv", "")
    }
}