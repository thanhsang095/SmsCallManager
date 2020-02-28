package com.nus.SmsCallManager.Models;

public class SmsModel {
    private String _id;
    private String address;
    private String msg;
    private String readState; //"0" for have not read sms and "1" for have read sms
    private String time;
    private String folderName;

    public String getId() {
        return _id;
    }

    public String getAddress() {
        return address;
    }

    public String getMsg() {
        return msg;
    }

    public String getReadState() {
        return readState;
    }

    public String getTime() {
        return time;
    }

    public String getFolderName() {
        return folderName;
    }


    public void setId(String id) {
        _id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setReadState(String readState) {
        this.readState = readState;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
