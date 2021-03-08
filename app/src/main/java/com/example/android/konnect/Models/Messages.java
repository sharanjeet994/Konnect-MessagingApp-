package com.example.android.konnect.Models;

public class Messages {

    String userId,message,messageId;
    long timeStamp;

    public Messages(String userId, String message, String messageId, long timeStamp) {
        this.userId = userId;
        this.message = message;
        this.messageId = messageId;
        this.timeStamp = timeStamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Messages(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Messages(){

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
