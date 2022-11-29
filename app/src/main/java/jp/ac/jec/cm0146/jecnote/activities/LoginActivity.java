package jp.ac.jec.cm0146.jecnote.activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import jp.ac.jec.cm0146.jecnote.R;
import jp.ac.jec.cm0146.jecnote.databinding.ActivityLoginBinding;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private PreferenceManager preferenceManager;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseFirestore database;

    private static final int RC_SIGN_IN = 9001; // ここはユニークのリクエストID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("ja");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListener();

    }

    private void setListener() {
        binding.googleSignInButton.setOnClickListener(v -> signIn());
    }

    // Toast表示
    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // signIn
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                showToast("成功:" + account.getId() + "\n" + account.getDisplayName() + "\n" + account.getFamilyName() + "\n" + account.getEmail());



//                preferenceManager.putString(Constants.KEY_USER_NAME, account.getDisplayName());
//                preferenceManager.putString(Constants.KEY_USER_IMAGE, String.valueOf(account.getPhotoUrl()));
//                preferenceManager.putString(Constants.KEY_USER_EMAIL, account.getEmail());

                firebaseAuthWithGoogle(account.getIdToken(), account.getDisplayName(), String.valueOf(account.getPhotoUrl()), account.getEmail());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                showToast("Google sign in failed" + e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken, String userName, String userImage, String userEmail) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {// ログイン成功
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOGIN", "signInWithCredential:success");

                            // FireStoreのインスタンス取得
                            database = FirebaseFirestore.getInstance();

                            // 別端末orアンインストール後に再度アプリログインした？(firestoreにemailがおんなじユーザある？）
                            database.collection(Constants.KEY_COLLECTION_USER)
                                    .whereEqualTo(Constants.KEY_USER_EMAIL, userEmail)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {// 以前のアカウントにログイン
                                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                                preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getString(Constants.KEY_USER_ID));
                                                preferenceManager.putString(Constants.KEY_USER_NAME, documentSnapshot.getString(Constants.KEY_USER_NAME));
                                                preferenceManager.putString(Constants.KEY_USER_IMAGE, documentSnapshot.getString(Constants.KEY_USER_IMAGE));
                                                preferenceManager.putString(Constants.KEY_USER_EMAIL, documentSnapshot.getString(Constants.KEY_USER_EMAIL));
                                            } else {// アカウント新規登録


                                                // データベースにユーザ登録
                                                HashMap<String , Object > map = new HashMap<>();
                                                // アイコン
                                                map.put(Constants.KEY_USER_IMAGE, userImage);
                                                // メールアドレス
                                                map.put(Constants.KEY_USER_EMAIL, userEmail);
                                                // ユーザ名（21cm0100電子太郎）
                                                map.put(Constants.KEY_USER_NAME, userName);
                                                database.collection(Constants.KEY_COLLECTION_USER)
                                                        .add(map)
                                                        .addOnSuccessListener( documentReference -> {
                                                            // preferenceに登録
                                                            preferenceManager.putString(Constants.KEY_USER_NAME, userName);
                                                            preferenceManager.putString(Constants.KEY_USER_EMAIL, userEmail);
                                                            preferenceManager.putString(Constants.KEY_USER_IMAGE, userImage);
                                                            preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                                                        })
                                                        .addOnFailureListener(exception -> {
                                                            showToast(exception.getMessage());
                                                        });

                                                showToast("else");

                                            }
                                        }
                                    });



                            // preferenceに
                            preferenceManager.putString(Constants.KEY_AUTH_TOKEN, idToken);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOGIN", "signInWithCredential:failure", task.getException());
                            showToast("Authentication failed.");
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: 別端末や、アンインストールした際のおんなじアカウントにログインする方法を！signin処理

        // ログインしたことがあるか？（同じ端末でアプリを開き直した時の）
        if(preferenceManager.getString(Constants.KEY_AUTH_TOKEN) != null) {
            // FireStoreに登録するための準備
            database = FirebaseFirestore.getInstance();
            HashMap<String , Object > map = new HashMap<>();

            // アカウントの更新を確認する
            String userName = preferenceManager.getString(Constants.KEY_USER_NAME);
            String userImage = preferenceManager.getString(Constants.KEY_USER_IMAGE);

            if(userName.equals(mAuth.getCurrentUser().getDisplayName())) {// 名前の変更があれば、
                // preferenceに再登録
                preferenceManager.putString(Constants.KEY_USER_NAME, mAuth.getCurrentUser().getDisplayName());
                // firestoreに再登録
                map.put(Constants.KEY_USER_NAME, mAuth.getCurrentUser().getDisplayName());
            }
            if(userImage.equals(mAuth.getCurrentUser().getPhotoUrl())) {// アイコンの変更があれば、
                preferenceManager.putString(Constants.KEY_USER_IMAGE, String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
                map.put(Constants.KEY_USER_IMAGE, String.valueOf(mAuth.getCurrentUser().getPhotoUrl()));
            }

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }
    }
}