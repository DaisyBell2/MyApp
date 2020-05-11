package com.daisybell.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daisybell.myapp.test.AddQuizTestsActivity;
import com.daisybell.myapp.test.TestsListActivity;
import com.daisybell.myapp.theory.AddTheoryActivity;
import com.daisybell.myapp.theory.TheoryListActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
