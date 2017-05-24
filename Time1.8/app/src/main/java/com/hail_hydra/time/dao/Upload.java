package com.hail_hydra.time.dao;

import android.os.SystemClock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import com.hail_hydra.time.entities.Diary;

public class Upload {
	public static int sUpload(Diary diary){
		System.out.println("Start upload");
		try {
			Socket socket = Connect.sConnect();
			if(socket == null){
				return -1;
			}
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF("UPLOAD");
			dos.flush();
			File file = new File(diary.getPathName());							//file为待上传图片文件
			FileInputStream fis = new FileInputStream(file);
			dos.writeUTF(file.getName());
			if(diary.getContent()!=null && diary.getContent()!=""){
				dos.writeUTF(diary.getContent());
			}
			else{
				dos.writeUTF("no record");
			}
			dos.flush();
			byte[] buffer = new byte[1024];
			int len;
			while((len = fis.read(buffer, 0, buffer.length)) > 0){
				dos.write(buffer, 0, len);
				dos.flush();
			}
			fis.close();
			socket.shutdownOutput();
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			System.out.println(dis.readUTF());
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finish upload");
		return 0;
	}
}
