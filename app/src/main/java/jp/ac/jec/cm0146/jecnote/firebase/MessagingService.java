package jp.ac.jec.cm0146.jecnote.firebase;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import jp.ac.jec.cm0146.jecnote.R;
import jp.ac.jec.cm0146.jecnote.activities.ChatActivity;
import jp.ac.jec.cm0146.jecnote.activities.MainActivity;
import jp.ac.jec.cm0146.jecnote.databinding.ActivityMainBinding;
import jp.ac.jec.cm0146.jecnote.models.StudentUser;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;

public class MessagingService extends FirebaseMessagingService {



    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // sharedPreferenceで動的には無理だけれど実装できるかも、チャットアイコン
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        preferenceManager.putBoolean(Constants.FCM_RECEIVED_MESSAGE, true);

        // Studentとしているが、全ユーザ自分自身
        StudentUser user = new StudentUser();
        user.id = remoteMessage.getData().get(Constants.KEY_USER_ID);
        user.userDisplayName = remoteMessage.getData().get(Constants.KEY_USER_NAME);
        user.token = remoteMessage.getData().get(Constants.USER_FCM_TOKEN);

        int notificationId = new Random().nextInt();
        String channelId = "chat_message";

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("user", user);
        // 通知からActivityを起動する
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(user.userDisplayName);
//        builder.setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE));
        builder.setContentText("新着メッセージがあります。");
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
//                remoteMessage.getData().get(Constants.KEY_MESSAGE)
                "新着メッセージがあります。"
        ));
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        // 通知タップでActivity起動
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        // Android8以降で必要
        CharSequence channelName = "Chat Message";
        String channelDescription = "This notification channel is used for chat message notifications";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setDescription(channelDescription);
        channel.setShowBadge(true);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());

    }


}
