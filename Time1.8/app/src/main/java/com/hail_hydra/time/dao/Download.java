package com.hail_hydra.time.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Download {
	public static byte[] sDownload(String date_time){
		System.out.println("Start download");
		ByteArrayOutputStream baos = null;
		try {
			Socket socket = Connect.sConnect();
			if(socket == null){
				return null;
			}
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF("DOWNLOAD");
			dos.flush();
			dos.writeUTF(date_time);
			dos.flush();
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while(((len = dis.read(buffer)) > 0)){
				baos.write(buffer, 0, len);
			}
			baos.close();
			dis.close();

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finish download");
		return baos.toByteArray();
	}
}
