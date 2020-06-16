package org.netty.demo.chat.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TcpHandler implements Runnable {
    private static ThreadPoolExecutor pool =
            new ThreadPoolExecutor(11, 32, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
    //线程池
    //以状态模式实现handler
    HandlerState state;
    private SelectionKey selectionKey;
    private SocketChannel socketChannel;

    public TcpHandler(SelectionKey sk, SocketChannel sc) {
        this.selectionKey = sk;
        this.socketChannel = sc;
        //初始状态设定为READING
        state = new ReadState();
    }

    @Override
    public void run() {
        try {
            state.handler(this, selectionKey, socketChannel, pool);
        } catch (IOException e) {
            System.out.println("warning A client has been closed");
            closeChannel();
        }
    }

    public void closeChannel() {
        try {
            selectionKey.cancel();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setState(HandlerState state) {
        this.state = state;
    }

    public static ThreadPoolExecutor getPool() {
        return pool;
    }

    public static void setPool(ThreadPoolExecutor pool) {
        TcpHandler.pool = pool;
    }

    public HandlerState getState() {
        return state;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
