package com.daisybell.myapp.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class QuizTestActivity extends AppCompatActivity {

    private static String TAG = "getQueTest";

    private TextView tvScore;
    private TextView tvQuestion;
    private RadioGroup rgAllAnswer;
    private RadioButton rbAnswer1;
    private RadioButton rbAnswer2;
    private RadioButton rbAnswer3;
    private RadioButton rbAnswer4;
    private Button btNext;
    private boolean isClick = true;

    private int index = -1;
    private int posNumQuest = 0;
    private int posNumRightAnswer = 0;

    Handler handler = new Handler();

    private ArrayList<RadioButton> option = new ArrayList<>();
    private ArrayList<Integer> numQuest = new ArrayList<>();

    private Map<String, Object> mMap = new TreeMap<>();
    private Map<String, Object> mMap2 = new TreeMap<>();
//    private Map<String, Object> mMapQuestion = new TreeMap<>();
//    private Map<String, Object> mMapSizeQuest = new TreeMap<>();

    private DatabaseReference mDataBase;

    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String rightAnswer;
    private String rightOption;
    private int rightAnswerPosition;
    private int countOfQuestion = 0;
    private int countOfAnswer = 0;
    private int position;

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
        getDataFromDB();
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
            position = intent.getIntExtra(Constant.POSITION, 0);
        }
    }

    // Получаем данные с firebase
    private void getDataFromDB() {

        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                    String key = ds1.getKey();
//                    Log.d(TAG, "key " + key);
                    Constant.QUEST_SIZE = 0;
                    Constant.TEST_SIZE++;

                    for (DataSnapshot ds2 : ds1.getChildren()) {
                        String key2 = ds2.getKey();
//                        Log.d(TAG, "key2 " + key2);
                        Test test = ds2.getValue(Test.class);
                        assert test != null;
                        mMap = test.toMap();
//                        Log.d(TAG, "mMap " + mMap);
                        mMap2.putAll(mMap);
                        Constant.QUEST_SIZE++;
                        index++;
                        Log.d(TAG, "mMap2 " + mMap2);
//                        Log.d(TAG, "index " + index);
                    }
                    if (index >= 0) {

                        numQuest.add(index);
                        index = -1;
//                        Log.d(TAG, "numQuestSize1 " + numQuest.size());
//                        Log.d(TAG, "numQuest1 " + numQuest);
//                        for (int i = 0; i < numQuest.size(); i++) {
//                            Log.d(TAG, "numQuestTet2 " + numQuest.get(i));
//                        }

                    }
                }
                    option1 = String.valueOf(mMap2.get("option1_" + position + "." + posNumQuest));
                    option2 = String.valueOf(mMap2.get("option2_" + position + "." + posNumQuest));
                    option3 = String.valueOf(mMap2.get("option3_" + position + "." + posNumQuest));
                    option4 = String.valueOf(mMap2.get("option4_" + position + "." + posNumQuest));
                    rightAnswer = String.valueOf(mMap2.get("rightAnswer_" + position + "." + posNumRightAnswer));
                    Log.d(TAG, "rightAnswer1 " + rightAnswer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBase.addValueEventListener(vListener);
        Constant.TEST_SIZE = -1;
        Constant.QUESTION_NUMBER = 0;
    }


    // Обработчик кнопки "Next", меняет вопрос и показывает, верый выбор или нет
    public void onClickNextQuestion(View view) {
        final RadioButton rb = findViewById(rgAllAnswer.getCheckedRadioButtonId());
        if (rb != null) {
            rightOption();
            if (posNumQuest > numQuest.get(position)) {
                if (isClick) {
                    for (int i = 0; i < rgAllAnswer.getChildCount(); i++) {
                        rgAllAnswer.getChildAt(i).setEnabled(false);
                    }
                    check(rb);
                    isClick = false;
                }
                Toast.makeText(this, "Вопросы кончились!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QuizTestActivity.this, ResultTestActivity.class);
                intent.putExtra(Constant.RESULT_TEST, countOfAnswer);
                intent.putExtra(Constant.NUM_QUEST_TEST, posNumQuest);
                startActivity(intent);
            } else {
                for (int i = 0; i < rgAllAnswer.getChildCount(); i++) {
                    rgAllAnswer.getChildAt(i).setEnabled(false);
                }
                check(rb);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rgAllAnswer.clearCheck();
                        }
                    }, 800);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rb.setTextColor(getResources().getColor(R.color.colorBlack));
                            getQuestionAndAnswer();
                            for (int i = 0; i < rgAllAnswer.getChildCount(); i++) {
                                rgAllAnswer.getChildAt(i).setEnabled(true);
                            }
                        }
                    }, 1000);
                }


        } else {
            Toast.makeText(this, "Вы ничего не выбрали!", Toast.LENGTH_SHORT).show();
        }
    }
    // Метод для проверки правельного ответа
    private void check(RadioButton radioButton) {
        if (rightOption.equals(radioButton.getText().toString())) {
            radioButton.setTextColor(getResources().getColor(R.color.colorGreen));
            countOfAnswer++;
            Toast.makeText(this, "Верно", Toast.LENGTH_SHORT).show();
        } else {
            radioButton.setTextColor(getResources().getColor(R.color.colorRed));
            Toast.makeText(this, "Не верно", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "countOfAnswer " + countOfAnswer);
    }
    // Метод получения и обозначения данными на кнопки
    private void getQuestionAndAnswer() {
//        posNumQuest++;
        posNumRightAnswer++;
        question = String.valueOf(mMap2.get("question_" + position + "." + posNumQuest));
        option1 = String.valueOf(mMap2.get("option1_" + position + "." + posNumQuest));
        option2 = String.valueOf(mMap2.get("option2_" + position + "." + posNumQuest));
        option3 = String.valueOf(mMap2.get("option3_" + position + "." + posNumQuest));
        option4 = String.valueOf(mMap2.get("option4_" + position + "." + posNumQuest));
        rightAnswer = String.valueOf(mMap2.get("rightAnswer_" + position + "." + posNumRightAnswer));
        tvQuestion.setText(question);
        rbAnswer1.setText(option1);
        rbAnswer2.setText(option2);
        rbAnswer3.setText(option3);
        rbAnswer4.setText(option4);

    }
    // Получаем правильный ответ и преобразуем его в String для последующей проверки
    private void rightOption() {
        posNumQuest++;
        switch (rightAnswer) {
            case "1":
                rightOption = option1;
                break;
            case "2":
                rightOption = option2;
                break;
            case "3":
                rightOption =option3;
                break;
            case "4":
                rightOption = option4;
                break;
        }
    }
}
