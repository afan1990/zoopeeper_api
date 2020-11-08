package com.test.zoopeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZookeeperCreate {

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
    public void create() throws Exception{
        //1.节点路径
        //2.节点数据
        //3.权限列表 ZooDefs.Ids.OPEN_ACL_UNSAFE world:anyone:cdrwa
        //4.节点类型
        zooKeeper.create("/create/node1","note1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void create2() throws Exception{
        //ZooDefs.Ids.READ_ACL_UNSAFE 只读 R
        zooKeeper.create("/create/node2","note2".getBytes(), ZooDefs.Ids.READ_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void create3() throws Exception{
        //world授权模式
        //权限列表
        List<ACL> acls = new ArrayList<>();
        //授权模式和授权对象
        Id id = new Id("world","anyone");
        //权限设置
        acls.add(new ACL(ZooDefs.Perms.READ,id));
        acls.add(new ACL(ZooDefs.Perms.WRITE,id));
        zooKeeper.create("/create/node3","note3".getBytes(), acls, CreateMode.PERSISTENT);
    }

    @Test
    public void create4() throws Exception{
        //ip授权模式
        //权限列表
        List<ACL> acls = new ArrayList<>();
        //授权模式和授权对象
        Id id = new Id("ip","127.0.0.1");
        //权限设置
        acls.add(new ACL(ZooDefs.Perms.ALL,id));
        zooKeeper.create("/create/node4","note4".getBytes(), acls, CreateMode.PERSISTENT);
    }

    @Test
    public void create5() throws Exception{
        //auth授权模式
        //添加授权用户
        zooKeeper.addAuthInfo("digest","fanbo:123456".getBytes());
        zooKeeper.create("/create/node5","note5".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
    }

    @Test
    public void create6() throws Exception{
        //auth授权模式
        //添加授权用户
        zooKeeper.addAuthInfo("digest","fanbo:123456".getBytes());
        //权限列表
        List<ACL> acls = new ArrayList<>();
        //授权模式和授权对象
        Id id = new Id("auth","fanbo");
        //权限设置
        acls.add(new ACL(ZooDefs.Perms.READ,id));
        zooKeeper.create("/create/node6","note6".getBytes(), acls, CreateMode.PERSISTENT);
    }

    @Test
    public void create7() throws Exception{
        //digest授权模式
        List<ACL> acls = new ArrayList<>();
        //授权模式和授权对象
        Id id = new Id("digest","fanbo:Eh0M41fj8H6v5ARfvWFMCSjrvbE=");
        //权限设置
        acls.add(new ACL(ZooDefs.Perms.ALL,id));
        zooKeeper.create("/create/node7","note7".getBytes(), acls, CreateMode.PERSISTENT);
    }

    @Test
    public void create8() throws Exception{
        //持久化顺序节点
        //OPEN_ACL_UNSAFE world:anyone:cdrwa
        String result = zooKeeper.create("/create/node8","node8".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(result);
    }

    @Test
    public void create9() throws Exception{
        //临时节点
        //OPEN_ACL_UNSAFE world:anyone:cdrwa
        String result = zooKeeper.create("/create/node9","node9".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        System.out.println(result);
    }

    @Test
    public void create10() throws Exception{
        //临时顺序节点
        //OPEN_ACL_UNSAFE world:anyone:cdrwa
        String result = zooKeeper.create("/create/node10","node10".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(result);
    }

    @Test
    public void create11() throws Exception{
        //异步方式创建节点
        zooKeeper.create("/create/node11","node11".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT,new AsyncCallback.StringCallback(){
            @Override
            public void processResult(int i, String s, Object o, String s1) {
                // 0 创建成功
                System.out.println(i);
                //节点路径
                System.out.println(s);
                //节点路径
                System.out.println(s1);
                //上下文参数
                System.out.println(o);
            }
        },"I am context");
        Thread.sleep(10000);
        System.out.println("结束");
    }
}
