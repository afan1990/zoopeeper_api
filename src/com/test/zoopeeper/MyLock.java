package com.test.zoopeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

//分布式锁设计
public class MyLock {

    String ip = "127.0.0.1";
    //计数对象
    CountDownLatch countDownLatch = new CountDownLatch(1);

    //zookeeper配置信息
    ZooKeeper zookeeper = null;
    private static final String LOCK_ROOT_PATH = "/Locks";
    private static final String LOCK_NODE_NAME = "/Lock_";
    private String lockPath;

    //打开zookeeper连接
    public MyLock(){
        try{
            zookeeper = new ZooKeeper(ip, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType() == Event.EventType.None){
                        if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                            System.out.println("连接成功");
                            countDownLatch.countDown();
                        }
                    }
                }
            });
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //获取锁
    public void acquireLock() throws Exception{
        createLock();
        attemptLock();
    }

    //创建节点
    public void createLock() throws Exception{
        //判断Locks节点是否存在
        Stat stat = zookeeper.exists(LOCK_ROOT_PATH,false);
        if(stat == null){
            zookeeper.create(LOCK_ROOT_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        //创建临时有序节点
        lockPath = zookeeper.create(LOCK_ROOT_PATH+LOCK_NODE_NAME,new byte[0],ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("节点创建成功:"+lockPath);

    }
    //监视器对象,监视上一个节点是否被删除
    Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType() == Event.EventType.NodeDeleted){
                synchronized (this){
                    notifyAll();
                }
            }
        }
    };

    //尝试获取锁
    public void attemptLock() throws Exception{
        //获取Locks节点下的所有子节点
        List<String> list = zookeeper.getChildren(LOCK_ROOT_PATH,false);
        //对子节点进行排序
        Collections.sort(list);
        //  /Locks/Lock_0000000000001
        int index = list.indexOf(lockPath.substring(LOCK_ROOT_PATH.length()+1));
        if(index == 0){
            System.out.println("获取锁成功");
            return;
        }else{
            //获取上一个节点的索引位置
            String path = list.get(index - 1);
            Stat stat = zookeeper.exists(LOCK_ROOT_PATH+"/"+path,watcher);
            if(stat == null){
                attemptLock();
            }else{
                synchronized (watcher){
                    watcher.wait();
                }
                attemptLock();
            }
        }

    }

    //释放锁
    public void releaseLock() throws Exception{
        zookeeper.delete(this.lockPath,-1);
        zookeeper.close();
        System.out.println("锁已经释放:"+this.lockPath);
    }

    public static void main(String[] args) throws Exception{
        MyLock lock = new MyLock();
        lock.createLock();
    }

}
