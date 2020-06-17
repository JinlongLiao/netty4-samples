package org.netty.demo.chat.reactor.server.state;

import io.netty.util.internal.StringUtil;
import org.netty.demo.chat.reactor.server.handler.TcpHandler;
import org.netty.demo.chat.reactor.server.state.work.WorkerThread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author liaojinlong
 */
public class ReadState implements HandlerState {
    private SelectionKey selectionKey;

    @Override
    public void handler(TcpHandler tcpHandler, SelectionKey selectionKey, SocketChannel socketChannel, ThreadPoolExecutor pool) throws IOException {
        this.selectionKey = selectionKey;
        //non-blocking 下不可用Readers 因为Readers不支持non-blocking
        byte[] arr = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(arr);
//读取字符串
        int numBytes = socketChannel.read(buf);
        if (numBytes == -1) {
            System.out.println("[Warning!] A client has been closed");
            tcpHandler.closeChannel();
            return;
        }//将读取到的byte内容转为字符串
        String messasge = new String(arr);
        System.out.println(tcpHandler.getSocketChannel().getLocalAddress().toString() + "说：" + messasge);
        if (!StringUtil.isNullOrEmpty(messasge)) {
            //改变状态
            tcpHandler.setState(new WorkState(tcpHandler, messasge));
            //do process in worker thread
            pool.execute(new WorkerThread(this, tcpHandler, messasge));
        }
    }

    @Override
    public void changeState(TcpHandler h, SelectionKey key, SocketChannel sc, ThreadPoolExecutor pool) throws IOException {

    }

    public synchronized void process(TcpHandler tcpHandler, String str) {

        //处理业务逻辑
        //do process(decode,logically process, encode)..
        tcpHandler.setState(new WriteState(tcpHandler, str));//改变状态（Working -> Sending）
        this.selectionKey.interestOps(SelectionKey.OP_WRITE);//通过key改变通道注册事件
        this.selectionKey.selector().wakeup();//使一个阻塞住的selector操作立即返回
    }

}