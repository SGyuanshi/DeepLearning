package com.hong.concurrent.lock.spinlock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 维护一个ThreadLocal<CLHNode> PRE_LOCAL来存储上一个线程
 * ThreadLocal<CLHNode> CUR_LOCAL来存储当前线程
 * AtomicReference<CLHNode> tail记录最后一个申请锁的线程
 * 1、当线程申请锁时，通过tail来获取上一个线程和更新为当前线程，并分别存入PRE_LOCAL和CUR_LOCAL
 * 一直自旋，直到上一个线程释放锁；
 * 2、当线程释放锁时，将持有锁的状态置为false。
 */
public class CLHLock {

    // 当前线程的节点
    private final ThreadLocal<CLHNode> CUR_LOCAL = new ThreadLocal<>();
    // 上一个线程的节点
    private final ThreadLocal<CLHNode> PRE_LOCAL = new ThreadLocal<>();
    // 最后一个线程的节点
    private AtomicReference<CLHNode> tail = new AtomicReference<>();

    public void lock(){
        CLHNode cur = new CLHNode();
        CUR_LOCAL.set(cur);
        // 获取上一个获取锁的线程
        CLHNode pre = tail.getAndSet(cur);
        if (pre != null){ // 锁被其他线程持有
            PRE_LOCAL.set(pre);
            while (pre.isLock){
                // 自旋
            }
        }
        // 没有其他线程持有锁
    }

    public void unlock(){
        CLHNode cur = CUR_LOCAL.get();
        // 将锁释放，让下一个线程可以使用
        cur.isLock = false;
    }

    /**
     * isLock来记录是否持有锁
     */
    private class CLHNode{
        private volatile boolean isLock = true;
    }
}
