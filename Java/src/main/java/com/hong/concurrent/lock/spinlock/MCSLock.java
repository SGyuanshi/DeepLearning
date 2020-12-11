package com.hong.concurrent.lock.spinlock;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * MCSLock：通过一个队列来实现对线程的排队，实现公平性。
 * 并且自旋是对本地变量（线程对应的节点），不是全局的共享资源，减少资源的消耗
 * 1、维护一个队列更新器，来获取最后的一个线程；
 * 2、当线程申请锁时，会与队列中的上一个线程连接，等到上一个线程释放锁；
 * 3、当释放锁时，没有其他线程在等待时直接释放，否则需要考虑此期间是否有新的线程进来。
 */
public class MCSLock {

    /**
     * 节点类，用于创建线程排队链表，可实现公平性
     */
    private class MCSNode{
        volatile MCSNode next;
        volatile boolean isLock = true;
    }
    // 每个线程存储自己的MCSNode，防止被修改
    private final ThreadLocal<MCSNode> LOCAL = new ThreadLocal<>();
    // MCSNode队列
    private volatile MCSNode queue;
    // 队列更新器，一直为最后的一个线程持有的锁
    private final AtomicReferenceFieldUpdater<MCSLock, MCSNode> UPDATER = AtomicReferenceFieldUpdater.newUpdater(
            MCSLock.class, MCSNode.class, "queue");

    public void lock(){
        MCSNode currentNode = new MCSNode();
        LOCAL.set(currentNode);
        // 获取当前线程的上一个线程的节点
        MCSNode preNode = UPDATER.getAndSet(this, currentNode);
        if (preNode != null){ // 其他线程在持有锁
            // 建立队列，排队在上一个线程的下一个
            preNode.next = currentNode;
            while (currentNode.isLock){ // 当上一个线程释放锁时，会将该线程的isLock置为false
                // 自旋
            }
        }
        // 没有其他线程在持有锁
    }

    public void unlock(){
        MCSNode currentNode = LOCAL.get();
        if (currentNode.next == null){ // 没有其他线程在等待
            if (!UPDATER.compareAndSet(this, currentNode, null)){ // 此时刚好有其他线程在申请锁
                while (currentNode.next == null){ // 直到其他线程申请锁完毕，形成列队关系
                    // 自旋
                }
            }
            // 正常释放锁
            return;
        }
        // 有其他线程在等待
        currentNode.next.isLock = false; // 释放锁，让下一个线程可用
        currentNode.next = null; // 垃圾回收当前线程的节点
    }
}
