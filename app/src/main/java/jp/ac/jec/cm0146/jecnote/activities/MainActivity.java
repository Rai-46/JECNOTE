package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import jp.ac.jec.cm0146.jecnote.R;
import jp.ac.jec.cm0146.jecnote.adapters.MenuListAdapter;
import jp.ac.jec.cm0146.jecnote.databinding.ActivityMainBinding;
import jp.ac.jec.cm0146.jecnote.listener.MainActivityListener;
import jp.ac.jec.cm0146.jecnote.models.MenuItems;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity implements MainActivityListener {

    private ActivityMainBinding binding;

    private PreferenceManager preferenceManager;


    private static final int MENU_GUIDE = 1;
    private static final int MENU_NEWS = 2;
    private static final int MENU_MAP = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//         \u000d

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListener();
        setList();
        getToken();

    }

    private void setListener() {
        binding.sendChatActivity.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
            startActivity(intent);

        });




    }

//    public Context getContext() {
//        return getApplicationContext();
//    }

    private void setList() {
        ArrayList<MenuItems> items = new ArrayList<>(Arrays.asList(
                new MenuItems().setId(MENU_GUIDE).setMenuTitle("学園生活ガイド").setMenuImage(R.drawable.lifeguide_2019),
                new MenuItems().setId(MENU_NEWS).setMenuTitle("学校からの連絡").setMenuImage(R.drawable.twitter_kakouzumi),
                new MenuItems().setId(MENU_MAP).setMenuTitle("学校所在地").setMenuImage(R.drawable.schoolmap)
        ));
        MenuListAdapter adapter = new MenuListAdapter(getApplicationContext(), this);
        for (MenuItems data: items) {
            adapter.add(data);
        }
        binding.menuItemList.setAdapter(adapter);

    }


    private void getToken() {
        // 通知に必要なFCMトークン
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateTokens);
    }

    private void updateTokens(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USER)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.USER_FCM_TOKEN, token);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!preferenceManager.getBoolean(Constants.FCM_RECEIVED_MESSAGE) || preferenceManager.getBoolean(Constants.FCM_RECEIVED_MESSAGE) == null) {
            // 未読 or null
            binding.sendChatActivity.setImageResource(R.drawable.ic_outline_chat_bubble_outline_24);
        } else {
            // 既読
            binding.sendChatActivity.setImageResource(R.drawable.ic_outline_mark_chat_unread_24);
        }
    }

    // MainActivityListener
    @Override
    public void onTapItem(MenuItems menuItems) {
        // Intentで学園生活ガイドViewに遷移
        if(menuItems.getId() == MENU_GUIDE) {
            Intent intent = new Intent(getApplicationContext(), SchoolGuideActivity.class);
            startActivity(intent);
        }
        // Intentで学校連絡Viewに遷移
        if(menuItems.getId() == MENU_NEWS) {
            Intent intent = new Intent(getApplicationContext(), SchoolNewsActivity.class);
            startActivity(intent);
        }
        if(menuItems.getId() == MENU_MAP) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
        }
    }

    // URLから画像をImageViewに追加する
    // TODO AsyncTaskが非推奨だから、余裕があれば直す！
    public class DownloadUserImage extends AsyncTask<String, Void, Bitmap> {
        protected ImageView bmImage;

        // ImageViewの参照先を受け取る
        public DownloadUserImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }


        @Override
        public Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}