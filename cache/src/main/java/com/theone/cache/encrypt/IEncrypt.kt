package com.theone.cache.encrypt

import javax.crypto.Cipher

/**
 * @Author zhiqiang
 * @Date 2019-05-14
 * @Email liuzhiqiang@moretickets.com
 * @Description 加解密策接口
 */
interface IEncrypt {

    @Throws(Exception::class)
    fun getEncryptCipher(key: String?): Cipher

    @Throws(Exception::class)
    fun getDecryptCipher(key: String?): Cipher

    @Throws(Exception::class)
    fun encrypt(key: String?, plainText: String): String

    @Throws(Exception::class)
    fun decrypt(key: String?, encryptedText: String): String
}
