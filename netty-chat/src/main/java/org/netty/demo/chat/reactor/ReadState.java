package org.netty.demo.chat.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public class ReadState implements HandlerState {
    private SelectionKey selectionKey;

    @Override
    public void handler(TcpHandler tcpHandler, SelectionKey sk, SocketChannel sc, ThreadPoolExecutor pool) throws IOException {
        this.selectionKey = sk;
        //non-blocking 下不可用Readers 因为Readers不支持non-blocking
        byte[] arr = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(arr);
//读取字符串
        int numBytes = sc.read(buf);
        if (numBytes == -1) {
            System.out.println("[Warning!] A client has been closed");
            tcpHandler.closeChannel();
            return;
        }//将读取到的byte内容转为字符串
        String str = new String(arr);
        System.out.println(tcpHandler.getSocketChannel().getLocalAddress().toString() + "说：" + str);
        if ((str != null) && !str.equals("")) {
            //改变状态
            tcpHandler.setState(new WorkState(tcpHandler, str));
            //do process in worker thread
            pool.execute(new WorkerThread(tcpHandler, str));
        }
    }

    @Override
    public void changeState(TcpHandler h, SelectionKey key, SocketChannel sc, ThreadPoolExecutor pool) throws IOException {

    }

    synchronized void process(TcpHandler tcpHandler, String str) {

        //处理业务逻辑
        //do process(decode,logically process, encode)..
        tcpHandler.setState(new WriteState(tcpHandler, str));//改变状态（Working -> Sending）
        this.selectionKey.interestOps(SelectionKey.OP_WRITE);//通过key改变通道注册事件
        this.selectionKey.selector().wakeup();//使一个阻塞住的selector操作立即返回
    }

    /**
     * 工作者线程
     */
    class WorkerThread implements Runnable {

        TcpHandler h;
        String str;

        public WorkerThread(TcpHandler h, String str) {
            this.h = h;
            this.str = str;
        }

        @Override
        public void run() {
            process(h, str);
        }
    }
}
