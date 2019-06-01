
# [CacheManager](https://github.com/snailflying/CacheManager) 

> android缓存管理器，分为两级缓存：内存缓存和文件缓存；先取内存数据，没有再从文件缓存中获取

# 特点
+ 二级缓存
	+ 内存缓存（采用LruCache实现）
	+ 文件缓存（由DiskLruCache实现）
+ 默认使用SD卡缓存
    + getCacheDir()获取的缓存文件较容易被删除
    + 大于1M的缓存文件，google建议使用getExternalCacheDir()缓存存储
    + 默认存储位置为app数据缓存位置
	    + 为处理防止被删除，在数据库文件夹下创建ACache文件夹，数据存储在该文件夹下
+ 支持文件加密存储
    + 默认Rsa加密内容
    + 默认生成Rsa唯一密钥（建议使用默认生成的密钥）
        + 默认生成的默认密钥，每个客户端都是唯一的，互不相同
        + 默认密钥存储在KeyStore中，防逆向工程获取密钥
+ 支持基本数据类型、String、JSONObject、JSONArray、实体对象（Test类）
	+ 不支持数据类型可转换成String存储
+ **缓存数据可设置过期时间，到期自动销毁**
+ 允许内存缓存
+ key值加密
    + 对应的本地缓存文件也加密
+ **添加数据监控机制**
+ **支持自定义加密算法**


# 使用方法
#### 
```
//初始化
ACache.init(
            encryptStrategy = RsaEncrypt.getInstance(this@MainActivity),
            cachePath = cacheDir.absolutePath + "/ACache",
            encrypt = true
        )
//存数据
ACache.getCache().putString("key1", "测试数据1")
//取数据
val key1Value = ACache.getCache().getString("key1")

```
具体方法见Demo
	
# 项目添加方法

暂时只支持源码依赖。


# 特别注意
+ 


# 关于

+ 个人博客：[简书](https://www.jianshu.com/u/50bb4070ebb0)
+ 如果你也喜欢这个库，Star一下吧，欢迎Fork

# 参考：
[Cache](https://github.com/zyyoona7/Cache)

# License

    Copyright 2016 Zhiqiang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.