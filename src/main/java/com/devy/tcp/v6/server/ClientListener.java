package com.devy.tcp.v6.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author: Devy
 * @create: 2020-04-20 14:13
 **/
public class ClientListener extends Thread{

    private boolean done=false;
    private ClientHandlerCallBack clientHandlerCallBack;
    Selector selector;

    public ClientListener(Selector selector,ClientHandlerCallBack clientHandlerCallBack) throws IOException {
        this.selector=selector;
        this.clientHandlerCallBack=clientHandlerCallBack;
    }

    @Override
    public void run(){
        super.run();

        System.out.println("Server ready ....");

        do{
            try {
               if(selector.select()==0){
                   if(done){
                       break;
                   }
                   continue;
               }
                Iterator<SelectionKey>  iterator=selector.selectedKeys().iterator();

               while (iterator.hasNext()){
                   if(done){
                       break;
                   }
                   SelectionKey selectionKey=iterator.next();
                   iterator.remove();
                   if(selectionKey.isAcceptable()){
                       System.out.println("new connection ......");
                       ServerSocketChannel serverSocketChannel=(ServerSocketChannel) selectionKey.channel();

                       SocketChannel socketChannel=serverSocketChannel.accept();

                       ClientHandler clientHandler=new ClientHandler(socketChannel,clientHandlerCallBack);
                       clientHandler.readToPrint();
                       clientHandlerCallBack.onConnected(clientHandler);

                   }
               }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }while (!done);
    }

    public void exit(){
        done=true;
        try {
            selector.wakeup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
