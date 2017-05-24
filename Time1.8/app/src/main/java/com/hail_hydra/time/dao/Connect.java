package com.hail_hydra.time.dao;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Connect {
    private static String serverIP = "192.168.200.1";
    private static int serverPort = 44445;
    private static SocketAddress socketAddress = new InetSocketAddress(serverIP,serverPort);
    public static Socket sConnect(){
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(socketAddress, 3000);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return socket;
    }
}
