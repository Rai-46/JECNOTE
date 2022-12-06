package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import jp.ac.jec.cm0146.jecnote.databinding.ActivityUserIdentificationBinding;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;
import kotlin.jvm.internal.PackageReference;

// ユーザが教員か学生かを見分けるView
public class UserIdentificationActivity extends AppCompatActivity {

    private ActivityUserIdentificationBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserIdentificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListener();

    }

    private void setListener() {
        binding.BtnTeacher.setOnClickListener(v -> {
            preferenceManager.putString(Constants.WHICH_SELECT, Constants.SELECTED_TEACHER);
            Intent intent = new Intent(getApplicationContext(), TeacherIdentificationActivity.class);
            startActivity(intent);
        });

        binding.BtnStudent.setOnClickListener(v -> {
            preferenceManager.putString(Constants.WHICH_SELECT, Constants.SELECTED_STUDENT);
            Intent intent = new Intent(getApplicationContext(), StudentIdentificationActivity.class);
            startActivity(intent);
        });
    }
}