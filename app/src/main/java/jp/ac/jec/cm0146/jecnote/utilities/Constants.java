package jp.ac.jec.cm0146.jecnote.utilities;

import android.security.AppUriAuthenticationPolicy;

import org.checkerframework.checker.index.qual.UpperBoundUnknown;

import java.util.Date;
import java.util.HashMap;

public class Constants {

    public static final String KEY_TEACHER_PASSWORD = "teacherPass";
    public static final String VALUE_TEACHER_PASSWORD = "password";

    public static final String ACCOUNT_SETTING_END = "accountSettingEnd";

    public static final String KEY_LOGINED = "logined";
    public static final String KEY_USER_NAME = "user";
    public static final String KEY_USER_IMAGE = "userImage";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_COLLECTION_USER = "userCollection";
    public static final String KEY_USER_ID = "userID";
    public static final String USER_FCM_TOKEN = "fcmToken";

    public static final String IS_TEACHER = "isTeacher";
    public static final String WHICH_SELECT = "whichSelect";
    public static final String SELECTED_TEACHER = "teacher";
    public static final String SELECTED_STUDENT = "student";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_IS_READ = "isReaded";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String LAST_SEND_MESSAGE_USERID = "lastSendUser";
    public static final String LAST_RECEIVED_MESSAGE_USERID = "lastReceivedUser";

     // SQLite
    public static final String DB_NAME = "ChatDatabase";
    public static final String DB_CHAT_TABLE = "ChatTable";
    public static final String DB_MESSAGE_TABLE = "MessageTable";
    public static final String DB_CHAT_ID = "ChatID";
    public static final String DB_MESSAGE_ID = "MessageID";
    public static final String DB_SENDER_ID = "SenderID";
    public static final String DB_RECEIVER_ID = "ReceiverID";
    public static final String DB_TIME_STAMP = "TimeStamp";
    public static final String DB_MESSAGE_DATA = "Message";
    public static final String DB_UPDATED_DATE = "UpdateDate";

    public static final String FCM_RECEIVED_MESSAGE = "receivedMessage";



    public static HashMap<String , String > remoteMsgHeaders = null;
    public static HashMap<String , String > getRemoteMsgHeaders() {
        if(remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAHt8hQZg:APA91bHmO5TzUELabXAhNr6bxGIEzP-x1SZ6QuAQTvc6eQIyyMlE68oIOScDX-LAKGKlt6oJMAITkAi17PKtufQJW2PdRV3jzAH6imUm6T6_nZssptY7Bpo4HSa7D8_T0-2ILDeLsSS1"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}
