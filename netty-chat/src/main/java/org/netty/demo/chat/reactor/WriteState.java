package org.netty.demo.chat.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public class WriteState implements HandlerState {
    private TcpHandler tcpHandler;
    private String str;

    public WriteState(TcpHandler tcpHandler, String str) {
        this.tcpHandler = tcpHandler;
        this.str = str;
    }

    @Override
    public void handler(TcpHandler tcpHandler, SelectionKey selectionKey, SocketChannel socketChannel, ThreadPoolExecutor pool) throws IOException {
        String str = "Server Say:\"Your message has send to is:" + this.str + "\"";
        ByteBuffer buf = ByteBuffer.wrap(str.getBytes());
        while (buf.hasRemaining()) {
            //回传给client的回应字符串，发送buf的position位置，到limit位置为止之间的内容
            socketChannel.write(buf);
        }
        //通过key改变通道注册的事件
        selectionKey.interestOps(SelectionKey.OP_READ);
        //使一个阻塞住的selector操作立即返回
        selectionKey.selector().wakeup();
        tcpHandler.setState(new ReadState());
    }

    @Override
    public void changeState(TcpHandler h, SelectionKey key, SocketChannel sc, ThreadPoolExecutor pool) throws IOException {

    }
}
