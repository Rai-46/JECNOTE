package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import jp.ac.jec.cm0146.jecnote.databinding.ActivityMainBinding;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        testInit();
    }

    private void testInit() {
        binding.testName.setText(preferenceManager.getString(Constants.KEY_USER_NAME));
        binding.textEmail.setText(preferenceManager.getString(Constants.KEY_USER_EMAIL));

        new DownloadUserImage(binding.testImage).execute(preferenceManager.getString(Constants.KEY_USER_IMAGE));
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