package com.devy.tcp.v5.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Devy
 * @create: 2020-04-20 14:03
 **/
public class TCPServer implements ClientHandlerCallBack{

    private final int prot;

    private final ExecutorService fowardingThreadPoolExecutor;

    private ClientListener clientListener;

    private List<ClientHandler> clientHandlerList=new ArrayList<ClientHandler>();


    public  TCPServer (int port){
        this.prot=port;
        this.fowardingThreadPoolExecutor=Executors.newSingleThreadExecutor();
    }

    public synchronized void broadcast(String str) {
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str);
        }
    }

    public boolean start(){
        try {
            ClientListener  clientListener=new ClientListener(prot,this);
            this.clientListener=clientListener;
            clientListener.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
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
