package com.devy.tcp.v5.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: Devy
 * @create: 2020-04-20 14:13
 **/
public class ClientListener extends Thread{

    private ServerSocket serverSocket;
    private boolean done=false;
    private  ClientHandlerCallBack clientHandlerCallBack;


    public ClientListener(int port,ClientHandlerCallBack clientHandlerCallBack) throws IOException {
        serverSocket=new ServerSocket(port);
        this.clientHandlerCallBack=clientHandlerCallBack;
    }

    @Override
    public void run(){
        super.run();

        System.out.println("Server ready ....");

        do{
            try {
                Socket client= serverSocket.accept();
                ClientHandler clientHandler=new ClientHandler(client,clientHandlerCallBack);
                clientHandler.readToPrint();

                clientHandlerCallBack.onConnected(clientHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }while (!done);
    }

    public void exit(){
        done=true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
