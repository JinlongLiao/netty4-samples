package org.netty.demo.io.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author liaojinlong
 */
public class Handler implements Runnable {

    final SocketChannel socket;
    final SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(Integer.MAX_VALUE);
    ByteBuffer output = ByteBuffer.allocate(Integer.MAX_VALUE);
    static final int READING = 0, SENDING = 1;
    int state = READING;


    public Handler(Selector selector, SocketChannel socketChannel) throws IOException {
        socket = socketChannel;
        //设置为非阻塞模式
        socketChannel.configureBlocking(false);
        //此处的0，表示不关注任何时间
        sk = socket.register(selector, 0);
        //将SelectionKey绑定为本Handler 下一步有事件触发时，将调用本类的run方法
        sk.attach(this);
        //将SelectionKey标记为可读，以便读取，不可关注可写事件
        sk.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    boolean inputIsComplete() {
        return false;
    }

    boolean outputIsComplete() {
        return false;
    }

    //这里可以通过线程池处理数据
    void process() {

    }


    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) { /* ... */ }

    }


    void read() throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            process();
            state = SENDING;
            // Normally also do first write now
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }

    void send() throws IOException {
        socket.write(output);
        if (outputIsComplete()) {
            //
            sk.cancel();
        }
    }

}