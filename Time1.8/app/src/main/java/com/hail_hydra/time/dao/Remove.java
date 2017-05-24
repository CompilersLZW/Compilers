package com.hail_hydra.time.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Remove {
	public static int sRemove(String date_time){
		System.out.println("Start remove");
		try {
			Socket socket = Connect.sConnect();
			if(socket ==null){
				return -1;
			}
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF("REMOVE");
			dos.flush();
			dos.writeUTF(date_time);
			dos.flush();
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			System.out.println(dis.readUTF());
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finish remove");
		return 0;
	}
}
