package com.test.zoopeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZookeeperDelete {

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
    public void delete1() throws Exception{
        //1.删除节点的节点路径
        //2.数据版本信息 -1代表删除节点时 版本号不参与删除
        zooKeeper.delete("/delete/node1",-1);
    }

    @Test
    public void delete2() throws Exception{
        //异步使用方式
        zooKeeper.delete("/delete/node2", -1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int i, String s, Object o) {
                //0 代表删除成功
                System.out.println(i);
                //节点路径
                System.out.println(s);
                //上下文参数
                System.out.println(o);
            }
        },"I am context");
        Thread.sleep(2000);
        System.out.println("结束");
    }
}
