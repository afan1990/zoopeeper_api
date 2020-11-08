package com.test.zoopeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZKConnectionWacther implements Watcher{

    static CountDownLatch countDownLatch = new CountDownLatch(1);
    static ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent watchedEvent) {
        try{
            //事件类型
            if(watchedEvent.getType() == Event.EventType.None){
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接创建成功");
                    countDownLatch.countDown();
                }else if(watchedEvent.getState() == Event.KeeperState.Disconnected){
                    System.out.println("断开连接");
                }else if(watchedEvent.getState() == Event.KeeperState.Expired){
                    System.out.println("会话超时");
                }else if(watchedEvent.getState() == Event.KeeperState.AuthFailed){
                    System.out.println("认证失败");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try{
            zooKeeper = new ZooKeeper("127.0.0.1:2181",5000,new ZKConnectionWacther());
            //阻塞线程等待连接创建
            countDownLatch.await();
            //会话id
            System.out.println(zooKeeper.getSessionId());
            Thread.sleep(5000);
            zooKeeper.close();
            System.out.println("结束");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
