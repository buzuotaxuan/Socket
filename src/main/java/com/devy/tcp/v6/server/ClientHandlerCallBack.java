package com.devy.tcp.v6.server;

/**
 * @author: Devy
 * @create: 2020-04-20 14:51
 **/
public interface ClientHandlerCallBack {

    // 自身关闭通知
    void onSelfClosed(ClientHandler handler);

    // 收到消息时通知
    void onNewMessageArrived(ClientHandler handler, String msg);

    //链接成功
     void onConnected(ClientHandler handler);
}
