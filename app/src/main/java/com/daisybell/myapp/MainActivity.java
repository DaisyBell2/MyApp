package com.daisybell.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.check_list.AddCheckListActivity;
import com.daisybell.myapp.check_list.CheckListNameActivity;
import com.daisybell.myapp.menu.AboutApplicationActivity;
import com.daisybell.myapp.menu.SettingsActivity;
import com.daisybell.myapp.menu.UsersActivity;
import com.daisybell.myapp.result.ResultCheckListNameActivity;
import com.daisybell.myapp.result.ResultTestsNameActivity;
import com.daisybell.myapp.test.AddQuizTestsActivity;
import com.daisybell.myapp.test.TestsListActivity;
import com.daisybell.myapp.theory.AddTheoryActivity;
import com.daisybell.myapp.theory.TheoryListActivity;
import com.daisybell.myapp.user_email.AddNewUserActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button btAddTheory, btAddTests, btAddCheckList, btAddNewUser;

//    private Boolean emailVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

//        Intent intent = getIntent();
//        if (intent != null) {
//            emailVerified = intent.getBooleanExtra("emailVerified", false);
//        }

        if (Constant.EMAIL_VERIFIED) {
            btAddTheory.setVisibility(View.VISIBLE);
            btAddTests.setVisibility(View.VISIBLE);
            btAddCheckList.setVisibility(View.VISIBLE);
            btAddNewUser.setVisibility(View.VISIBLE);
        } else {
            btAddTheory.setVisibility(View.GONE);
            btAddTests.setVisibility(View.GONE);
            btAddCheckList.setVisibility(View.GONE);
            btAddNewUser.setVisibility(View.GONE);
        }

    }
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);

        btAddTheory = findViewById(R.id.btAddTheory);
        btAddTests = findViewById(R.id.btAddTests);
        btAddCheckList = findViewById(R.id.btAddCheckList);
        btAddNewUser = findViewById(R.id.btAddNewUser);
    }

    public void onClickTheoryList(View view) {
        Intent intent = new Intent(MainActivity.this, TheoryListActivity.class);
        startActivity(intent);
    }
    public void onClickAddTheory(View view) {
        Intent intent = new Intent(MainActivity.this, AddTheoryActivity.class);
        startActivity(intent);
    }

    public void onClickTestsList(View view) {
        Intent intent = new Intent(MainActivity.this, TestsListActivity.class);
        startActivity(intent);
    }

    public void onClickAddTest(View view) {
        Intent intent = new Intent(MainActivity.this, AddQuizTestsActivity.class);
        startActivity(intent);
    }

//    // Выход из аккаунта
//    public void onClickSingOut(View view) {
//        FirebaseAuth.getInstance().signOut();
//        startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        finish();
//    }

    public void onClickAddCheckList(View view) {
        startActivity(new Intent(MainActivity.this, AddCheckListActivity.class));
    }

    public void onClickCheckList(View view) {
        startActivity(new Intent(MainActivity.this, CheckListNameActivity.class));
    }

    public void onClickResultTests(View view) {
        startActivity(new Intent(MainActivity.this, ResultTestsNameActivity.class));
    }

    public void onClickResultCheckList(View view) {
        startActivity(new Intent(MainActivity.this, ResultCheckListNameActivity.class));
    }

    public void onClickAddNewUser(View view) {
        startActivity(new Intent(MainActivity.this, AddNewUserActivity.class));
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
                startActivity(new Intent(MainActivity.this, UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(MainActivity.this, AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
