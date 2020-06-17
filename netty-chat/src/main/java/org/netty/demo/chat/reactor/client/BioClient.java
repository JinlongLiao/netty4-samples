package org.netty.demo.chat.reactor.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class BioClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 8080);) {
            new Thread(() -> {
                while (true) {
                    try {
                        byte[] bytes = new byte[1024];
                        int read = socket.getInputStream().read(bytes);
                        System.out.println(new String(bytes));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            while (true) {
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()) {
                    String s = scanner.nextLine();
                    socket.getOutputStream().write(s.getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
