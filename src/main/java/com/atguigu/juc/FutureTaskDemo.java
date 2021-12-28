package com.atguigu.juc;

import java.util.concurrent.*;

public class FutureTaskDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            System.out.println(Thread.currentThread().getName() + "\t" + "----come in");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1024;
        });
        new Thread(futureTask,"t1").start();
       // System.out.println(futureTask.get());//不见不散，只要出现get方法,不管是否计算完成都阻塞等待结果出来再运行
      //  System.out.println(futureTask.get(2L, TimeUnit.SECONDS));//过时不候，工作中使用这个
        //不要阻塞，尽量用轮询替代
        while(true){
            if(futureTask.isDone()){
                System.out.println("----result:"+futureTask.get());
                break;
            }else{
                System.out.println("还在计算中");
            }
        }

    }
}
