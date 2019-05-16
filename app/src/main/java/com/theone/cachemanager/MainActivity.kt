package com.theone.cachemanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.theone.cache.ACache
import com.theone.cache.encrypt.AesRsaEncrypt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        ACache.init(encryptStrategy = AesRsaEncrypt(this))

    }
}
