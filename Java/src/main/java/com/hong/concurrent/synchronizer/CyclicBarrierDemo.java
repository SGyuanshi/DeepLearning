package com.hong.concurrent.synchronizer;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier：初始化设置计数数量N
 * 每次调用await方法，线程会进行阻塞，计数数量会减1，直到等于0，阻塞的线程开始执行
 * 当计数数量等于0时，继续调用await方法，计数数量会重新变为N-1，开始下一轮的同步
 */
public class CyclicBarrierDemo {

    // 每组进入场馆的人数
    private int numGroup = 5;
    // 总的学生人数
    private int numStudent = 20;
    // 当前第几组
    private int groupId = 1;
    // 同步器
    private CyclicBarrier cyclicBarrier = new CyclicBarrier(numGroup);

    public static void main(String[] args) {
        CyclicBarrierDemo demo = new CyclicBarrierDemo();
        demo.run();
    }

    private void run(){
        // 所有学生陆续到场
        for (int i=0; i<numStudent; i++){
            new Thread(new Student()).start();
        }
    }

    /**
     * 学生类
     */
    private class Student implements Runnable{

        @Override
        public void run() {
            try {
                // 返回当前线程是第几个进入的
                int current = cyclicBarrier.await();
                if (current == numGroup-1){ // 此时cyclicBarrier的计数为0，阻塞的线程开始执行，并进入下一轮
                    System.out.println("第 " + groupId + " 组人数到齐，进入场馆");
                    groupId += 1;
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
