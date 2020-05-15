package com.daisybell.myapp.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TestsListActivity extends AppCompatActivity {

    private static String TAG = "getQueTest";

    private DatabaseReference mDataBase;
    private ArrayAdapter<String> mAdapter;
    private List<String> ListNameTest;
    private List<Test> ListTest;
    private ListView lvTests;
    private String key;
    private Map<String, Test> mMapTest = new TreeMap<>();
    Test test;
    String nameTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_list);

        init();
        getDataFromDB();
        setOnClickItem();
    }
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        Constant.INDEX_TEST = preferences.getInt(Constant.KEY_INDEX_TEST, 0);
        Constant.INDEX_ID = preferences.getInt(Constant.KEY_INDEX_ID, 0);
        Constant.INDEX_QUEST = preferences.getInt(Constant.KEY_INDEX_QUEST, 0);
//        key = preferences.getString("key", "");

        lvTests = findViewById(R.id.lvTests);
        ListNameTest = new ArrayList<>();
        ListTest = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ListNameTest);
        lvTests.setAdapter(mAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.TESTS_KEY);
    }

   // Получаем данные с firebase
    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (ListNameTest.size() > 0)ListNameTest.clear();
                if (ListTest.size() > 0)ListTest.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Test test = ds.getChildren().iterator().next().getValue(Test.class);
                        assert test != null;
                        ListNameTest.add(test.nameTest);
                        ListTest.add(test);
                        Log.d("getData", "TEst of: " + test);
                        Log.d("getData", "ListNameTest of: " + ListNameTest);
                        Log.d("getData", "ListTest of: " + ListTest);
                        Log.d("getData", "NameTest of: " + test.nameTest);
                        Log.d("getData", "QUANTITY_QUEST of: " + Constant.QUANTITY_QUEST);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("getError" ,"Failed to read value.", databaseError.toException());
            }
        };
        mDataBase.addValueEventListener(vListener);
    }
    private void setOnClickItem() {
        lvTests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Test test = ListTest.get(position);
                Intent intent = new Intent(TestsListActivity.this, QuizTestActivity.class);
                intent.putExtra(Constant.TEST_NAME, test.nameTest);
                intent.putExtra(Constant.TEST_QUESTION, test.question);
                intent.putExtra(Constant.TEST_OPTION1, test.option1);
                intent.putExtra(Constant.TEST_OPTION2, test.option2);
                intent.putExtra(Constant.TEST_OPTION3, test.option3);
                intent.putExtra(Constant.TEST_OPTION4, test.option4);
                intent.putExtra(Constant.TEST_RIGHT_ANSWER, test.rightAnswer);
                intent.putExtra(Constant.POSITION, position);
                startActivity(intent);
            }
        });
    }

}
