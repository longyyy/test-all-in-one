package com.taobao.teaey.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xiaofei.wxf
 */
public class OIOServer {
    static class Reader implements Runnable {
        private Socket socket;

        Reader(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    int data = socket.getInputStream().read();
                    if (-1 == data) {
                        socket.close();
                        System.out.println("断开连接:" + socket);
                        return;
                    }
                    socket.getOutputStream().write(data + 1);
                    socket.getOutputStream().flush();
                    System.out.println("Read:" + data + " Write:" + (data + 1));
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        while (true) {
            Socket s = serverSocket.accept();
            new Thread(new Reader(s)).start();
            System.out.println("新连接");
        }
    }
}
