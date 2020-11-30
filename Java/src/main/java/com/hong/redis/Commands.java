package com.hong.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.List;

public class Commands {

    private static Jedis jedis = new Jedis("localhost");

    /**
     * 管道技术：客户端将多个操作放入管道中，然后发送至服务端，服务端一次性处理返回客户端
     */
    static void pipeline(){
        Pipeline pipeline = jedis.pipelined();
        for (int i=0; i<10; i++){
            pipeline.lpush("pipeline", ""+i);
        }
        List<Object> ret = pipeline.syncAndReturnAll();
    }

    public static void main(String[] args) {
        Commands.pipeline();
    }
}
