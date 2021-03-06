package com.daisybell.myapp.result;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.check_list.SaveResultCheckList;
import com.daisybell.myapp.menu.AboutApplicationActivity;
import com.daisybell.myapp.menu.SettingsActivity;
import com.daisybell.myapp.menu.UsersActivity;
import com.daisybell.myapp.test.SaveResultTests;
import com.daisybell.myapp.user_email.AddNewUserActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ResultTestsActivity extends AppCompatActivity {

    private static String TAG = "ResultTests";

    private TextView tvFullName, tvNameTest, tvResultTest, tvDate, tvStartTime, tvFinishTime;

    private SaveResultTests mSaveResultTests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_tests);

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
        Intent intent = new Intent(ResultTestsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Метод для отображения 3х точек на toolbar'e
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem search_item = menu.findItem(R.id.search_view);
        MenuItem users_item = menu.findItem(R.id.users);
        search_item.setVisible(false);
        if (!Constant.EMAIL_VERIFIED) {
            users_item.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.users: // Пользователи
                startActivity(new Intent(ResultTestsActivity.this, UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(ResultTestsActivity.this, SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(ResultTestsActivity.this, AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ResultTestsActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
