package com.daisybell.myapp.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Map;
import java.util.TreeMap;

public class QuizTestActivity extends AppCompatActivity {

    private String TAG = "getQuestion";

    private TextView tvScore;
    private TextView tvQuestion;
    private RadioGroup rgAllAnswer;
    private RadioButton rbAnswer1;
    private RadioButton rbAnswer2;
    private RadioButton rbAnswer3;
    private RadioButton rbAnswer4;
    private ArrayList<RadioButton> option = new ArrayList<>();

    private DatabaseReference mDataBase;

    private List<Test> mListTest = new ArrayList<>();
    private List<String> mListNameTest = new ArrayList<>();
    private Map<String, Test> mMapTest = new TreeMap<>();
    private Map<String, String> mMapNameTest = new TreeMap<>();

    private String question;
    private int rightAnswer;
    private int rightAnswerPosition;
    private int countOfQuestion = 0;
    private int countOfAnswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_tests);

        if (savedInstanceState != null) {
            Constant.INDEX_ID = savedInstanceState.getInt(Constant.KEY_INDEX_ID, 0);
            Constant.INDEX_QUEST = savedInstanceState.getInt(Constant.KEY_INDEX_QUEST, 0);
//            Constant.INDEX_TEST = savedInstanceState.getInt(Constant.KEY_INDEX_TEST, 0);
        }

        init();
        getIntentMain();
    }
    private void init() {
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.TESTS_KEY);
        tvScore = findViewById(R.id.tvScore);
        tvQuestion = findViewById(R.id.tvQuestion);
        rgAllAnswer = findViewById(R.id.rgAllAnswer);
        rbAnswer1 = findViewById(R.id.rbAnswer1);
        rbAnswer2 = findViewById(R.id.rbAnswer2);
        rbAnswer3 = findViewById(R.id.rbAnswer3);
        rbAnswer4 = findViewById(R.id.rbAnswer4);

        option.add(rbAnswer1);
        option.add(rbAnswer2);
        option.add(rbAnswer3);
        option.add(rbAnswer4);
    }

    private void getIntentMain() {
        Intent intent = getIntent();
        if (intent != null) {
            tvQuestion.setText(intent.getStringExtra(Constant.TEST_QUESTION));
            rbAnswer1.setText(intent.getStringExtra(Constant.TEST_OPTION1));
            rbAnswer2.setText(intent.getStringExtra(Constant.TEST_OPTION2));
            rbAnswer3.setText(intent.getStringExtra(Constant.TEST_OPTION3));
            rbAnswer4.setText(intent.getStringExtra(Constant.TEST_OPTION4));
        }
    }

    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListTest.size() > 0) mListTest.clear();
                if (mListNameTest.size() > 0) mListNameTest.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                String name_test = ds.getKey();
//                tvQuestion.append(name_test);
//                Log.d(TAG, name_test);
                    Test test = ds.getValue(Test.class);
                    assert test != null;
                    mMapTest.put(Constant.KEY_INDEX_TEST, test);
                    mMapNameTest.put(Constant.KEY_INDEX_TEST, test.nameTest);
                    mListNameTest.add(test.question);
                    mListTest.add(test);
                    Log.d(TAG, "Value is: " + test);
                    Log.d(TAG, "ListTest is: " + mListTest);
                    Log.d(TAG, "ListNameTest is: " + mListNameTest);
                    Log.d(TAG, "MapTest is: " + mMapTest);
                    Log.d(TAG, "MapNameTest is: " + mMapNameTest);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBase.addValueEventListener(vListener);
    }
    private void getQuestion() {
        Test test = new Test();
        String question = test.getQuestion();
        tvQuestion.setText(question);
    }
}
