package com.example.puniaraharja.balicilichat.persistence;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Startup on 5/30/17.
 */

public class Chat {

    public String id;
    public String name;
    public String image;
    private String lastChat;
    private String unread;
    private Long date;

    public Chat()
    {}

    public Chat(String id, String name, String image, String lastChat, String unread, Long date) {
        this.id=id;
        this.name=name;
        this.image=image;
        this.lastChat=lastChat;
        this.unread=unread;
        this.date=date;
    }



    public String getUid() {
        return id;
    }

    public void setUid(String uid) {
        this.id = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastChat() {
        return lastChat;
    }

    public void setLastChat(String lastChat) {
        this.lastChat = lastChat;
    }

    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {

            this.date = date;

    }


}