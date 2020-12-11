package com.hong.concurrent.synchronizer;

import java.util.concurrent.Semaphore;

/**
 * 初始化设定许可数量（控制最多同时执行的线程数）
 * acquire方法抢占一个许可，release方法释放许可
 * 当没有剩余的许可时，acquire方法会进入等待
 */
public class SemaphoreDemo {

    // 许可限额数量（即最多同时执行的线程数）：柜台数量
    private int numCounter = 5;
    // 总线程数：顾客数量
    private int numCustomer = 20;
    // 控制器
    private Semaphore semaphore = new Semaphore(numCounter);

    public static void main(String[] args) {
        SemaphoreDemo demo = new SemaphoreDemo();
        demo.run();
    }

    private void run(){
        // 顾客陆续到达，抢占空闲柜台办理服务
        for (int i=0; i<numCustomer; i++){
            new Thread(new Customer()).start();
        }
    }

    /**
     * 顾客类
     */
    private class Customer implements Runnable{

        @Override
        public void run() {
            try {
                // 抢占许可：顾客进入空闲的柜台或者等待
                semaphore.acquire();
                Thread.sleep(2000);
                // 释放许可：顾客办理完服务从柜台离开，其他顾客可以进入柜台
                semaphore.release();
                System.out.println("顾客(" + Thread.currentThread().getName() + ")完成服务办理，存在空闲柜台！");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
