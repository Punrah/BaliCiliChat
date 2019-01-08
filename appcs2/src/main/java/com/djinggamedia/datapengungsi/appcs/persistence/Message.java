package com.djinggamedia.datapengungsi.appcs.persistence;

/**
 * Created by Startup on 5/30/17.
 */

public class Message {

    private String id;
    private String image;
    private String message;
    private String read;






    public void setImage(String image) {
        this.image = image;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getImage() {
        return image;
    }
}