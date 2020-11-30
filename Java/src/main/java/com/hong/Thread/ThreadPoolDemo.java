package com.hong.Thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolDemo {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        pool.submit(new MyRunnable(true));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pool.submit(new MyRunnable(false));
        pool.submit(new MyRunnable(false));

        pool.shutdown();
    }
}

class MyRunnable implements Runnable{

    boolean flag;

    public MyRunnable(boolean flag){
        this.flag = flag;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + ": " + StaticMethod.getName(this.flag));
    }
}