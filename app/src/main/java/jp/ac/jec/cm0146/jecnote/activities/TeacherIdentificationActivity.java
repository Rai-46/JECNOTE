package jp.ac.jec.cm0146.jecnote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.PrecomputedText;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import jp.ac.jec.cm0146.jecnote.R;
import jp.ac.jec.cm0146.jecnote.databinding.ActivityTeacherIdentificationBinding;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;

public class TeacherIdentificationActivity extends AppCompatActivity {

    private ActivityTeacherIdentificationBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherIdentificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListener();

    }

    private void makeToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setListener() {
//        setSpinner();

        binding.backBtn.setOnClickListener(v -> finish());
        binding.nextBtn.setOnClickListener(v -> {

            String password = "";
            //TODO: 間違っていたor未入力はダイヤログ表示

            TextView inputPass = (TextView) findViewById(R.id.identificationPassword);
            password = inputPass.getText().toString();

            if("".equals(password)) {
                showAlertListener();
                return;
            }
            //TODO: パスワード
            if(password.equals(preferenceManager.getString(Constants.KEY_TEACHER_PASSWORD))) {
                preferenceManager.putBoolean(Constants.KEY_LOGINED, true);
                preferenceManager.putBoolean(Constants.IS_TEACHER, true);

                // FireStoreのユーザ情報に教員であることを追加する。
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USER)
                        .document(preferenceManager.getString(Constants.KEY_USER_ID));
                documentReference.update(Constants.IS_TEACHER, true);




                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                showAlertListener();
            }




        });
    }

    private void showAlertListener() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherIdentificationActivity.this);
        builder.setTitle("確認").setMessage("正しいパスワードを入力してください。")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

//    private void setSpinner() {
//        List<String> spinnerList = Arrays.asList(getResources().getStringArray(R.array.subject));
//        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, spinnerList);
//
//        binding.dropDownView.setAdapter(adapter);
//        binding.dropDownView.setText("", false);
//        binding.dropDownView.addTextChangedListener(new TextWatcher() {// Spinnerのリスナー
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                makeToast(s.toString());
//            }
//        });
//    }
}