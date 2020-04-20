package com.devy.tcp.v5.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author: Devy
 * @create: 2020-04-20 15:09
 **/
public class ClientReadHandler  extends Thread{

    private boolean done=false;
    private  ClientHandler clientHandler;
    private  InputStream inputStream;
    public ClientReadHandler(ClientHandler clientHandler) throws IOException {
        this.clientHandler=clientHandler;
    }


    @Override
    public void run(){
        super.run();
        try {
             inputStream=clientHandler.getSocket().getInputStream();
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));
            do{
                String data=bufferedReader.readLine();
                if(data==null||"bye".equals(data)){
                    clientHandler.exit();
                    break;
                }
                clientHandler.getClientHandlerCallBack().onNewMessageArrived(clientHandler,data);
            }while (!done);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void exit(){
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
