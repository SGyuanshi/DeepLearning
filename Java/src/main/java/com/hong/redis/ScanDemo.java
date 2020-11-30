package com.hong.redis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class ScanDemo {

    public static void main(String[] args) throws IOException {
        ScanDemo scanDemo = new ScanDemo();
        scanDemo.hScan();

    }

    /**
     * redis的hashmap数据结构的hscan功能
     * @throws IOException
     */
    public static void hScan() throws IOException {
        // 添加redis集群的节点
        Set<HostAndPort> hosts = new HashSet<HostAndPort>();
        hosts.add(new HostAndPort("192.1.6.147", 22420));

        // 创建redis客户端连接
        JedisCluster client = new JedisCluster(hosts);
        // 要进行scan的数据的key
        String key = "hash-test";
        // hscan配置的实例
        ScanParams params = new ScanParams();
        // 匹配模式，只匹配以test开头的field
        params.match("test*");
        // 一次扫描100条数据
        params.count(100);

        // 第一次cursor设置为0,
        String cursor = "0";
        ScanResult<Entry<byte[], byte[]>> scans = client.hscan(key.getBytes(), cursor.getBytes());
        for (Entry<byte[], byte[]> entry: scans.getResult()) {
            System.out.println(new String(entry.getKey()));
            System.out.println(new String(entry.getValue()));
        }
        cursor = scans.getStringCursor();

        // 当cursor为0时，即结束一轮的扫描
        while (!cursor.equals("0")) {
            scans = client.hscan(key.getBytes(), cursor.getBytes());
            for (Entry<byte[], byte[]> entry: scans.getResult()) {
                System.out.println(new String(entry.getKey()));
                System.out.println(new String(entry.getValue()));
            }
            cursor = scans.getStringCursor();
        }

        client.close();
    }

    /**
     * 单节点的redis连接
     * @param args
     */
    public static void redisLocal(String[] args) {
        //连接本地的 Redis 服务
        Jedis client = new Jedis("localhost");
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: "+ client.ping());

        client.close();
    }

}
