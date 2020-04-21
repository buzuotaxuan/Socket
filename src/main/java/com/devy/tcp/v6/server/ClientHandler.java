package com.devy.tcp.v6.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author: Devy
 * @create: 2020-04-20 14:30
 **/
public class ClientHandler {

   private SocketChannel socket;
   private ClientHandlerCallBack clientHandlerCallBack;
   private ClientReadHandler clientReadHandler;
   private ClientWriteHandler clientWriteHandler;


    public ClientHandler(SocketChannel socketChannel, ClientHandlerCallBack clientHandlerCallBack) throws Exception{
        this.socket=socketChannel;

        this.clientHandlerCallBack=clientHandlerCallBack;
        socketChannel.configureBlocking(false);
        Selector readSelector=Selector.open();
        socketChannel.register(readSelector,SelectionKey.OP_READ);
        this.clientReadHandler=new ClientReadHandler(readSelector,this);

        Selector writerSelector=Selector.open();
        socketChannel.register(writerSelector,SelectionKey.OP_WRITE);
        this.clientWriteHandler=new ClientWriteHandler(writerSelector,this);
    }


    public void readToPrint() {
        clientReadHandler.start();
    }

    public void send(String str) {
        clientWriteHandler.send(str);
    }


    public SocketChannel getSocket() {
        return socket;
    }

    public ClientHandlerCallBack getClientHandlerCallBack() {
        return clientHandlerCallBack;
    }

    public void exit(){
        try {
            clientReadHandler.exit();
            clientWriteHandler.exit();
            socket.close();
            clientHandlerCallBack.onSelfClosed(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
