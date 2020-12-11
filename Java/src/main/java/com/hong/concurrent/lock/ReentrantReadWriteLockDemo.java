package com.hong.concurrent.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁基于的原理是多个读操作不需要互斥，
 * 如果读锁试图锁定时，写锁是被某个线程持有，读锁将无法获得，而只好等待对方操作结束，
 * 这样就可以自动保证不会读取到有争议的数据。
 * 其实本质就是，写锁是使用独占模式EXCLUSIVE，读锁时使用共享模式SHARED
 */
public class ReentrantReadWriteLockDemo {
    private final Map<String, String> map = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public static void main(String[] args) {
        ReentrantReadWriteLockDemo demo = new ReentrantReadWriteLockDemo();
        demo.run();
    }

    public void run(){
        new Thread(new MyThread("test", "abc")).start();
        new Thread(new MyThread("test-2", "fff")).start();
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
            readLock.lock();
            System.out.println("读锁锁定");
            try{
                Thread.sleep(1000);
                System.out.println("读取成功：" + map.get(key));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readLock.unlock();
            }
        }

        public void write(String key, String value){
            writeLock.lock();
            System.out.println("写锁锁定");
            try {
                map.put(key, value);
                Thread.sleep(2000);
                System.out.println("数据写入完成");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                writeLock.unlock();
            }
        }
    }
}
