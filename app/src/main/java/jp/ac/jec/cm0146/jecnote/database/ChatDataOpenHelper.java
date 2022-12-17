package jp.ac.jec.cm0146.jecnote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import jp.ac.jec.cm0146.jecnote.models.ChatMessage;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;

public class ChatDataOpenHelper extends SQLiteOpenHelper {




    public ChatDataOpenHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO チャットデータのテーブル作る
//        String createChatTable = "CREATE TABLE" + Constants.DB_CHAT_TABLE + " (" +
//                ""
//        db.execSQL();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    // select

}
