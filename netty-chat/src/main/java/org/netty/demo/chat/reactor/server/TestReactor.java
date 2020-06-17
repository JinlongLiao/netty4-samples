package org.netty.demo.chat.reactor.server;

import org.netty.demo.chat.reactor.server.boss.BossGroup;

public class TestReactor {
    public static void main(String[] args) {
        BossGroup bossGroup = new BossGroup(8080);
        Thread t = new Thread(bossGroup);
        t.start();
    }
}
