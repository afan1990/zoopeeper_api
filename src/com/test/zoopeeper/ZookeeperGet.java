package com.test.zoopeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZookeeperGet {
    ZooKeeper zooKeeper;

    String ip ="127.0.0.1:2181";

    @Before
    public void before() throws Exception{
        //计数器对象
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //1.服务器的ip和端口
        //2.客户端与服务器之间的会话超时时间，单位毫秒
        //3.监视器对象
        zooKeeper = new ZooKeeper(ip, 5000, new Watcher() {
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
    }

    @After
    public void after() throws Exception{
        zooKeeper.close();
    }

    @Test
    public void get1() throws Exception{
        Stat stat = new Stat();
        //1.节点路径
        //3.读取节点属性对象
        byte[] bys = zooKeeper.getData("/get/node1",false,stat);
        System.out.println(new String(bys));
        System.out.println(stat.getAversion());
    }

    @Test
    public void get2() throws Exception{
        //异步方式
        zooKeeper.getData("/get/node1", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                // 0代表读取成功
                System.out.println(i);
                //节点路径
                System.out.println(s);
                //上下文参数对象
                System.out.println(o);
                //数据
                System.out.println(bytes);
                //属性对象
                System.out.println(stat);
            }
        },"I am context");
        Thread.sleep(2000);
        System.out.println("结束");
    }
}
