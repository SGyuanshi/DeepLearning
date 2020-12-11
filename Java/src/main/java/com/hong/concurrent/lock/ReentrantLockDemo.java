package com.hong.concurrent.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * tryLock(long timeout, TimeUnit unit)：带有超时时间的加锁，如果在unit时间内未获得锁，则放弃加锁
 * lockInterruptibly()：如果线程被其他线程中断，则直接放弃抢锁，抛出中断异常
 * lock()：如果线程被其他线程中断，则仍会继续抢锁，直到获取锁之后再抛出异常
 */
public class ReentrantLockDemo {

    ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        ReentrantLockDemo demo = new ReentrantLockDemo();
        demo.run();
    }

    public void run(){
        // 测试tryLock()
        new Thread(new MyThread(1500)).start();
        new Thread(new MyThread(1500)).start();
        // 测试lock()与lockInterruptibly()的区别
        new Thread(new MyThread(1500, false)).start();
        Thread thread = new Thread(new MyThread(1500, true));
        thread.start();
        thread.interrupt();
        Thread thread2 = new Thread(new MyThread(1500, false));
        thread2.start();
        thread2.interrupt();
    }

    private class MyThread implements Runnable{
        private long time;
        // 是否使用可中断的加锁方法lockInterruptibly()
        private Boolean interrupt = null;

        MyThread(long time){
            this.time = time;
        }

        MyThread(long time, Boolean interrupt){
            this.time = time;
            this.interrupt = interrupt;
        }

        @Override
        public void run() {
            if (interrupt == null){
                tryLockTest();
            } else{
                lockInterruptiblyTest();
            }
        }

        /**
         * 测试tryLock()
         */
        private void tryLockTest(){
            try {
                boolean flag = lock.tryLock(1, TimeUnit.SECONDS);
                if (flag){
                    Thread.sleep(time);
                    System.out.println(Thread.currentThread().getName() + "：运行完毕");
                } else{
                    System.out.println(Thread.currentThread().getName() + "：抢锁超时");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        /**
         * 测试lock()与lockInterruptibly()的区别
         */
        private void lockInterruptiblyTest(){
            try {
                if (interrupt){
                    lock.lockInterruptibly();
                } else{
                    lock.lock();
                }
                Thread.sleep(time);
                System.out.println(Thread.currentThread().getName() + "执行完毕");
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " 被打断，是否持有锁：" + lock.isHeldByCurrentThread());
//                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

    }
}
