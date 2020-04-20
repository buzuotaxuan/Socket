package com.devy.tcp.v5.server;

import java.io.IOException;
import java.net.Socket;

/**
 * @author: Devy
 * @create: 2020-04-20 14:30
 **/
public class ClientHandler {

   private Socket socket;
   private ClientHandlerCallBack clientHandlerCallBack;
   private ClientReadHandler clientReadHandler;
   private ClientWriteHandler clientWriteHandler;

    public ClientHandler(Socket socket,ClientHandlerCallBack clientHandlerCallBack) throws Exception{
        this.socket=socket;
        this.clientHandlerCallBack=clientHandlerCallBack;
        this.clientReadHandler=new ClientReadHandler(this);
        this.clientWriteHandler=new ClientWriteHandler(this);
    }


    public void readToPrint() {
        clientReadHandler.start();
    }

    public void send(String str) {
        clientWriteHandler.send(str);
    }


    public Socket getSocket() {
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
