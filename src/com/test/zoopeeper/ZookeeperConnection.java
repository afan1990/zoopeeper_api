package com.test.zoopeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZookeeperConnection {

    public static void main(String[] args) {
        try{
            //计数器对象
            CountDownLatch countDownLatch = new CountDownLatch(1);
            //1.服务器的ip和端口
            //2.客户端与服务器之间的会话超时时间，单位毫秒
            //3.监视器对象
            ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState()== Event.KeeperState.SyncConnected){
                        System.out.println("连接创建成功");
                        //通知主线程继续向下执行
                        countDownLatch.countDown();
                    }
                }
            });
            //zookeeper创建过程为异步创建
            //主线程阻塞等待连接对象创建成功（process方法 执行完毕后）
            countDownLatch.await();
            //会话ID
            System.out.println(zooKeeper.getSessionId());
            zooKeeper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
