package com.hong.concurrent.threadpool;

import com.hong.concurrent.StaticMethod;

import java.util.concurrent.*;

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

        FutureTask<String> task = new FutureTask<>(new MyCallable());
        pool.submit(new Thread(task));
        // isDone()查询任务是否执行完毕
        task.isDone();
        // get()获取结果，该方法会阻塞，直到任务完成成功返回结果
        try {
            Object result = task.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        pool.shutdown();
    }
}

class MyRunnable implements Runnable{

    boolean flag;

    MyRunnable(boolean flag){
        this.flag = flag;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + ": " + StaticMethod.getName(this.flag));
    }
}

/**
 * 实现Callable接口，可创建带返回值的线程
 */
class MyCallable implements Callable<String>{

    @Override
    public String call() throws Exception {
        return "return call";
    }
}