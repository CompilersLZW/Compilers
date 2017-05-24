package com.hail_hydra.time.entities;

import com.hail_hydra.time.dao.Explore;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by J_Zhen on 2017/3/29.
 */
public class Diary {

    private String dateTime;
    private String content;
    private String pathName;

    //构造
    public Diary(){

    }
    public Diary(String dateTime,String content){
        this.dateTime = dateTime;
        this.content = content;
    }

    public static List<Diary> getAllDiary() {

        List<Diary> diaries = Explore.sExplore();
        return diaries;

    }

    public String getDateTime(){
        return this.dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getContent(){
        return this.content;
    }
    public String getPathName() {
        return pathName;
    }
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

}
