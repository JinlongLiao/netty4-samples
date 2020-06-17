package org.netty.demo.chat.reactor.server.accptor;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TCPSubReactor implements Runnable {
    private final ServerSocketChannel ssc;
    private final Selector selector;
    private boolean restart = false;
    int num;

    public TCPSubReactor(Selector selector, ServerSocketChannel ssc, int num) {
        this.ssc = ssc;
        this.selector = selector;
        this.num = num;
    }

    @Override
    public void run() {
        //在线程被中断前持续运行
        while (!Thread.interrupted()) {
            //  System.out.println("waiting for restart");
            //在线程被中断前以及被指定重启前持续运行
            while (!Thread.interrupted() && !restart) {
                try {
                    if (selector.select() == 0) {
                        //若没有事件就绪则不往下执行
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //取得所有已就绪事件的key集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()) {
                    //根据事件的key进行调度
                    dispatch(it.next());
                    it.remove();
                }

            }
        }
    }

    public void dispatch(SelectionKey s) {
        //调用run方法
        Runnable attachment = (Runnable) s.attachment();
        if (attachment != null) {
            attachment.run();
        }
    }


    public void setRestart(boolean restart) {
        this.restart = restart;
    }
}
