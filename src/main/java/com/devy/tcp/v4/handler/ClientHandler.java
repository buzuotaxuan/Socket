package com.devy.tcp.v4.handler;

import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Devy
 * @create: 2020-04-18 14:37
 **/
public class ClientHandler {

    private final CloseNotify closeNotify;
    private final Socket socket;
    private final ClientReadHandler readHandler;
    private final ClientWriteHandler writeHandler;

    public ClientHandler(Socket socket,CloseNotify closeNotify) throws  Exception{
        this.socket=socket;
        this.closeNotify=closeNotify;
        this.readHandler =new ClientReadHandler(socket.getInputStream());
        this.writeHandler=new ClientWriteHandler(socket.getOutputStream());

    }

    public void send(String str) {
        writeHandler.send(str);
    }

    public void readToPrint() {
        readHandler.start();
    }


    public interface CloseNotify {
        void onSelfClosed(ClientHandler handler);
    }

    private void exitBySelf(){
        exit();
        closeNotify.onSelfClosed(this);
    }


    public void exit(){
        readHandler.exit();
        writeHandler.exit();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class ClientReadHandler extends Thread{

        private boolean done=false;

        private final InputStream inputStream;

        ClientReadHandler(InputStream inputStream){
            this.inputStream=inputStream;
        }

        @Override
        public  void run(){
            super.run();

            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));

            try {
                do {
                    String str=reader.readLine();
                    if(null==str||"bye".equals(str)){
                        System.out.println("无法读取客户端数据");
                        ClientHandler.this.exitBySelf();
                        break;
                    }
                    System.out.println(str);
                }while (!done);
            } catch (IOException e) {
                e.printStackTrace();

                if(!done){
                    ClientHandler.this.exitBySelf();
                }
            } finally {
                try {
                    inputStream.close();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void exit() {
            done = true;
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class ClientWriteHandler{
        private boolean done =false;

        private final PrintStream printStream;

        private final ExecutorService executorService;


        ClientWriteHandler(OutputStream outputStream){
            this.printStream=new PrintStream(outputStream);
            this.executorService=Executors.newSingleThreadExecutor();
        }



        public void send(String str){
            executorService.execute(new WriteRunnable(str));
        }


        class WriteRunnable implements Runnable{
            private  String msg;

            WriteRunnable(String msg){
                this.msg=msg;
            }
            @Override
            public void run(){
                if(ClientWriteHandler.this.done){
                    return;
                }
                try {
                    ClientWriteHandler.this.printStream.println(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        void exit()
        {
            done= true;
            printStream.close();
            executorService.shutdown();
        }

    }
}
