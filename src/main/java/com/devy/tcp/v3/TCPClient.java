package com.devy.tcp.v3;

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

            socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(),port),3000);

            todo(socket);


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

        InputStream inputStream=client.getInputStream();

        BufferedReader socketBufferdReader=new BufferedReader(new InputStreamReader(inputStream));

        boolean flag=true;

        do{
            try {
                String str=bufferedReader.readLine();

                printStream.println(str);

                String echo=socketBufferdReader.readLine();

                if(echo.equalsIgnoreCase("bye")){
                    flag=false;
                }else {
                    System.out.println(echo);
                }
            } catch (SocketTimeoutException e) {
            }

        }while (flag);

        printStream.close();
        socketBufferdReader.close();


    }
}
