package com.hong.concurrent.synchronizer;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * 同步器CountDownLatch，初始化设置计数数量
 * 每次调用countDown方法，计数数量就会减1，直到等于0。
 * 线程调用await方法，会进行阻塞，直到计数数量等于0时，才开始执行
 * 当计数数量等于0时，再继续调用countDown，会直接返回false退出。
 * 即实际线程时大于初始设定计数数量n时，await只会等待前n个线程
 */
public class CountDownLatchDemo {

    private int numRunner = 100;

    // 起跑同步器
    private CountDownLatch begin = new CountDownLatch(1);
    // “比赛结束”同步器
    private CountDownLatch end = new CountDownLatch(numRunner);

    public static void main(String[] args) {
        CountDownLatchDemo demo = new CountDownLatchDemo();
        demo.run();
    }

    public void run(){
        // 添加运动员线程
        for (int i=0; i<numRunner; i++){
            new Thread(new Runner()).start();
        }
        // 所有运动员开始起跑
        System.out.println("比赛开始===");
        begin.countDown();
        // 等待所有运动员到达终点，才开始输出“比赛结束”
        try {
            end.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("比赛结束！！！");
    }

    /**
     * 运动员类
     */
    private class Runner implements Runnable{

        Random random = new Random();

        @Override
        public void run() {
            try {
                // 等待其他运动员准备完毕，才开始起跑
                begin.await();
                // 模拟运动员多少秒跑到终点
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 当前线程的运动员已经到达终点
                end.countDown();
            }
        }
    }
}
