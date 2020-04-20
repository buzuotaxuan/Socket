package com.devy.tcp.v4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author: Devy
 * @create: 2020-04-18 13:59
 **/
public class Server {

    public static void main(String[] args) {
        TCPServer server=new TCPServer(TCPConstant.PORT_SERVER);
        boolean isSucceed=server.start();
        try {
            if(isSucceed){
                System.out.println("-------start");
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
                String str;
                do {
                    str=bufferedReader.readLine();
                    server.broadcast(str);
                }while (!str.equals("bye"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.stop();

    }
}

