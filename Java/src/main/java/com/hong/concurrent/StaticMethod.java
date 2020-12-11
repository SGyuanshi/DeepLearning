package com.hong.concurrent;

public final class StaticMethod {

    private static final String name = "abc";

    public static String getName(boolean flag){
        synchronized (name){
            if (flag){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return name;
    }
}
