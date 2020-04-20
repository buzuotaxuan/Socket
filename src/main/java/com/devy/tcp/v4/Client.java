package com.devy.tcp.v4;

/**
 * @author: Devy
 * @create: 2020-04-18 14:11
 **/
public class Client {

    public static void main(String[] args) {
        TCPClient.linkWith(TCPConstant.PORT_SERVER);
    }
}
