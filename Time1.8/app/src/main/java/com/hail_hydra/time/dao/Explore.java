package com.hail_hydra.time.dao;

import com.hail_hydra.time.entities.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Explore {
	public static List<Diary> sExplore(){
		System.out.println("Start explore");
		List<Diary> diaries = new ArrayList();
		try {
			Socket socket = Connect.sConnect();
			if(socket == null){
				return null;
			}
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF("EXPLORE");
			dos.flush();
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			int num = Integer.parseInt(dis.readUTF());			//获得列表元素个数
			for(int i = 0;i<num;i++){
				Diary diary = new Diary(dis.readUTF(),dis.readUTF());
				diaries.add(diary);
			}
			dis.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finish explore");
		return diaries;
	}
}
