package com.daisybell.myapp.theory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TheoryListActivity extends AppCompatActivity {

    private ListView mLvTheory;
    private ArrayAdapter<String> mAdapter;
    private List<String> mListTitle;
    private List<Theory> mListTheory;
    private DatabaseReference mDataBase;
    private String THEORY_KEY = "Theory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory_list);
        init();
        getDataFromDB();
        onClickItem();
    }
    private void init() {
        mLvTheory = findViewById(R.id.lvTheory);
        mListTitle = new ArrayList<>();
        mListTheory = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListTitle);
        mLvTheory.setAdapter(mAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(THEORY_KEY);
    }
    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListTitle.size() > 0)mListTitle.clear();
                if (mListTheory.size() > 0)mListTheory.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Theory theory = ds.getValue(Theory.class);
                    assert theory != null;
                    mListTitle.add(theory.title);
                    mListTheory.add(theory);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBase.addValueEventListener(vListener);
    }
    private void onClickItem() {
        mLvTheory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Theory theory = mListTheory.get(position);
                Intent intent = new Intent(TheoryListActivity.this, TheoryShowActivity.class);
                intent.putExtra(Constant.THEORY_TITLE, theory.title);
                intent.putExtra(Constant.THEORY_TEXT, theory.text);
                startActivity(intent);
            }
        });
    }
}
