package com.test.zoopeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKWatcherExists {

    ZooKeeper zooKeeper;

    String ip ="127.0.0.1:2181";

    @Before
    public void before() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(ip, 6000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("连接对象的参数");
                if(watchedEvent.getState()== Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                }
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType:"+watchedEvent.getType());
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws Exception{
        zooKeeper.close();
    }

    @Test
    public void exist1() throws Exception{
        //1.节点路径
        //2.使用连接对象中的watcher
        zooKeeper.exists("/watcher1",true);
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void exist2() throws Exception{
        //1.节点路径
        //2.自定义watcher对象
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType:"+watchedEvent.getType());
            }
        });
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void exist3() throws Exception{
        //watcher 一次性(一次注册一次通知)
        Watcher watcher = new Watcher() {
            @Override

            public void process(WatchedEvent watchedEvent) {
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType:"+watchedEvent.getType());
                //此句功能是 监听该节点 多个事件通知 如果不加 只能监听一次
                try {
                    zooKeeper.exists("/watcher1",this);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        zooKeeper.exists("/watcher1",watcher);
        Thread.sleep(80000);
        System.out.println("结束");
    }

    @Test
    public void exist4() throws Exception{
        //注册多个监听器对象
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("1");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType:"+watchedEvent.getType());
            }
        });

        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("2");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType:"+watchedEvent.getType());
            }
        });

        Thread.sleep(80000000);
        System.out.println("结束");
    }
}
