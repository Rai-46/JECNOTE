package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import jp.ac.jec.cm0146.jecnote.databinding.ActivityChatListBinding;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;

public class ChatListActivity extends AppCompatActivity {

    private ActivityChatListBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setToken();
        setListener();

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());

        //TODO: ユーザが教員だったら、searchUsersを表示する。
        if((preferenceManager.getBoolean(Constants.IS_TEACHER) != null) && preferenceManager.getBoolean(Constants.IS_TEACHER)) {
            binding.searchUsers.setVisibility(View.VISIBLE);
            binding.searchUsers.setOnClickListener(v -> {

            });
        }
    }

    private void setToken() {
        // 通知に必要なFCMトークン
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateTokens);
    }

    private void updateTokens(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USER)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.USER_FCM_TOKEN, token);
    }
}