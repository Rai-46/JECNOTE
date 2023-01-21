package jp.ac.jec.cm0146.jecnote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import jp.ac.jec.cm0146.jecnote.adapters.SearchUserAdapter;
import jp.ac.jec.cm0146.jecnote.databinding.ActivitySearchUserBinding;
import jp.ac.jec.cm0146.jecnote.listener.SearchUserActivityListener;
import jp.ac.jec.cm0146.jecnote.models.StudentUser;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;

public class SearchUserActivity extends AppCompatActivity implements SearchUserActivityListener {

    private ActivitySearchUserBinding binding;
    private InputMethodManager mInputMethodManager;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        init();
        setListener();
    }

    private void init () {
        mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void isLoading(boolean bool) {
        if(bool == true) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.studentUserList.setVisibility(View.GONE);
            binding.errorMessage.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.studentUserList.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorMessage() {
        binding.errorMessage.setText("ユーザが見つかりませんでした。");
        binding.errorMessage.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
        binding.studentUserList.setVisibility(View.GONE);
    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.searchFromSchoolID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // もし、未入力だったら、
                    if(v.getText().toString().length() == 0) {
                        showErrorMessage();
                        return false;
                    }

                    // ここでfireStoreで検索をかける
                    getUserList(v.getText().toString());
                    // キーボードしまう
                    mInputMethodManager.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });

    }

    private void getUserList(String keyword) {
        // firestoreから検索条件に一部でも一致したら、そのユーザを返す。
        // FIXME: 部分一致の検索はできないそう。前方一致はできるそうなので、それで実装する。
        // やろうと思えば、できそうだが、（onComplete内でkeywordと比べて、、、、）毎回全ユーザをゲットするのは通信も大きく無駄になるであろう

        isLoading(true);


        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USER)
                .orderBy(Constants.KEY_USER_EMAIL)
                .whereNotEqualTo(Constants.KEY_USER_EMAIL, preferenceManager.getString(Constants.KEY_USER_EMAIL))// 自分と同じEmailを場外
                .whereEqualTo(Constants.IS_TEACHER, null)// 教員ではない
                .startAt(keyword)
                .endAt(keyword + "\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {// データある
                            Log.i("hogehoge", "94");
                            List<StudentUser> users = new ArrayList<>();
                            for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                // 学生のユーザデータを格納
                                StudentUser studentUser = new StudentUser();
                                Log.i("hogehoge", "99");
                                studentUser.userDisplayName = queryDocumentSnapshot.getString(Constants.KEY_USER_NAME);
                                studentUser.userEmail = queryDocumentSnapshot.getString(Constants.KEY_USER_EMAIL);
                                studentUser.userImage = queryDocumentSnapshot.getString(Constants.KEY_USER_IMAGE);
                                studentUser.id = queryDocumentSnapshot.getId();
                                users.add(studentUser);
                            }
                            if (users.size() > 0) {
                                // ユーザ見つけた時
                                Log.i("hogehoge", "107");
                                // ここで、ListViewへセット
                                SearchUserAdapter adapter = new SearchUserAdapter(getApplicationContext(), SearchUserActivity.this);
                                for(StudentUser user : users) {
                                    adapter.add(user);
                                }
                                binding.studentUserList.setAdapter(adapter);

                                isLoading(false);
                                binding.errorMessage.setVisibility(View.GONE);

                            } else {
                                showErrorMessage();
                            }
                        } else {
                            showErrorMessage();
                        }
                    }
                });

    }

    // Listがタップされた時の処理。タップされたStudentUserの情報をもらっている
    @Override
    public void onTapListener(StudentUser user) {
        // チャット画面に遷移。userを渡す。

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}