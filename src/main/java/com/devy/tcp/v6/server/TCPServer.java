package com.devy.tcp.v6.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author: Devy
 * @create: 2020-04-20 14:03
 **/
public class TCPServer implements ClientHandlerCallBack {

    private final int port;

    private final ExecutorService fowardingThreadPoolExecutor;

    private ClientListener clientListener;

    private Selector selector;

    ServerSocketChannel server;

    private List<ClientHandler> clientHandlerList=new ArrayList<ClientHandler>();


    public  TCPServer (int port){
        this.port=port;
        this.fowardingThreadPoolExecutor=Executors.newSingleThreadExecutor();
    }

    public synchronized void broadcast(String str) {
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str);
        }
    }

    public boolean start(){
        try {
            selector =Selector.open();
            server=ServerSocketChannel.open();

            server.configureBlocking(false);

            server.socket().bind(new InetSocketAddress(port));

            server.register(selector,SelectionKey.OP_ACCEPT);

            ClientListener clientListener=new ClientListener(selector,this);
            this.clientListener=clientListener;
            clientListener.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public void stop(){
        if(null!=clientListener){
            clientListener.exit();
        }

        try {
            selector.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (TCPServer.this) {
            for (ClientHandler clientHandler : clientHandlerList) {
                clientHandler.exit();
            }

            clientHandlerList.clear();
        }
    }


    @Override
    public void onSelfClosed(ClientHandler handler) {
        clientHandlerList.remove(handler);


    }

    @Override
    public void onNewMessageArrived(ClientHandler handler, String msg) {

        System.out.println("Received-" + ":" + msg);
        // 异步提交转发任务
        fowardingThreadPoolExecutor.execute(() -> {
            synchronized (TCPServer.this) {
                for (ClientHandler clientHandler : clientHandlerList) {
                    if (clientHandler.equals(handler)) {
                        // 跳过自己
                        continue;
                    }
                    // 对其他客户端发送消息
                    clientHandler.send(msg);
                }
            }
        });
    }

    @Override
    public void onConnected(ClientHandler handler) {
        synchronized (TCPServer.this){
            clientHandlerList.add(handler);
        }

    }
}
