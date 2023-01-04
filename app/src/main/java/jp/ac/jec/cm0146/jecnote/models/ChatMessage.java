package jp.ac.jec.cm0146.jecnote.models;

import java.util.Date;

public class ChatMessage {
    private String senderId, receiverId, message, dateTime, lastSenderID;
    private Date dateObject;
    private String conversionId, firstReceiverName, firstReceiverImage, firstSenderId, firstSenderName;
    private boolean isRead;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLastSenderID() {
        return lastSenderID;
    }

    public void setLastSenderID(String lastSenderID) {
        this.lastSenderID = lastSenderID;
    }

    public Date getDateObject() {
        return dateObject;
    }

    public void setDateObject(Date dateObject) {
        this.dateObject = dateObject;
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public String getFirstReceiverName() {
        return firstReceiverName;
    }

    public void setFirstReceiverName(String firstReceiverName) {
        this.firstReceiverName = firstReceiverName;
    }

    public String getFirstReceiverImage() {
        return firstReceiverImage;
    }

    public void setFirstReceiverImage(String firstReceiverImage) {
        this.firstReceiverImage = firstReceiverImage;
    }

    public String getFirstSenderId() {
        return firstSenderId;
    }

    public void setFirstSenderId(String firstSenderId) {
        this.firstSenderId = firstSenderId;
    }

    public String getFirstSenderName() {
        return firstSenderName;
    }

    public void setFirstSenderName(String firstSenderName) {
        this.firstSenderName = firstSenderName;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean read) {
        isRead = read;
    }
}
