package com.devy.tcp.v3;

import com.devy.tcp.v3.constants.TCPConstant;

import java.io.IOException;

/**
 * @author: Devy
 * @create: 2020-04-18 13:59
 **/
public class Server {

    public static void main(String[] args) {
        TCPServer server=new TCPServer(TCPConstant.PORT_SERVER);
        boolean isSucceed=server.start();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.stop();

    }
}

