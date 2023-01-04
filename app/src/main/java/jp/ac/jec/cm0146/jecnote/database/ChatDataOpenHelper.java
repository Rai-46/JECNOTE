package jp.ac.jec.cm0146.jecnote.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.ac.jec.cm0146.jecnote.models.ChatMessage;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;

public class ChatDataOpenHelper extends SQLiteOpenHelper {


    public ChatDataOpenHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // チャットデータのテーブル作る
        String createChatTable = "CREATE TABLE " + Constants.DB_CHAT_TABLE + " (" +
                Constants.DB_CHAT_ID + " TEXT PRIMARY KEY);";
        db.execSQL(createChatTable);

        // メッセージデータのテーブル 時刻はStringでformatしてから
        String createMessageTable = "CREATE TABLE " + Constants.DB_MESSAGE_TABLE + " (" +
                Constants.DB_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Constants.DB_CHAT_ID + " TEXT," +
                Constants.DB_SENDER_ID + " TEXT," +
                Constants.DB_RECEIVER_ID + " TEXT," +
                Constants.DB_TIME_STAMP + " TEXT," +
                Constants.DB_MESSAGE_DATA + " TEXT);";

        db.execSQL(createMessageTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    // select where partnerID
    public ArrayList<ChatMessage> selectChatData(String partnerID) {
        SQLiteDatabase db = getWritableDatabase();
        String selectSQL = "SELECT * FROM " + Constants.DB_MESSAGE_TABLE + " WHERE " + Constants.DB_CHAT_ID + "=?";

        ArrayList<ChatMessage> ary = new ArrayList<>();

        Log.i("abcdefg", "partnerID:" + partnerID + "\n");
        Log.i("testData", "selectChatData");

        if(db == null) {
            return ary;
        }
        try {
            Cursor cur = db.rawQuery(selectSQL, new String[]{partnerID});
            while (cur.moveToNext()) {
                ChatMessage message = new ChatMessage();
                message.setSenderId(cur.getString(2));
                message.setReceiverId(cur.getString(3));
                message.setDateTime(cur.getString(4));
                message.setMessage(cur.getString(5));

                ary.add(message);
            }


        } catch (SQLiteException e) {
            Log.e("SQLiteException", e.getMessage());
        } finally {
            db.close();
        }
        Log.i("testData", "selectChatData return:" + ary);

        ary.sort((obj1, obj2) -> parseDate(obj1.getDateTime()).compareTo(parseDate(obj2.getDateTime())));

        return ary;
    }

    // Date情報 String -> Date
    private Date parseDate(String dateStr) {

        Date date = null;

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss");
            date = sdf.parse(dateStr);
//            Log.i("dateInfo", String.valueOf(date));
            return date;

        } catch (ParseException e ) {
            e.printStackTrace();
        }

        return date;

    }

    // insert chatTable where partnerID
    //TODO 初めてチャットする時のみ呼ばれる。アプリアンインストール後でも初めてチャットする場合には呼ばれないといけないよね
    // 条件は、CONVERSATIONには相手がいる場合。相手の情報をインサートするべき
    // 正直、このテーブルは存在価値ないような気がしている
    public void insertChatTable (String partnerID) {
        SQLiteDatabase db = getWritableDatabase();
        String insertChatTable = "INSERT INTO " + Constants.DB_CHAT_TABLE + " VALUE(" +
                "'" + partnerID + "');";

        if(db != null) {
            try {
                db.execSQL(insertChatTable);
            } catch (SQLiteException e) {
                Log.e("SQLiteException", e.getMessage());
            } finally {
                db.close();
            }
        }

    }

    // insert message where partnerID
    public void insertMessageData(String partnerID, String senderID, String dateTime, String message ) {
        SQLiteDatabase db = getWritableDatabase();

        Log.i("testData", "insertMessageData");

        String insertMessageData = "INSERT INTO " + Constants.DB_MESSAGE_TABLE + "(" +
                Constants.DB_CHAT_ID + "," +
                Constants.DB_SENDER_ID + "," +
                Constants.DB_RECEIVER_ID + "," +
                Constants.DB_TIME_STAMP + "," +
                Constants.DB_MESSAGE_DATA + ") VALUES (" +
                "'" + partnerID + "', " +
                "'" + senderID + "', " +
                "'" + partnerID + "', " +
                "'" + dateTime + "', " +
                "'" + message + " fromDB" + "');";


        if(db != null) {
            try {
                db.execSQL(insertMessageData);
            } catch (SQLiteException e) {
                Log.e("SQLiteException", e.getMessage());
            } finally {
                db.close();
            }
        }

    }

}
