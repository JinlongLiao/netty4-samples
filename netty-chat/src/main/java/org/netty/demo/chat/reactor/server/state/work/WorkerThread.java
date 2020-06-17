package org.netty.demo.chat.reactor.server.state.work;

import org.netty.demo.chat.reactor.server.handler.TcpHandler;
import org.netty.demo.chat.reactor.server.state.ReadState;

/**
 * 工作者线程
 */
public class WorkerThread implements Runnable {

    private final ReadState readState;
    private TcpHandler tcpHandler;
    private String message;

    public WorkerThread(ReadState readState, TcpHandler tcpHandler, String message) {
        this.tcpHandler = tcpHandler;
        this.message = message;
        this.readState = readState;
    }

    @Override
    public void run() {
        readState.process(tcpHandler, message);
    }
}
