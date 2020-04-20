package com.devy.tcp.v4;

import com.devy.tcp.v4.handler.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Devy
 * @create: 2020-04-11 13:59
 **/
public class TCPServer {

     public final static int capacity=64 * 1024 * 1024;
     private static final int PORT = 20000;
    private final int port;
    private ClientListener    listener;

    private List<ClientHandler> clientHandlerList=new ArrayList<ClientHandler>();


    public TCPServer(int port) {
        this.port = port;
    }

    public boolean start(){
        try {
            ClientListener listener=new ClientListener(port);
            this.listener=listener;
            listener.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public void stop(){
        if(null!=listener){
            listener.exit();
        }
    }

    public void broadcast(String data){
        for (ClientHandler clientHandler:clientHandlerList) {
            clientHandler.send(data);
        }
    }

      private static void initServer(ServerSocket serverSocket) throws IOException{
         serverSocket.setReuseAddress(true);

         serverSocket.setReceiveBufferSize(capacity);

         serverSocket.setPerformancePreferences(1,1,1);
      }


      private  class ClientListener extends Thread{

          private ServerSocket server;
          private boolean done = false;
          private  ClientListener (int port) throws Exception{
              server = new ServerSocket(port);
              initServer(server);
          }

          @Override
          public void run(){
              super.run();
              do{
                  Socket client;
                  try {
                      client=server.accept();
                      try {
                          ClientHandler clientHandler=new ClientHandler(client,
                                  handler ->clientHandlerList.remove(handler));
                          clientHandler.readToPrint();

                          clientHandlerList.add(clientHandler);
                      } catch (Exception e) {
                          e.printStackTrace();
                      }

                  } catch (IOException e) {

                  }
              }while (!done);
          }

          void exit() {
              done = true;
              try {
                  server.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }

      }

}
