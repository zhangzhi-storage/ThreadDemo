package com.atguigu.juc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class CompletableFutureAPIDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        //创建一个线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 20, 1L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(50), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片信息");
            return "hello.jpg";
        },threadPoolExecutor);
        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("查询商品的属性");
            return "黑色+256G";
        },threadPoolExecutor);
        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品介绍");
            return "华为";
        },threadPoolExecutor);
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);
        allOf.join();//等待所有结果完成
        System.out.println("main....end..."+futureImg.get()+"=>"+futureAttr.get()+"=>"+futureDesc.get());
    }
    private static void m9(){
        //创建一个线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 20, 1L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(50), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程:"+Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("任务1结束:");
            return i;
        },threadPoolExecutor);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程:" + Thread.currentThread().getId());
            System.out.println("任务2结束:");
            return "Hello";
        },threadPoolExecutor);
        //future1和future2只要有一个完成就开始执行任务3
        future1.runAfterEitherAsync(future2,()->{
            System.out.println("任务3开始..之前的结果:");
        },threadPoolExecutor);
    }

    private static void m8(ThreadPoolExecutor threadPoolExecutor, CompletableFuture<Integer> future1, CompletableFuture<String> future2) {
        future1.thenAcceptBothAsync(future2,(f1, f2)->{
            System.out.println("任务3开始执行...之前的结果:"+f1+"-->"+f2);
        }, threadPoolExecutor);
    }

    private static void m7(ThreadPoolExecutor threadPoolExecutor, CompletableFuture<Integer> future1, CompletableFuture<String> future2) throws InterruptedException, ExecutionException {
        /**
         public <U,V> CompletableFuture<V> thenCombineAsync(
         CompletionStage<? extends U> other,
         BiFunction<? super T,? super U,? extends V> fn, Executor executor)
         两个任务执行完成再完成C的情况
         */
        CompletableFuture<String> future3 = future1.thenCombineAsync(future2, (f1, f2) -> {
            return f1 + ":" + f2 + " -->haha";
        }, threadPoolExecutor);
        System.out.println("main...end.."+future3.get());
    }

    private static void m6() {
        //实现excel分批导入
        //合并多个接口的数据返回给前端
        //一个大Excel文件 50万记录分批导入到数据库
        System.out.println(CompletableFuture.supplyAsync(() -> {
            return 10;
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            return 20;
        }), (r1, r2) -> {
            return r1 + r2;
        }).thenCombine(CompletableFuture.supplyAsync(()->{
            return 30;
        }),(r3,r4)->{
            return r3+r4;
        }).join());
    }

    //对计算结果谁快用谁
    private static void m5() {
        //applyToEither 谁快就把谁的结果返回
        System.out.println(CompletableFuture.supplyAsync(() -> {
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(2L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 2;
        }), r -> {
            return r;
        }).join());
    }
  /*   Map<String,Object> map=new HashMap<>();
        CompletableFuture.supplyAsync(()->{
            map.put("prodcut",1);
            return map;
        }).s;*/
    /**
     * 对计算结果进行消费
     */
    private static void m4() {
        CompletableFuture.supplyAsync(()->{
            return 1;
        }).thenApply(f->{
            return f+2;
        }).thenApply(f->{
            return f+3;
        }).thenAccept(r->
                System.out.println(r));
    }


    private static void m3() {
        //创建一个线程池
        //handle 有异常仍然可以进行
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 20, 1L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(50), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        CompletableFuture.supplyAsync(()->{
            return 1;
        }).handle((f,e)->{
            System.out.println("-----1");
            int i=1/0;
            return f+2;
        }).handle((f,e) -> {
            System.out.println("-----2");
            return f + 3;
        }).handle((f,e) -> {
            System.out.println("-----3");
            return f + 4;
        }).whenComplete((v, e) -> {
            if (e == null) {
                System.out.println("------result:" + v);
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        }).join();
    }

    private static void m2() {
        //创建一个线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 20, 1L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(50), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        //第二步参数用第一步返回的结果,如果有异常就停止
        System.out.println(CompletableFuture.supplyAsync(() -> {
            return 1;
        }).thenApply(f -> {
            System.out.println("-----1");
            int i=1/0;
            return f + 2;
        }).thenApply(f -> {
            System.out.println("-----2");
            return f + 3;
        }).thenApply(f -> {
            System.out.println("-----3");
            return f + 4;
        }).whenComplete((v, e) -> {
            if (e == null) {
                System.out.println("------result:" + v);
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        }).join());
    }

    private static void m1() throws InterruptedException, ExecutionException {
        //创建一个线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 20, 1L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(50), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            //暂停几秒钟线程
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        },threadPoolExecutor);
        //System.out.println(future.get());
//        System.out.println(future.get(2L, TimeUnit.SECONDS));
        //没算完，返回设定的valueIfAbsent的值，计算完返回计算完成后的结果
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //  System.out.println(future.getNow(9999));
        System.out.println(future.complete(-44)+"\t"+future.get());
        threadPoolExecutor.shutdown();
    }
}
