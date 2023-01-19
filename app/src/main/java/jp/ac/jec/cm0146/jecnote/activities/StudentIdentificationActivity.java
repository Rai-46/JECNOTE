package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import jp.ac.jec.cm0146.jecnote.databinding.ActivityStudentIdentificationBinding;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;

public class StudentIdentificationActivity extends AppCompatActivity {

    private ActivityStudentIdentificationBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentIdentificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListener();

    }

    private void makeToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.nextBtn.setOnClickListener(v -> {
            binding.backBtn.setVisibility(View.GONE);
            binding.nextBtn.setVisibility(View.GONE);
            binding.startBtn.setVisibility(View.VISIBLE);
            binding.attentionText.setVisibility(View.GONE);
            binding.welcomeText.setVisibility(View.VISIBLE);
        });
        binding.startBtn.setOnClickListener(v -> {
            makeToast("スタート！");
            preferenceManager.putBoolean(Constants.KEY_LOGINED, true);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Firebaseにアカウント完成したと追記
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USER)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .update(Constants.ACCOUNT_SETTING_END, true);

        });
    }
}