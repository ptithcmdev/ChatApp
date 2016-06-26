package com.ptit.model;

/**
 * Created by Phan Anh on 6/24/2016.
 */
public class Message  {
    public String name,message,img;
    public boolean isSelf;

    public Message(String name, String message, String img, boolean isSelf) {
        this.name = name;
        this.message = message;
        this.img = img;
        this.isSelf = isSelf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }
}
