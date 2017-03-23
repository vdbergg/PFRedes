package com.berg.pfredes;


import java.util.Date;

/**
 * Created by berg on 21/03/17.
 */

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;
    private String urlPhoto;

    public ChatMessage(String messageText, String messageUser, String urlPhoto) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.urlPhoto = urlPhoto;

        messageTime = new Date().getTime();
    }

    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;

        messageTime = new Date().getTime();
    }

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getUrlPhoto() { return urlPhoto; }

    public void setUrlPhoto(String urlPhoto) { this.urlPhoto = urlPhoto; }
}
