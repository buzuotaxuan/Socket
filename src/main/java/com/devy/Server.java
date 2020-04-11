package com.devy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: Devy
 * @create: 2020-04-11 13:59
 **/
public class Server {

      public static void main(String [] args) throws IOException {
          ServerSocket serverSocket =new ServerSocket(2000);

          System.out.println("Server already started");

          System.out.println("Server info "+ serverSocket.getInetAddress() +"P"+serverSocket.getLocalPort() );

          for(;;){
              Socket socket=serverSocket.accept();
              new ClientHandler(socket).start();
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
