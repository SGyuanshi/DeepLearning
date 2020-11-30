package com.hong.redis;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

/**
 * 提供缓存穿透、缓存击穿、缓存雪崩的解决方案
 */
public class CacheDanger {

    private final String lock = "";

    private int expireTime = 100;

    private Jedis jedis = new Jedis("localhost");

    /**
     * 缓存穿透：当输入key从数据库查询不到数据时，将该key进行短暂的缓存，value为EMPTY
     * @param key
     * @return
     */
    private String query2(String key){
        String value = jedis.get(key);
        if (value != null){
            return value;
        }
        value = getDataFromDB(key);
        if (value == null){
            value = StringUtils.EMPTY;
            // 这里的过期时间一般为短期
            jedis.setex(key, expireTime, value);
        }
        return value;
    }

    /**
     * 缓存击穿：setnx利用互斥锁，只让一个线程去读取数据库
     * @param key
     * @return
     */
    private String query3(String key){
        String value = jedis.get(key);
        if (value != null){
            return value;
        }
        // 代表key已经失效
        String keyLock = key+"_lock";
        if ((jedis.setnx(keyLock, "1")) == 1){ //加锁成功
            // 这里对锁加上过期时间，是防止加锁的服务器宕机导致锁一直未释放
            // 或者这里可以由其他服务器的客户端来删除锁key，此时就需要用到value设为：当前时间+过期时间
            jedis.expire(keyLock, expireTime);
            // 从数据库获取数据，然后进行缓存
            value = getDataFromDB(key);
            jedis.setex(key, expireTime, value);
            // 删除keyLock，释放锁
            jedis.del(keyLock);
        } else { // 代表其他线程拿到了锁，正在读取数据库或者已经完成缓存设置
            // 留一点时间保证其他线程已经从数据库中获取数据
            // 或者这里可以进入while循环，不断获取查看锁是否已经释放
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            value = jedis.get(key);
        }
        return value;
    }


    /**
     * 缓存雪崩，加锁只让一个线程查询数据库
     * @param key
     * @return
     */
    private String query(String key){
        String value = jedis.get(key);
        if (value != null){
            return value;
        }
        synchronized (lock){
            if ((value = jedis.get(key)) != null){
                return value;
            }
            value = getDataFromDB(key);
            jedis.setex(key, expireTime, value);
            return value;
        }
    }

    /**
     * 模拟从数据库获取数据
     * @param key
     * @return
     */
    private String getDataFromDB(String key){
        return "";
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
//        jedis.set("test", StringUtils.EMPTY);
        System.out.println(StringUtils.isBlank(jedis.get("test")));
    }
}
