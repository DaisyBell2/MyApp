package com.daisybell.myapp.result;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.check_list.SaveResultCheckList;
import com.daisybell.myapp.test.SaveResultTests;

public class ResultTestsActivity extends AppCompatActivity {

    private static String TAG = "ResultTests";

    private TextView tvFullName, tvNameTest, tvResultTest, tvDate, tvStartTime, tvFinishTime;

    private SaveResultTests mSaveResultTests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_tests);
        setTitle("Результаты тестов");

        init();
        getIntentMain();
    }

    private void init() {
        tvFullName = findViewById(R.id.tvFullName);
        tvNameTest = findViewById(R.id.tvNameTest);
        tvResultTest = findViewById(R.id.tvResultTest);
        tvDate = findViewById(R.id.tvDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvFinishTime = findViewById(R.id.tvFinishTime);
    }

    private void getIntentMain() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mSaveResultTests = (SaveResultTests) intent.getSerializableExtra("item");

            if (mSaveResultTests != null) {
                tvFullName.setText(mSaveResultTests.getFullName());
                tvNameTest.setText(mSaveResultTests.getNameTest());
                tvResultTest.setText(mSaveResultTests.getResultTest());
                tvDate.setText(mSaveResultTests.getDateTest());
                tvStartTime.setText(mSaveResultTests.getStartTimeTest());
                tvFinishTime.setText(mSaveResultTests.getFinishTimeTest());
            }
        }

//        Intent intent = getIntent();
//        if (intent != null) {
//            tvFullName.setText(intent.getStringExtra(Constant.RESULT_TESTS_NAME));
//            tvNameTest.setText(intent.getStringExtra(Constant.RESULT_TESTS_NAME_TEST));
//            tvResultTest.setText(intent.getStringExtra(Constant.RESULT_TESTS_R));
//            tvDate.setText(intent.getStringExtra(Constant.RESULT_TESTS_DATE));
//            tvStartTime.setText(intent.getStringExtra(Constant.RESULT_TESTS_START_TIME));
//            tvFinishTime.setText(intent.getStringExtra(Constant.RESULT_TESTS_FINISH_TIME));
//        }
    }

    public void onClickBackToHome(View view) {
        startActivity(new Intent(ResultTestsActivity.this, MainActivity.class));
        finish();
    }
}
