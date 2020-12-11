package com.hong.concurrent.lock.spinlock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 继承Runnable接口是为了测试
 */
public class SpinLock implements Runnable{
    // 初设化cas中的对象为null
    private AtomicReference<Thread> cas = new AtomicReference<>();

    // 同步器，使所有线程能够同时启动
    private static CountDownLatch begin = new CountDownLatch(1);

    private static int num = 0;

    /**
     * 通过CAS加锁
     */
    public void lock(){
        Thread current = Thread.currentThread();
        // 判断其他线程是否拿到cas，即判断cas中的对象是否为null
        // compareAndSet是原子操作
        while (!cas.compareAndSet(null, current)){
            // 不进行任何操作，即自旋
        }
    }

    /**
     * 通过CAS释放锁
     */
    public void unlock(){
        Thread current = Thread.currentThread();
        // 将cas中的对象重新设为null
        cas.compareAndSet(current, null);
    }

    /**
     * 实现对num+1的操作，测试乐观锁CAS机制是否正确
     */
    @Override
    public void run() {
        // 一直等待直到begin调用countDown()
        try {
            begin.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock();
        num += 1;
        // 测试是否能够打印正确，不加锁时会打印混乱，如可能打印两个"nums: 100"
        System.out.println("nums: " + num);
        unlock();
    }

    public static int getNum() {
        return num;
    }

    public static CountDownLatch getBegin(){
        return begin;
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i=0; i<100; i++){
            Thread current = new Thread(new SpinLock());
            current.start();
        }
        // 所有线程同时启动
        SpinLock.getBegin().countDown();
        // 有些线程可能还在排队或运行
        Thread.sleep(5000);
        // 不加锁时会打印混乱，如可能打印两个"nums: 100"
        //　结果应该为100
//        System.out.println(SpinLock.getNum());

    }
}

