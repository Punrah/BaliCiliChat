package com.djinggamedia.datapengungsi.appcs.persistence;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Startup on 5/30/17.
 */

public class Chat {

    public String id;
    public String name;
    public String image;
    private String lastChat;
    private String unread;
    private String date;

    public Chat()
    {}

    public Chat(String id, String name, String image, String lastChat, String unread, String date) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {

            this.date = date;

    }

    public Date getDateDate()
    {

        String string = date;
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("d MMM yy h:mm a");
        try {
            date = format.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}