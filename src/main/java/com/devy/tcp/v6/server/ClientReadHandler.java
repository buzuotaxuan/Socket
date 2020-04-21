package com.devy.tcp.v6.server;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author: Devy
 * @create: 2020-04-20 15:09
 **/
public class ClientReadHandler  extends Thread{

    private boolean done=false;
    private final Selector selector;
    private final ByteBuffer byteBuffer;
    private final ClientHandler clientHandler;
    public ClientReadHandler(Selector readSelector,ClientHandler clientHandler){

        this.selector=readSelector;
        this.clientHandler=clientHandler;
        byteBuffer=ByteBuffer.allocate(256);
    }

    @Override
    public void run(){
        super.setName("clientReadHandler");
        super.run();
        try {
            do{
                if (selector.select() == 0) {
                    if (done) {
                        break;
                    }
                    continue;
                }
                Iterator<SelectionKey> iterator=selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    if(done){
                        break;
                    }
                    SelectionKey selectionKey=iterator.next();
                    iterator.remove();

                    if(selectionKey.isReadable()){
                        SocketChannel client=(SocketChannel)selectionKey.channel();
                        byteBuffer.clear();

                        int readCount=client.read(byteBuffer);

                        if(readCount>0){
                            String data=new String (byteBuffer.array(),0,readCount-1);
                            clientHandler.getClientHandlerCallBack().onNewMessageArrived(clientHandler,data);
                        }else {
                            System.out.println("客户端退出");
                            clientHandler.exit();
                        }
                    }
                }

            }while (!done);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void exit(){
        try {
            done = true;
            selector.wakeup();
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
