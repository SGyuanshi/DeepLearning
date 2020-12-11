package com.hong.concurrent.lock.spinlock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 可重入的自旋锁：通过count来记录线程获取锁的次数
 */
public class ReentrantSpinLock {

    private AtomicReference<Thread> cas = new AtomicReference<>();
    // 该线程获取锁的次数
    private int count;

    public void lock(){
        Thread current = Thread.currentThread();
        if (current == cas.get()){ // 当前线程已经取到锁了
            count += 1;
        }
        while (!cas.compareAndSet(null, current)){
            // Do Nothing
        }
    }

    public void unlock(){
        if (count > 0){ // 当前线程多次获取该锁，并未全部释放
            count -= 1;
        } else{ // 当前线程获取的锁都已释放
            cas.compareAndSet(Thread.currentThread(), null);
        }
    }
}
