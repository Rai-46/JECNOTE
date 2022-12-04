package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//         \u000d


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListener();
        setList();

    }

    private void setListener() {
        binding.sendChatActivity.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
            startActivity(intent);
        });
    }

    private void setList() {
        ArrayList<MenuItems> items = new ArrayList<MenuItems>(Arrays.asList(
                new MenuItems().setMenuTitle("学園生活ガイド").setMenuImage(R.drawable.lifeguide_2019),
                new MenuItems().setMenuTitle("学校からの連絡")
        ));
        MenuListAdapter adapter = new MenuListAdapter(getApplicationContext(), this);
        for (MenuItems data: items) {
            adapter.add(data);
        }
        binding.menuItemList.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // MainActivityListener
    @Override
    public void onTapItem(MenuItems menuItems) {
        // Intentで学園生活ガイドViewに遷移
        Log.i("intent", "onTapItem");
    }


    // URLから画像をImageViewに追加する
    public class DownloadUserImage extends AsyncTask<String, Void, Bitmap> {
        protected ImageView bmImage;

        // ImageViewの参照先を受け取る
        public DownloadUserImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }


        @Override
        public Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}