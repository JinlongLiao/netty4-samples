package org.netty.demo.chat.reactor.server.accptor;

import org.netty.demo.chat.reactor.server.handler.TcpHandler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public class Acceptor implements Runnable {

    private final ServerSocketChannel serverSocketChannel;
    private final int cores;
    private AtomicInteger atomicInteger;
    private final Selector[] selectors;
    private int selIndex = 0;
    private TCPSubReactor tcpSubReactors[];
    private Thread[] threads;

    public Acceptor(ServerSocketChannel serverSocketChannel, int cores) {
        this.cores = cores;
        this.atomicInteger = new AtomicInteger(cores);
        this.serverSocketChannel = serverSocketChannel;
        this.selectors = new Selector[cores];
        this.tcpSubReactors = new TCPSubReactor[cores];
        this.threads = new Thread[cores];

        try {
            for (int i = 0; i < cores; i++) {
                selectors[i] = Selector.open();
                tcpSubReactors[i] = new TCPSubReactor(selectors[i], serverSocketChannel, i);
                threads[i] = new Thread(tcpSubReactors[i]);
                threads[i].start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            selIndex = atomicInteger.getAndIncrement() % cores;
            //接受client连接请求
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                System.out.println("client has connected,ip is" + socketChannel.getLocalAddress());
                socketChannel.configureBlocking(false);
                tcpSubReactors[selIndex].setRestart(true);
                //使一个阻塞住的selector操作立即返回
                selectors[selIndex].wakeup();
                SelectionKey selectionKey = socketChannel.register(selectors[selIndex], SelectionKey.OP_READ);
//               /r[selIdx].
                selectors[selIndex].wakeup();
                selectionKey.attach(new TcpHandler(selectionKey, socketChannel));
                //重放线程
                tcpSubReactors[selIndex].setRestart(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
