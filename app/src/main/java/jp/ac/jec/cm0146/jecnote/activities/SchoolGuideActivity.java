package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import jp.ac.jec.cm0146.jecnote.adapters.GuideListAdapter;
import jp.ac.jec.cm0146.jecnote.databinding.ActivitySchoolGuideBinding;
import jp.ac.jec.cm0146.jecnote.listener.SchoolGuideActivityListener;
import jp.ac.jec.cm0146.jecnote.models.GuideItem;
import jp.ac.jec.cm0146.jecnote.network.AsyncGuideListRequest;
import jp.ac.jec.cm0146.jecnote.utilities.JsonHelper;

public class SchoolGuideActivity extends AppCompatActivity implements SchoolGuideActivityListener {

    private ActivitySchoolGuideBinding binding;
    private InputMethodManager mInputMethodManager;
    private String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchoolGuideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        request("");
        setListener();
    }

    private void request(String keyword) {

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.listView.setVisibility(View.GONE);

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority("21cm0146.main.jp");
        uriBuilder.path("jecGuide/outputHTML.php");
        uriBuilder.appendQueryParameter("keyword", keyword);

        AsyncGuideListRequest req = new AsyncGuideListRequest(this);
        req.execute(uriBuilder);

    }

    private void setList(String json) {

        ArrayList<GuideItem> guideItems = JsonHelper.parseGuideListJSON(json);
        GuideListAdapter adapter = new GuideListAdapter(getApplicationContext(), this);
        for(GuideItem guideItem : guideItems) {
            adapter.add(guideItem);
        }
        binding.listView.setAdapter(adapter);


        if(guideItems.isEmpty()) {
            binding.errorMessage.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.errorMessage.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            binding.listView.setVisibility(View.VISIBLE);
        }

    }

    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.inputKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    EditText value = binding.inputKeyword;
                    keyword = value.getText().toString();

                    request(keyword);

                    // キーボードしまう
                    mInputMethodManager.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onTapItem(GuideItem item) {
        // 画面遷移
        Intent intent = new Intent(getApplicationContext(), GuideView.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }

    @Override
    public void parseJson(String json) {
        setList(json);
    }
}