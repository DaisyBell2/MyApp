package com.daisybell.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.check_list.AddCheckListActivity;
import com.daisybell.myapp.check_list.CheckListNameActivity;
import com.daisybell.myapp.result.ResultCheckListNameActivity;
import com.daisybell.myapp.result.ResultTestsNameActivity;
import com.daisybell.myapp.test.AddQuizTestsActivity;
import com.daisybell.myapp.test.TestsListActivity;
import com.daisybell.myapp.theory.AddTheoryActivity;
import com.daisybell.myapp.theory.TheoryListActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Главный экран");

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
}
