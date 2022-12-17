package jp.ac.jec.cm0146.jecnote.models;

import java.util.Date;

public class ChatMessage {
    public String senderId, receiverId, message, dateTime, lastSenderID;
    public Date dateObject;
    public String conversionId, firstReceiverName, firstReceiverImage, firstSenderId, firstSenderName;
    public boolean isRead;
}
