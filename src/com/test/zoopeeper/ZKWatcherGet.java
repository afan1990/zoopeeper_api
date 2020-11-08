package com.test.zoopeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKWatcherGet {

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
    public void get() throws Exception{
        //1.节点路径
        //2.使用连接对象watcher
        zooKeeper.getData("/watcher1",true,null);
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void get2() throws Exception{
        zooKeeper.getData("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType:"+watchedEvent.getType());
            }
        },null);
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void get3() throws Exception{
        //一次性
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType:"+watchedEvent.getType());
                if(watchedEvent.getType() == Event.EventType.NodeDataChanged){
                    try {
                        zooKeeper.getData("/watcher1",this,null);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        zooKeeper.getData("/watcher1",watcher,null);
        Thread.sleep(50000);
        System.out.println("结束");
    }

    @Test
    public void get4() throws Exception{
        //注册多个监听器对象
        zooKeeper.getData("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("1");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType:"+watchedEvent.getType());
                if(watchedEvent.getType() == Event.EventType.NodeDataChanged){
                    try {
                        zooKeeper.getData("/watcher1",this,null);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, null);
        zooKeeper.getData("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("2");
                System.out.println("path:"+watchedEvent.getPath());
                System.out.println("eventType:"+watchedEvent.getType());
                if(watchedEvent.getType() == Event.EventType.NodeDataChanged){
                    try {
                        zooKeeper.getData("/watcher1",this,null);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, null);
        Thread.sleep(50000);
        System.out.println("结束");
    }
}
