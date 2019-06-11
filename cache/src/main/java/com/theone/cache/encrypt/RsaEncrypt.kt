package com.theone.cache.encrypt

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.*
import java.security.spec.RSAKeyGenParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

/**
 * @Author zhiqiang
 * @Date 2019-05-15
 * @Email liuzhiqiang@moretickets.com
 * @Description
 * 顺序不能乱
 * 1.生成keyStore
 * 2.生成RsaKey
 * 3.生成AesKey
 *
 */
class RsaEncrypt private constructor(context: Context) : IEncrypt {
    private val mContext: Context = context.applicationContext
    private var keyStore: KeyStore? = null

    private val ivParameterSpec: IvParameterSpec
        @Throws(Exception::class)
        get() = IvParameterSpec(iv.toByteArray())

    private//加密随机字符串生成AES key
    val iv: String
        get() {
            val serialNo = GenKey.getAndroidId(mContext)
            return GenKey.SHA("$serialNo#\$Zhi\$D%FQiang")!!.substring(0, 12)
        }

    init {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore!!.load(null)
            createRsaKeys(context, KEYSTORE_ALIAS)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Throws(Exception::class)
    override fun getEncryptCipher(key: String?): Cipher {
        val keySpec = getKeySpec(key)

        val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec)
        return cipher

    }

    @Throws(Exception::class)
    override fun getDecryptCipher(key: String?): Cipher {
        val keySpec = getKeySpec(key)
        val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec)
        return cipher

    }


    @Throws(Exception::class)
    fun getPrivateEncryptCipher(key: String?): Cipher {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val publicKey = keyStore!!.getCertificate(KEYSTORE_ALIAS).publicKey

            val cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            cipher
        } else {
            val keySpec = getKeySpec(key)

            val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec)
            cipher
        }

    }

    @Throws(Exception::class)
    fun getPrivateDecryptCipher(key: String?): Cipher {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val privateKey = keyStore!!.getKey(KEYSTORE_ALIAS, null) as PrivateKey
            val cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            cipher
        } else {
            val keySpec = getKeySpec(key)
            val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec)
            cipher
        }

    }

    @Throws(Exception::class)
    override fun encrypt(key: String?, plainText: String): String {

        val cipher = getPrivateEncryptCipher(key)

        return if (needSplit(plainText)) {
            String(rsaSplitEncryptByPub(DEFAULT_RSA_KEY_LENGTH, plainText.toByteArray(), cipher))
        } else {
            val encryptedByte = cipher.doFinal(plainText.toByteArray())
            Base64Util.encode(encryptedByte)
        }
    }

    @Throws(Exception::class)
    override fun decrypt(key: String?, encryptedText: String): String {

        val cipher = getPrivateDecryptCipher(key)

        val decryptedBytes = Base64Util.decode(encryptedText)
        return if (needSplit(encryptedText)) {
            String(rsaSplitDecryptByPri(DEFAULT_RSA_KEY_LENGTH, decryptedBytes, cipher))
        } else {
            String(cipher.doFinal(decryptedBytes))
        }
    }

    @Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class)
    private fun createRsaKeys(context: Context, alias: String) {
        if (!containsAlias(alias)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                createKeysM(alias, false)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                createKeysJBMR2(context, alias)
            }
        }
    }

    @Throws(Exception::class)
    private fun getKeySpec(key: String?): SecretKeySpec {

        val aesKey = getKey(key)

        return SecretKeySpec(aesKey, TYPE_AES)
    }

    //keyLenth:密钥长度 需要*8  取值只能为 16 24 32 对应密钥长度为128、192、256
    private fun getKey(key: String?): ByteArray {
        val serialNo = GenKey.getAndroidId(mContext)
        //加密随机字符串生成AES key
        return GenKey.SHA("$serialNo$key#\$Zhi\$D%F^Qiang")!!.substring(0, 32).toByteArray()
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class)
    private fun createKeysJBMR2(context: Context, alias: String): KeyPair {

        val start = GregorianCalendar()
        val end = GregorianCalendar()
        end.add(Calendar.YEAR, 30)

        val spec = KeyPairGeneratorSpec.Builder(context)
            // You'll use the alias later to retrieve the key. It's a key
            // for the key!
            .setAlias(alias)
            .setSubject(X500Principal("CN=$alias"))
            .setSerialNumber(BigInteger.valueOf(Math.abs(alias.hashCode()).toLong()))
            // Date range of validity for the generated pair.
            .setStartDate(start.time)
            .setEndDate(end.time)
            .build()

        val kpGenerator = KeyPairGenerator.getInstance(
            TYPE_RSA,
            ANDROID_KEY_STORE
        )
        kpGenerator.initialize(spec)
        return kpGenerator.generateKeyPair()
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class, InvalidAlgorithmParameterException::class)
    private fun createKeysM(alias: String, requireAuth: Boolean): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA,
            ANDROID_KEY_STORE
        )
        keyPairGenerator.initialize(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setAlgorithmParameterSpec(
                RSAKeyGenParameterSpec(DEFAULT_RSA_KEY_LENGTH, RSAKeyGenParameterSpec.F4)
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setDigests(
                    KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA384,
                    KeyProperties.DIGEST_SHA512
                )
                // Only permit the private key to be used if the user authenticated
                // within the last five minutes.
                .setUserAuthenticationRequired(requireAuth)
                .build()
        )
        return keyPairGenerator.generateKeyPair()

    }

    /**
     * JBMR2+ If Key with the default alias exists, returns true, else false.
     * on pre-JBMR2 returns true always.
     */
    private fun containsAlias(alias: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                keyStore!!.load(null)
                keyStore!!.containsAlias(alias)
            } catch (e: Exception) {
                false
            }

        } else {
            true
        }
    }

    private fun needSplit(plainText: String): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && plainText.length > getMaxCleartextLen(DEFAULT_RSA_KEY_LENGTH))
    }

    /**
     * RSA使用私钥 分段解密
     *
     * @param keyLength
     * @param data
     * @param privateKey
     * @param isEncrypt
     * @param transformation
     */
    private fun rsaSplitDecryptByPri(
        keyLength: Int = DEFAULT_RSA_KEY_LENGTH,
        data: ByteArray,
        cipher: Cipher
    ): ByteArray {
        //分段明文长度
        val cleartextLen = getMaxCleartextLen(keyLength)
        //密钥长度
        val keyLen = keyLength / 8
        //计算分段加密的block数 (向上取整) ，如果余数非0，block数再加1
        val blockSize = if (data.size % cleartextLen == 0) data.size / cleartextLen else data.size / cleartextLen + 1
        // 输出buffer
        val outBuffer = ByteArrayOutputStream(blockSize * cleartextLen)

        return try {
            //解密
            outBuffer.use {
                for (offset in 0 until data.size step keyLen) {
                    // block大小: keyLen 或剩余字节数
                    val inputLen = if (data.size - offset > keyLen) keyLen else data.size - offset
                    // 得到分段加密结果
                    val decryptedBlock = cipher.doFinal(data, offset, inputLen)
                    // 追加结果到输出buffer中
                    outBuffer.write(decryptedBlock, 0, decryptedBlock.size)
                }
                return outBuffer.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyArray<Byte>().toByteArray()
        }
    }

    /**
     * RSA使用公钥 分段加密
     *
     * @param keyLength
     * @param data
     * @param publicKey
     * @param isEncrypt
     * @param transformation
     */
    private fun rsaSplitEncryptByPub(
        keyLength: Int = DEFAULT_RSA_KEY_LENGTH,
        data: ByteArray,
        cipher: Cipher
    ): ByteArray {
        //分段明文长度
        val cleartextLen = getMaxCleartextLen(keyLength)
        //密钥长度
        val keyLen = keyLength / 8
        //计算分段加密的block数 (向上取整) ，如果余数非0，block数再加1
        val blockSize = if (data.size % cleartextLen == 0) data.size / cleartextLen else data.size / cleartextLen + 1
        // 输出buffer, 大小为blockSize个keyLen
        val outBuffer = ByteArrayOutputStream(blockSize * keyLen)
        return try {
            //加/解密
            outBuffer.use {
                for (offset in 0 until data.size step cleartextLen) {
                    // block大小: blockSize 或剩余字节数
                    val inputLen = if (data.size - offset > cleartextLen) cleartextLen else data.size - offset
                    // 得到分段加密结果
                    val encryptedBlock = cipher.doFinal(data, offset, inputLen)
                    // 追加结果到输出buffer中
                    outBuffer.write(encryptedBlock, 0, encryptedBlock.size)
                }
                return outBuffer.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyArray<Byte>().toByteArray()
        }
    }

    /**
     * 获取最大的明文长度
     *
     * @param keyLength 密钥长度
     */
    private fun getMaxCleartextLen(keyLength: Int): Int = keyLength / 8 - 11


    companion object : SingletonHolder<RsaEncrypt, Context>(::RsaEncrypt) {
        val TAG = "AesRsaUtil"

        private val KEYSTORE_ALIAS = "KEYSTORE_ACHACE_RSA"

        val ANDROID_KEY_STORE = "AndroidKeyStore"
        val TYPE_RSA = "RSA"
        val TYPE_AES = "AES"
        val AES_GCM_NO_PADDING = "AES/GCM/NoPadding"
        val RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1Padding"
        const val DEFAULT_RSA_KEY_LENGTH = 1024
    }
}
