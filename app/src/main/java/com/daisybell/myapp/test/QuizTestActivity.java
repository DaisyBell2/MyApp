package com.daisybell.myapp.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.Map;

public class QuizTestActivity extends AppCompatActivity {

    private static String TAG = "getQueTest";

    private TextView tvScore;
    private TextView tvQuestion;
    private RadioGroup rgAllAnswer;
    private RadioButton rbAnswer1;
    private RadioButton rbAnswer2;
    private RadioButton rbAnswer3;
    private RadioButton rbAnswer4;
    private ArrayList<RadioButton> option = new ArrayList<>();

    private DatabaseReference mDataBase;

    Map<String, Object> mMap;
    Map<String, ArrayList<String>> mMapAllOption;
    Map<String, Object> mMapQuestion;
    Map<String, Object> mMapRightAnswer;
//    Map<String, Object> mMapTestKey;

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
        getQuestion();
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

    private void getQuestion() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String testKey = dataSnapshot1.getKey();
                    Log.d(TAG, "testKey " + testKey);
                    assert testKey != null;
                    Constant.QUEST_SIZE = Constant.INDEX_QUEST_MAP;
                    Constant.INDEX_QUEST_MAP = 1;
                    Constant.INDEX_QUEST_MAP_TEST++;
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        String key2 = dataSnapshot2.getKey();
                        Test test = dataSnapshot2.getValue(Test.class);
                        assert test != null;
                        mMap = test.toMap();
                        mMapQuestion = test.toMapQuestion();
                        mMapAllOption = test.toMapAllOption();
                        mMapRightAnswer = test.toMapRightAnswer();
                        Constant.INDEX_QUEST_MAP++;
                        Log.d(TAG, "key2 " + key2);
                        Log.d(TAG, "test " + test);
                        Log.d(TAG, "mMap " + mMap);
                        Log.d(TAG, "mMapQuestion " + mMapQuestion);
                        Log.d(TAG, "mMapAllOption " + mMapAllOption);
                        Log.d(TAG, "mMapRightAnswer " + mMapRightAnswer);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBase.addValueEventListener(vListener);
        Constant.TEST_SIZE = Constant.INDEX_QUEST_MAP_TEST;
        Constant.INDEX_QUEST_MAP_TEST = 0;
        Constant.INDEX_QUEST_MAP = 1;
    }

    @SuppressLint("SetTextI18n")
    public void onClickNextQuestion(View view) {
        int i = 1;
        for (Object key : mMapQuestion.values()) {
            tvQuestion.setText(String.valueOf(key));
        }
//        tvQuestion.setText("" + mMapQuestion.get("question" + "("+ 1 +"."+ 2 +")"));
        Log.d(TAG, "tvQuestion "+ mMapQuestion.values());
        Log.d(TAG, "tvQuestion.setText "+ tvQuestion.getText().toString());
        i++;
    }
}
