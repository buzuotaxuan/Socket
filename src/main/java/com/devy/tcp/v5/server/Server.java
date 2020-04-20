package com.devy.tcp.v5.server;

import com.devy.tcp.v3.constants.TCPConstant;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author: Devy
 * @create: 2020-04-20 14:03
 **/
public class Server {

    public static void main(String[] args) throws Exception{
        TCPServer tcpServer = new TCPServer(TCPConstant.PORT_SERVER);
        boolean isSucceed = tcpServer.start();
        if (!isSucceed) {
            System.out.println("Start TCP server failed!");
            return;
        }


        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String str;
        do {
            str = bufferedReader.readLine();
            tcpServer.broadcast(str);
        } while (!"00bye00".equalsIgnoreCase(str));
    }
}
