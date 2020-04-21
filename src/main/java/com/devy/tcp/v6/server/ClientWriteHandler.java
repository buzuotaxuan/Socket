package com.devy.tcp.v6.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author: Devy
 * @create: 2020-04-20 16:32
 **/
public class ClientWriteHandler {

    private ClientHandler clientHandler;
    private ExecutorService executorService;
    private boolean done=false;
    private Selector selector;
    private ByteBuffer byteBuffer;

    public ClientWriteHandler(Selector writerSelector,ClientHandler handler) throws IOException{
        this.clientHandler=handler;
        this.selector=writerSelector;
        executorService=Executors.newSingleThreadExecutor();
        byteBuffer=ByteBuffer.allocate(256);
    }

    public  void send(String msg){
        System.out.println("-----");
        if(done){
            return;
        }

        executorService.execute(new WriteRunnable(msg));
    }

    class WriteRunnable implements Runnable{
        private final String data;

        public WriteRunnable(String data){
            this.data= data + '\n';
        }

        @Override
        public void run(){
            if(ClientWriteHandler.this.done){
                return;
            }
            System.out.println("++++++++++++++");
            byteBuffer.clear();
            byteBuffer.put(data.getBytes());
            byteBuffer.flip();

            while (!done&& byteBuffer.hasRemaining()){
              SocketChannel socketChannel = clientHandler.getSocket();
                try {
                    int len = socketChannel.write(byteBuffer);
                    if(len<0){
                        System.out.println("client has quit.....");
                        clientHandler.exit();
                        break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void exit(){
        try {
           selector.wakeup();
           selector.close();
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
