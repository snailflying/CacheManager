package com.theone.cachemanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.theone.cache.ACache
import com.theone.cache.encrypt.AesRsaEncrypt
import com.theone.cache.encrypt.RsaEncrypt
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.ReentrantLock

class MainActivity : AppCompatActivity() {
    private var jsonArray: JSONArray? = null
    private var jsonObject: JSONObject? = null
    private var index = 0
    private var countDownLatch = CountDownLatch(10)
    private val lock = ReentrantLock(true)
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 用户已经同意该权限
        ACache.init(
            encryptStrategy = AesRsaEncrypt.getInstance(this@MainActivity),
            cachePath = cacheDir.absolutePath + "/ACache",
            encrypt = true
        )
        initData()
        initEvent()
    }

    private fun initData() {
        jsonObject = JSONObject();
        try {
            jsonObject!!.put("11", "11");
        } catch (e: JSONException) {
            e.printStackTrace();
        }

        jsonArray = JSONArray()
        jsonArray!!.put(jsonObject);
    }

    private fun initEvent() {
        main_text.setOnClickListener {
            val time1 = System.currentTimeMillis();

            val key1Value = ACache.getCache().getString("key1")
            val key2Value = ACache.getCache().getString("key2", "default")
            val key2ValueEncrypt = ACache.getCache().getString("key2", "default", decrypt = true)
            val key3Value = ACache.getCache().getString("key3", "default")
            val key4Value = ACache.getCache().getString("key4", "default")
            val key5Test = ACache.getCache().getSerializable("key5", Test(115, "aaron5"))//可能为null
            val key5Value = if (key5Test == null) "" else key5Test!!.toString()
            val key6Test = ACache.getCache().getSerializable("key6", Test(116, "aaron6"), true)
            val key6Value = if (key6Test == null) "" else key6Test!!.toString()
            val key7Value = ACache.getCache().getJsonObj("key7")
            val key8Value = ACache.getCache().getJsonObj("key8", decrypt = true)
            val key9Value = ACache.getCache().getJsonArray("key9")
            val key10Value = ACache.getCache().getJsonArray("key10", decrypt = true)
            val key11Value = ACache.getCache().getString("key11")
            val key12Value = ACache.getCache().getString("key12", "default")
            val key13Value = ACache.getCache().getString("key13")
            val key14Test = ACache.getCache().getSerializable<Test>("key14")
            val key14Value = if (key14Test == null) "" else key14Test!!.toString()
            val key15Value = ACache.getCache().getString("key15", "default")
            val key16Test = ACache.getCache().getSerializable("key16", Test(115, "aaron16"), true)
            val key16Value = if (key16Test == null) "" else key16Test!!.toString()
            //key19未存储的数据，返回默认值
            val key19Value = ACache.getCache().getString("null1", "null1")
            //key20未存储的数据，返回默认值

            val value = ("测试:\n"
                    + "字符串(默认方式):"
                    + check("测试数据1", key1Value)
                    + "\n"
                    + "字符串(加密):原始：$key2Value 加密： $key2ValueEncrypt"
                    + check("测试数据2", key2Value)
                    + "\n"
                    + "特殊字符串(不加密):"
                    + check("~!@#$%^&*()_+{}[];':,.<>`", key3Value)
                    + "\n"
                    + "特殊字符串(加密):"
                    + check("~!@#$%^&*()_+{}[];':,.<>`", key4Value)
                    + "\n"
                    + "实体对象[Test类](不加密):"
                    + check(Test(1, "2").toString(), key5Value)
                    + "\n"
                    + "实体对象[Test类](加密):"
                    + check(Test(1, "2").toString(), key6Value)
                    + "\n"
                    + "jsonObject对象(不加密):"
                    + check(jsonObject.toString(), key7Value.toString())
                    + "\n"
                    + "jsonObject对象(加密):"
                    + check(jsonObject.toString(), key8Value.toString())
                    + "\n"
                    + "jsonArray对象(不加密):"
                    + check(jsonArray.toString(), key9Value.toString())
                    + "\n"
                    + "jsonArray对象(加密):"
                    + check(jsonArray.toString(), key10Value.toString())
                    + "\n"
                    + "数字(不加密):"
                    + check(1.toString() + "", key11Value)
                    + "\n"
                    + "数字(加密):"
                    + check(1.toString() + "", key12Value)
                    + "\n"
                    + "字符串(5秒):"
                    + check("测试数据1", key13Value)
                    + "\n"
                    + "实体对象[Test类](5秒):"
                    + check(Test(1, "2").toString(), key14Value)
                    + "\n"
                    + "字符串(5秒)(加密):"
                    + check("测试数据1", key15Value)
                    + "\n"
                    + "实体对象(5秒)(加密):"
                    + check(Test(1, "2").toString(), key16Value)
                    + "\n"
                    )
            main_text3.setText(value)
            val time2 = System.currentTimeMillis()
            val time = (time2 - time1)
            Toast.makeText(this@MainActivity, "展示完毕,耗时：$time 毫秒", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.main_text1).setOnClickListener {
            val time1 = System.currentTimeMillis();
            ACache.getCache().putString("key1", "测试数据1")//默认加密状态
            ACache.getCache().putString("key2", "测试数据2", encrypt = true)//true代表加密存储
            ACache.getCache().putString("key18", "测试数据18", encrypt = false)//false代表不加密存储
            ACache.getCache().putString("key3", "~!@#$%^&*()_+{}[];':,.<>`")//特殊字符串测试
            ACache.getCache().putString("key4", "~!@#$%^&*()_+{}[];':,.<>`", encrypt = true)//加密特殊字符串测试
            ACache.getCache().putSerializable("key5", Test(1, "2"))//实体对象测试
            ACache.getCache().putSerializable("key6", Test(1, "2"), encrypt = true)//加密实体对象测试
            ACache.getCache().putJsonObj("key7", jsonObject)//jsonObject对象测试
            ACache.getCache().putJsonObj("key8", jsonObject, encrypt = true)//加密jsonObject对象测试
            ACache.getCache().putJsonArray("key9", jsonArray)//jsonArray对象测试
            ACache.getCache().putJsonArray("key10", jsonArray, encrypt = true)//加密jsonArray对象测试
            ACache.getCache().putString("key11", "1")//jsonArray对象测试
            ACache.getCache().putString("key12", "1", encrypt = true)//加密jsonArray对象测试
            ACache.getCache().putString("key13", "测试数据1", 5)//保存数据5秒
            ACache.getCache().putSerializable("key14", Test(1, "2"), 5)//保存对象数据5秒
            ACache.getCache().putString("key15", "测试数据1", 5, true)//加密保存数据5秒
            ACache.getCache().putSerializable("key16", Test(1, "2"), 5, true)//加密保存对象数据5秒
            val time2 = System.currentTimeMillis()
            val time = (time2 - time1)
            Toast.makeText(this@MainActivity, "保存成功,耗时：$time 毫秒", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.main_text2).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                index = 0
                countDownLatch = CountDownLatch(10)
                Toast.makeText(this@MainActivity, "压力测试，看Log", Toast.LENGTH_SHORT).show()
                val thread = Thread(Runnable { doTask() })
                thread.start()
            }
        })

        findViewById<View>(R.id.main_text4).setOnClickListener {
            clearMemory()
            Toast.makeText(this@MainActivity, "清理内存成功", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.main_text5).setOnClickListener {
            ACache.getCache().evictMemoryAll()
            Toast.makeText(this@MainActivity, "清理所有内存缓存成功", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.main_text6).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                ACache.getCache().evictAll()
                Toast.makeText(this@MainActivity, "清理缓存成功", Toast.LENGTH_SHORT).show()
            }
        })

        findViewById<View>(R.id.main_text7).setOnClickListener {
            ACache.getCache().remove("key1")
            ACache.getCache().remove("key2")
            ACache.getCache().remove("key18")
            ACache.getCache().remove("key3")
            ACache.getCache().remove("key4")
            Toast.makeText(this@MainActivity, "清理缓存成功", Toast.LENGTH_SHORT).show()
        }
    }

    private fun doTask() {
        val startTime = System.currentTimeMillis()   //获取开始时间
        for (i in 0..9) {
            val thread = Thread(Runnable {
                synchronized(this@MainActivity) {
                    index++
                }
                ACache.getCache().putString("key0", "" + index)//默认加密状态
                countDownLatch.countDown()
            })
            thread.start()
        }
        try {
            countDownLatch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val str1 = ACache.getCache().getString("key0")
        Log.e("time", str1)
        val endTime = System.currentTimeMillis() //获取结束时间
        Log.e("time", "程序运行时间： " + (endTime - startTime) + "ms")
    }


    private fun clearMemory() {
        ACache.getCache().removeFromMemory("key1")
        ACache.getCache().removeFromMemory("key2")
        ACache.getCache().removeFromMemory("key18")
        ACache.getCache().removeFromMemory("key3")
        ACache.getCache().removeFromMemory("key4")
        ACache.getCache().removeFromMemory("key5")
        ACache.getCache().removeFromMemory("key6")
        ACache.getCache().removeFromMemory("key7")
        ACache.getCache().removeFromMemory("key8")
        ACache.getCache().removeFromMemory("key9")
        ACache.getCache().removeFromMemory("key10")
        ACache.getCache().removeFromMemory("key11")
        ACache.getCache().removeFromMemory("key12")
        ACache.getCache().removeFromMemory("key13")
        ACache.getCache().removeFromMemory("key14")
        ACache.getCache().removeFromMemory("key15")
        ACache.getCache().removeFromMemory("key16")
    }

    fun check(str: String, str1: String): String {
        return if (str == str1) "ok" else "fail"
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_default -> {
                clearMemory()
            }
            R.id.action_config1 -> {
                clearMemory()
            }
            else -> {
            }
        }
        return true
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
