package com.hong.concurrent.lock.spinlock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 维护一个递增的排队号和服务号
 * 每当线程来获取锁时，就分配一个排队号，锁则对应服务号
 * 仅当排队号等于服务号时，才能获取锁
 * 这样先排队的线程可以先获取锁，实现了公平性
 */
public class FairSpinLock {

    // 排队号
    private static AtomicInteger ticketNum = new AtomicInteger();
    // 服务号
    private AtomicInteger serverNum = new AtomicInteger();
    // 每个线程存储自己的排队号，防止排队号被修改
    private ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public void lock(){
        int currentTicketNum = ticketNum.getAndIncrement();
        threadLocal.set(currentTicketNum);
        // 仅当排队号等于服务号时，才能获取锁
        while (currentTicketNum != serverNum.get()){
            // Do Nothing 自旋
        }
    }

    /**
     * 将服务号加1，相当于释放锁给下一个线程使用
     */
    public void unlock(){
        int currentTicketNum = ticketNum.get();
        serverNum.compareAndSet(currentTicketNum, currentTicketNum+1);
    }
}
