package com.ashishdev.notitracker.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Notifications")
public class Notification {

    @ColumnInfo(name = "conversation_title")
    private String conversationTitle;

    @ColumnInfo(name = "conversation_text")
    private String conversationText;

    @ColumnInfo(name = "time_stamp")
    private String timeStamp;

    @ColumnInfo(name = "phone_number")
    private String phoneNo;

    @ColumnInfo(name = "conversation_serial_id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    @Ignore
    public Notification() {
    }

    public Notification(String conversationTitle, String conversationText, String timeStamp, long id, String phoneNo) {
        this.conversationTitle = conversationTitle;
        this.conversationText = conversationText;
        this.timeStamp = timeStamp;
        this.id = id;
        this.phoneNo = phoneNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getConversationTitle() {
        return conversationTitle;
    }

    public void setConversationTitle(String conversationTitle) {
        this.conversationTitle = conversationTitle;
    }

    public String getConversationText() {
        return conversationText;
    }

    public void setConversationText(String conversationText) {
        this.conversationText = conversationText;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

