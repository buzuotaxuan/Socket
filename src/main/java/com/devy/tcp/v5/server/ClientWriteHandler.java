package com.devy.tcp.v5.server;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Devy
 * @create: 2020-04-20 16:32
 **/
public class ClientWriteHandler {

    private ClientHandler clientHandler;
    private final PrintStream printStream;
    private ExecutorService executorService;
    private boolean done=false;

    public ClientWriteHandler(ClientHandler handler) throws IOException{
        this.clientHandler=handler;
        this.printStream=new PrintStream(handler.getSocket().getOutputStream());
        executorService=Executors.newSingleThreadExecutor();
    }

    public  void send(String msg){
        if(done){
            return;
        }

        executorService.execute(new WriteRunnable(msg));
    }

    class WriteRunnable implements Runnable{
        private final String data;
        public WriteRunnable(String data){
            this.data=data;
        }

        @Override
        public void run(){
            if(ClientWriteHandler.this.done){
                return;
            }
            ClientWriteHandler.this.printStream.println(data);
        }
    }
    public void exit(){
        try {
            printStream.close();
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
