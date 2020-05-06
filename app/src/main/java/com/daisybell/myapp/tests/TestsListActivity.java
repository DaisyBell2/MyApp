package com.daisybell.myapp.tests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.R;
import com.daisybell.myapp.theory.Theory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TestsListActivity extends AppCompatActivity {

    private ListView mLvTests;
    private ArrayAdapter<String> mAdapter;
    private List<String> mListTitle;
    private List<Theory> mListTests;
    private DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_list);

        init();
    }
    private void init() {
        mLvTests = findViewById(R.id.lvTests);
        mListTitle = new ArrayList<>();
        mListTests = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListTitle);
        mLvTests.setAdapter(mAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.TESTS_KEY);
    }

}
