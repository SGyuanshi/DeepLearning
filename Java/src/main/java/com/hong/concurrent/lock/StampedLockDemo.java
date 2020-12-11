package com.hong.concurrent.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * StampedLock，在提供类似读写锁的同时，还支持优化读模式。
 * 优化读基于假设，大多数情况下读操作并不会和写操作冲突，
 * 其逻辑是先试着直接读取数据，
 * 然后通过 validate 方法确认是否进入了写模式，
 * 如果没有进入，就成功避免了开销，直接返回数据；
 * 如果进入，则尝试获取读锁，重新读取数据。
 */
public class StampedLockDemo {

    private final StampedLock lock = new StampedLock();
    private final Map<String, String> map = new HashMap<>();

    public static void main(String[] args) {
        StampedLockDemo demo = new StampedLockDemo();
        demo.run();
    }

    public void run(){
        new Thread(new MyThread("test", "abc")).start();
        new Thread(new MyThread("test-2", "fff")).start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(new MyThread("test")).start();
        new Thread(new MyThread("test")).start();
        new Thread(new MyThread("test-2")).start();
    }

    private class MyThread implements Runnable{
        String key;
        String value;

        MyThread(String key){
            this.key = key;
        }
        MyThread(String key, String value){
            this.key = key;
            this.value = value;
        }
        @Override
        public void run() {
            if (value != null){
                write(key, value);
            } else{
                read(key);
            }
        }

        public void read(String key){
            long state = lock.tryOptimisticRead();
            String value = null;
            // state为0表示写锁被其他线程持有，其他值则表示此刻没有线程持有写锁
            // 尝试直接无锁读取数据
            if (state != 0){
                value = map.get(key);
            }
            // 判断在这段时间内是否其他线程获取写锁
            if (!lock.validate(state)){
                // 表示其他线程获取过写锁，数据可能已经发生变化，则申请读锁
                try{
                    state = lock.readLock();
                    System.out.println(Thread.currentThread().getName() + "成功申请读锁");
                    value = map.get(key);
                } finally {
                    lock.unlockRead(state);
                }
            }
            // 表示没有线程获取过写锁，则不申请读锁，直接返回
            System.out.println(Thread.currentThread().getName() + " 读取成功：" + value);
        }

        public void write(String key, String value){
            long state = lock.writeLock();
            System.out.println("写锁锁定：" + Thread.currentThread().getName());
            try {
                map.put(key, value);
                Thread.sleep(2000);
                System.out.println("数据写入完成，key：" + key + "，value：" + value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
               lock.unlockWrite(state);
            }
        }
    }
}
