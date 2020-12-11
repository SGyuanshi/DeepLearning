package com.hong.gc;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hong
 * @Description:
 * @date 2020-12-10 11:18
 * @Project: Java
 * @Package com.hong.gc
 */
public class ReferenceDemo {

    private final static ReferenceQueue<byte[]> queue = new ReferenceQueue<>();

    public static void main(String[] args) {
        weakTest();
    }

    static void weakTest() {
        List<WeakReference<byte[]>> weakReferenceList = new ArrayList<>();

        for (int i=0; i<10; i++){
            WeakReference<byte[]> weakReference = new WeakReference<>(new byte[1024], queue);
            weakReferenceList.add(weakReference);
        }
        System.out.println("gc之前：" + getQueueSize());
        System.gc();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("gc之后：" + getQueueSize());
    }

    static int getQueueSize(){
        Reference<? extends byte[]> poll;
        int num = 0;
        while ((poll = queue.poll()) != null){
            num += 1;
        }
        return num;
    }
}
