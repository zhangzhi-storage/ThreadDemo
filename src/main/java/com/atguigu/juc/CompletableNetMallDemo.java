package com.atguigu.juc;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompletableNetMallDemo {
    /**
     * 比价需求
     * 输出同一个商品在不同店铺的价格
     * CompletableFuture可以做异步多线程并发的，而且不阻塞，从2秒优化了0.5秒
     * CompletableFuture默认使用的forkjoin线程池，我自己也优化了线程池，手写了ThreadPoolExecutor，如果是CPU密集型,CPU核数+1,IO密集型要说阻塞系数,把它用在CompletableFuture里面,把每个接口挨个调用变成了异步编排，性能上有极致的提升
     */
    static List<NetMall> list = Arrays.asList(
            new NetMall("jd"),
            new NetMall("pdd"),
            new NetMall("taobao"),
            new NetMall("dangdangwang"),
            new NetMall("tmall")
    );

    //同步
    public static List<String> getPiceByStep(List<NetMall> list, String productName) {
        return list.stream().map(netMall -> String.format(productName + " in %s price is %.2f", netMall.getMallName(), netMall.calPrice(productName))).collect(Collectors.toList());
    }

    //异步，多箭齐发
    //List<NetMall> ---  List<String>
    public static List<String> getPiceByASync(List<NetMall> list, String productName) {
       /* return list.stream().map(netMall ->
                CompletableFuture.supplyAsync(() ->
                        String.format(productName + " in %s price is %.2f", netMall.getMallName(), netMall.calPrice(netMall.getMallName()))
                )
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList());*/
        //List<NetMall>  --->         List<CompletableFuture<String>> --->  List<String>
        List<CompletableFuture<String>> completableFutureList = list.stream().map(netMall -> CompletableFuture.supplyAsync(() -> String.format(productName + " in %s price is %.2f", netMall.getMallName(), netMall.calPrice(netMall.getMallName())))).collect(Collectors.toList());
        List<String> collect1 = completableFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList());
        return collect1;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        List<String> list1 = getPiceByStep(list, "mysql");
        for (String s : list1) {
            System.out.println(s);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("----costTime1:"+(endTime-startTime)/1000.0+"秒");


        long startTime2 = System.currentTimeMillis();
        List<String> list2 = getPiceByASync(list, "mysql");
        for (String s : list2) {
            System.out.println(s);
        }
        long endTime2 = System.currentTimeMillis();
        System.out.println("----costTime2:"+(endTime2-startTime2)/1000.0+"秒");
     /*   System.out.println(CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
         //   int i=10/0;
            return 1;
        }).whenComplete((v, e) -> {
            if (e == null) {
                System.out.println("--------result:" + v);
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        }).join());
        System.out.println("-------main thread over");
        *//**
         * join方法获取值不会抛出异常 get方法获取值会抛出异常
         *//*
      //  System.out.println(completableFuture.get());//以前的老future,会发生阻塞

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}

class NetMall {

    private String mallName;

    public String getMallName() {
        return mallName;
    }

    public NetMall(String mallName) {
        this.mallName = mallName;
    }

    public double calPrice(String productName) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //模拟商品价格
        return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);
    }
}
