package com.devy.tcp.v4;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @author: Devy
 * @create: 2020-04-11 13:59
 **/
public class TCPClient {
    public static void linkWith(int port) {
        Socket socket= new Socket();

        try {
            socket.setSoTimeout(3000);
//            socket.setReceiveBufferSize(TCPServer.capacity);
            socket.setSendBufferSize(TCPServer.capacity);
            socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(),port),3000);

            ReadHandler readHandler=new ReadHandler(socket.getInputStream());
            readHandler.start();
            todo(socket);

            readHandler.exit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void todo(Socket client) throws IOException{
        InputStream in= System.in;
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in));
        OutputStream outputStream=client.getOutputStream();
        PrintStream printStream=new PrintStream(outputStream);
        boolean flag=true;

        do{
            String str=bufferedReader.readLine();
            printStream.println(str);
        }while (flag);

        printStream.close();
    }

    static class ReadHandler extends Thread{
        private boolean done=false;
        private InputStream inputStream;
        ReadHandler(InputStream inputStream){
            this.inputStream=inputStream;
        }

        @Override
        public void run(){

            BufferedReader socketBufferdReader=new BufferedReader(new InputStreamReader(inputStream));

            try {
                do{
                    String echo= null;
                    try {
                        echo = socketBufferdReader.readLine();
                    } catch (SocketTimeoutException e) {
                       continue;
                    }

                    if (echo == null) {
                        System.out.println("连接已关闭，无法读取数据！");
                        break;
                    }

                    if(echo.equalsIgnoreCase("bye")){
                        done=true;
                        break;
                    }else {
                        System.out.println(echo);
                    }
                }while (!done);

                inputStream.close();
                socketBufferdReader.close();
            } catch (IOException e) {
                e.printStackTrace();
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
}
