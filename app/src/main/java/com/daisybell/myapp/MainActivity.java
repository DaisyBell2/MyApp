package com.daisybell.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.check_list.AddCheckListActivity;
import com.daisybell.myapp.check_list.CheckListNameActivity;
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

    private Boolean emailVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Главный экран");

        init();

        Intent intent = getIntent();
        if (intent != null) {
            emailVerified = intent.getBooleanExtra("emailVerified", false);
        }

        if (emailVerified) {
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

    // Выход из аккаунта
    public void onClickSingOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

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

    // Полностью закрывает приложение
    public void onBackPressed() {
        System.gc();
        System.exit(0);
    }
}
