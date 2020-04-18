package com.devy.tcp.v3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: Devy
 * @create: 2020-04-11 13:59
 **/
public class TCPServer {

     private final static int capacity=64 * 1024 * 1024;
     private static final int PORT = 20000;
    private final int port;
    private ClientListener    listener;

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

      private static ServerSocket createServer() throws IOException{
          ServerSocket serverSocket=new ServerSocket();
          return serverSocket;
      }

      private static void initServer(ServerSocket serverSocket) throws IOException{
         serverSocket.setReuseAddress(true);

         serverSocket.setReceiveBufferSize(capacity);

         serverSocket.setPerformancePreferences(1,1,1);
      }


      private static class ClientListener extends Thread{

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
                      ClientHandler handler=new ClientHandler(client);
                      handler.start();
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



      public static class ClientHandler extends Thread{

          private  Socket socket;
          private  boolean flag =true;
          ClientHandler (Socket socket){
              this.socket=socket;
          }

          @Override
          public void run(){
              System.out.println("client connected"+socket.getInetAddress());

              try {
                  PrintStream socketOutput =new PrintStream(socket.getOutputStream());

                  BufferedReader socketInput=new BufferedReader(new InputStreamReader(socket.getInputStream()));


                  do {
                      String receivedMessage=socketInput.readLine();

                      if("bye".equalsIgnoreCase(receivedMessage)){
                          flag=false;
                          socketOutput.println("bye");
                      }else {
                          System.out.println(receivedMessage);
                          socketOutput.println("call back"+receivedMessage.length());
                      }

                  }while (flag);

                  socketInput.close();
                  socketOutput.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }finally {
                  try {
                      socket.close();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
              System.out.println("client already quite" +socket.getInetAddress());

          }
      }

}
