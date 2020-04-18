package com.devy.tcp.v2;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * @author: Devy
 * @create: 2020-04-11 13:59
 **/
public class Server {

     private  static int capacity=64 * 1024 * 1024;
     private static final int PORT = 20000;

      public static void main(String [] args) throws IOException {
          ServerSocket serverSocket =createServer();

          initServer(serverSocket);

          serverSocket.bind(new InetSocketAddress(Inet4Address.getLocalHost(),PORT),50);
          System.out.println("Server already started");

          System.out.println("Server info "+ serverSocket.getInetAddress() +"P"+serverSocket.getLocalPort() );

          for(;;){
              Socket socket=serverSocket.accept();
              new ClientHandler(socket).start();
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

                  OutputStream outputStream=socket.getOutputStream();

                  InputStream inputStream =socket.getInputStream();

                  byte [] buffer=new byte[256];

                  int readCount=inputStream.read(buffer);

                  ByteBuffer byteBuffer=ByteBuffer.wrap(buffer,0,readCount);

                  // byte
                  byte be = byteBuffer.get();

                  // char
                  char c = byteBuffer.getChar();

                  // int
                  int i = byteBuffer.getInt();

                  // bool
                  boolean b = byteBuffer.get() == 1;

                  // Long
                  long l = byteBuffer.getLong();

                  // float
                  float f = byteBuffer.getFloat();

                  // double
                  double d = byteBuffer.getDouble();

                  int pos=byteBuffer.position();

                  String data=new String (buffer,pos,readCount-pos-1);

                  System.out.println("收到数量：" + readCount + " 数据："
                          + be + "\n"
                          + c + "\n"
                          + i + "\n"
                          + b + "\n"
                          + l + "\n"
                          + f + "\n"
                          + d + "\n"
                          + data + "\n");

                  outputStream.write(buffer, 0, readCount);
                  outputStream.close();
                  inputStream.close();


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
