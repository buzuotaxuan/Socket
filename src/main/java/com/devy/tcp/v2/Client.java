package com.devy.tcp.v2;

import javax.annotation.Resource;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * @author: Devy
 * @create: 2020-04-11 13:59
 **/
public class Client {

    private static final int PORT = 20000;
    private static final int LOCAL_PORT = 20001;

    public static void main(String[] args)throws Exception {
        Socket socket= createServer();
        initSocket(socket);
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 3000);
        try {

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



        OutputStream outputStream=client.getOutputStream();

        InputStream clientInputStream=client.getInputStream();

        byte [] buffer=new byte[256];

        ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);


        // byte
        byteBuffer.put((byte) 126);

        // char
        char c = 'a';
        byteBuffer.putChar(c);

        // int
        int i = 2323123;
        byteBuffer.putInt(i);

        // bool
        boolean b = true;
        byteBuffer.put(b ? (byte) 1 : (byte) 0);

        // Long
        long l = 298789739;
        byteBuffer.putLong(l);


        // float
        float f = 12.345f;
        byteBuffer.putFloat(f);


        // double
        double d = 13.31241248782973;
        byteBuffer.putDouble(d);

        // String
        String str = "Hello你好！";
        byteBuffer.put(str.getBytes());

        outputStream.write(buffer,0,byteBuffer.position()+1);

        int readCount= clientInputStream.read(buffer);

        System.out.println("收到数量：" + readCount);

        // 资源释放
        outputStream.close();
        clientInputStream.close();
    }

    public  static Socket createServer(){
        Socket socket=new Socket();
        try {
            socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(),LOCAL_PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  socket;
    }

    public static void initSocket(Socket socket) throws Exception{
        socket.setSoTimeout(2000);

        socket.setReuseAddress(true);

        socket.setTcpNoDelay(true);

        socket.setKeepAlive(true);

        socket.setSoLinger(true,20);

        socket.setOOBInline(true);

        socket.setReceiveBufferSize(64 * 1024 * 1024);

        socket.setSendBufferSize(64 * 1024 * 1024);

        // 设置性能参数：短链接，延迟，带宽的相对重要性
        socket.setPerformancePreferences(1, 1, 0);
    }
}
