package org.netty.demo.chat.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class BossGroup implements Runnable {

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    public BossGroup(int port) {

        try {
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            //设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            SelectionKey sk = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
//            指定附加对象
            sk.attach(new Acceptor(serverSocketChannel));
            InetSocketAddress addr = new InetSocketAddress(port);
            serverSocketChannel.socket().bind(addr);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            System.out.println("waiting for new event on port:" + serverSocketChannel.socket().getLocalPort());
            try {
                //没有连接进来，则持续等待
                if (selector.select() == 0) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                dispatch(iterator.next());
                //调用完后移除
                iterator.remove();
            }
        }

    }

    public void dispatch(SelectionKey selectionKey) {
        //调用run方法
        Runnable runnable = (Runnable) selectionKey.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }
}
