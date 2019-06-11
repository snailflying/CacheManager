package com.theone.cache.encrypt

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.text.TextUtils
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
 * @Description 加密大数据时使用，因两层加密，加密小数据反而慢，小数据时推荐用RsaEncrypt
 */
class AesRsaEncrypt
/**
 * 顺序不能乱
 * 1.生成keyStore
 * 2.生成RsaKey
 * 3.生成AesKey
 *
 * @param context 上下文
 */
private constructor(context: Context) : IEncrypt {
    private val mContext: Context = context.applicationContext
    private var keyStore: KeyStore? = null
    private var secretKeySpec: SecretKeySpec? = null

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
            createAESKey()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Throws(Exception::class)
    override fun getEncryptCipher(key: String?): Cipher {
        val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
        cipher.init(Cipher.ENCRYPT_MODE, getAESKeySpec(key), ivParameterSpec)
        return cipher
    }

    @Throws(Exception::class)
    override fun getDecryptCipher(key: String?): Cipher {
        val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
        cipher.init(Cipher.DECRYPT_MODE, getAESKeySpec(key), ivParameterSpec)
        return cipher
    }

    /**
     * AES 加密
     *
     * @param plainText: 需要被加密数据.
     * @return Base64格式的加密字符串.
     */
    @Throws(Exception::class)
    override fun encrypt(key: String?, plainText: String): String {
        val cipher = getEncryptCipher(key)

        val encryptedBytes = cipher.doFinal(plainText.toByteArray())

        //将byte转为Base64编码格式
        return Base64Util.encode(encryptedBytes)
    }

    /**
     * AES 解密
     *
     * @param encryptedText: 需要被解密数据.
     * @return 解密后字符串.
     */
    @Throws(Exception::class)
    override fun decrypt(key: String?, encryptedText: String): String {
        //将Base64编码格式字符串解码为byte
        val decodedBytes = Base64Util.decode(encryptedText)

        val cipher = getDecryptCipher(key)

        return String(cipher.doFinal(decodedBytes))
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
    private fun createAESKey() {
        // Generate AES-Key
        if (TextUtils.isEmpty(getIV(mContext))) {
            setIV(mContext, iv)
        }

        if (TextUtils.isEmpty(getAESKey(mContext))) {
            // Encrypt AES-Key with RSA Public Key then save to SharedPrefs
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                val encryptAESKey = encryptRSA(getKey(null))
                setAESKey(mContext, encryptAESKey)
            }
        }
    }

    @Throws(Exception::class)
    private fun getAESKeySpec(key: String?): SecretKeySpec {
        return if (secretKeySpec != null) {
            secretKeySpec!!
        } else {
            val aesKey: ByteArray
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                val encryptedKey = getAESKey(mContext)
                aesKey = decryptRSA(encryptedKey)
            } else {
                aesKey = getKey(key)
            }
            secretKeySpec = SecretKeySpec(aesKey, TYPE_AES)
            secretKeySpec!!
        }

    }

    //keyLenth:密钥长度 需要*8  取值只能为 16 24 32 对应密钥长度为128、192、256
    private fun getKey(key: String?): ByteArray {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val aesKey = ByteArray(32)
            val secureRandom = SecureRandom()
            secureRandom.nextBytes(aesKey)

            //加密随机字符串生成AES key
            return aesKey

        } else {
            val serialNo = GenKey.getAndroidId(mContext)
            //加密随机字符串生成AES key
            return GenKey.SHA("$serialNo$key#\$Zhi\$D%F^Qiang")!!.substring(0, 32).toByteArray()

        }
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
                RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4)
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
                keyStore!!.containsAlias(alias)
            } catch (e: Exception) {
                false
            }

        } else {
            true
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Throws(Exception::class)
    private fun encryptRSA(plainText: ByteArray): String {
        val publicKey = keyStore!!.getCertificate(KEYSTORE_ALIAS).publicKey

        val cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val encryptedByte = cipher.doFinal(plainText)
        return Base64Util.encode(encryptedByte)
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Throws(Exception::class)
    private fun decryptRSA(encryptedText: String): ByteArray {
        //        PrivateKey privateKey = (PrivateKey) keyStore.getKey(KEYSTORE_ALIAS, null);
        val privateKey = keyStore!!.getKey(KEYSTORE_ALIAS, null) as PrivateKey

        val cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        val encryptedBytes = Base64Util.decode(encryptedText)

        return cipher.doFinal(encryptedBytes)
    }


    companion object : SingletonHolder<AesRsaEncrypt, Context>(::AesRsaEncrypt) {
        val TAG = "AesRsaUtil"
        private val KEYSTORE_ALIAS = "KEYSTORE_DEMO"
        private val ENCRYPT_INFO = "encrypt_info"

        val ANDROID_KEY_STORE = "AndroidKeyStore"
        val TYPE_RSA = "RSA"
        val TYPE_AES = "AES"
        val AES_GCM_NO_PADDING = "AES/GCM/NoPadding"
        val RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1Padding"

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

        fun getAESKey(context: Context): String {
            val spUser = getSpSetting(context)
            return spUser!!.getString("aes_key", "") ?: ""
        }

        fun setIV(context: Context, value: String) {
            if (TextUtils.isEmpty(value)) return
            val spUser = getSpSetting(context)
            val editor = spUser!!.edit()
            editor.putString("security_iv", value)
            editor.apply()
        }

        fun getIV(context: Context): String {
            val spUser = getSpSetting(context)
            return spUser!!.getString("security_iv", "") ?: ""
        }

    }

}
