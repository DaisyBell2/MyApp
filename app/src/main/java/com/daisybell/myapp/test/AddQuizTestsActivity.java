package com.daisybell.myapp.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class AddQuizTestsActivity extends AppCompatActivity {

//    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

    Handler handler = new Handler();

    private EditText etEnterNameTest;
    private EditText etEnterQuestion;
    private EditText etOption1;
    private EditText etOption2;
    private EditText etOption3;
    private EditText etOption4;

    private RadioGroup mRadioGroup;
    private RadioButton mRBAnswerOption1;

    private DatabaseReference mDataBase;
    private ArrayList<String> initKey = new ArrayList<>();
    private Map<String, Object> mMap = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz_tests);

        if (savedInstanceState != null) {
            Constant.INDEX_ID = savedInstanceState.getInt(Constant.KEY_INDEX_ID, 0);
            Constant.INDEX_QUEST = savedInstanceState.getInt(Constant.KEY_INDEX_QUEST, 0);
            // Constant.INDEX_TEST = savedInstanceState.getInt(Constant.KEY_INDEX_TEST, 0);
        }

        init();
    }
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        Constant.INDEX_TEST = preferences.getInt(Constant.KEY_INDEX_TEST, 0);
        Constant.INDEX_ID = preferences.getInt(Constant.KEY_INDEX_ID, 0);
        Constant.INDEX_QUEST = preferences.getInt(Constant.KEY_INDEX_QUEST, 0);

        etEnterNameTest = findViewById(R.id.etEnterNameTest);
        etEnterQuestion = findViewById(R.id.etEnterQuestion);
        etOption1 = findViewById(R.id.etOption1);
        etOption2 = findViewById(R.id.etOption2);
        etOption3 = findViewById(R.id.etOption3);
        etOption4 = findViewById(R.id.etOption4);
        mRadioGroup = findViewById(R.id.rgAnswerOption);
        mRBAnswerOption1 = findViewById(R.id.rbAnswerOption1);
        mRBAnswerOption1.setChecked(true);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.TESTS_KEY);
    }

    // Кнопка для добавления вопроса и сохранения его в Map
    public void onClickAddQuestion(View view) {
        int checkedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();
        RadioButton myRadioButton = findViewById(checkedRadioButtonId);
        int id = Constant.INDEX_ID;
        String nameTest = etEnterNameTest.getText().toString().trim();
        String question = etEnterQuestion.getText().toString().trim();
        String option1 = etOption1.getText().toString().trim();
        String option2 = etOption2.getText().toString().trim();
        String option3 = etOption3.getText().toString().trim();
        String option4 = etOption4.getText().toString().trim();
        String rightAnswer = String.valueOf(myRadioButton.getText());
        Test newTest = new Test(id, nameTest, question, option1, option2, option3, option4, rightAnswer);
        if (!TextUtils.isEmpty(nameTest) && !TextUtils.isEmpty(question) && !TextUtils.isEmpty(option1)
                && !TextUtils.isEmpty(option2) && !TextUtils.isEmpty(option3) && !TextUtils.isEmpty(option4)) {
            mMap.put("quest" + Constant.INDEX_QUEST, newTest); // Сохраняем данные в Map
//            Log.d("mMap", "mMap " + mMap);
            Constant.INDEX_ID++;
            Constant.INDEX_QUEST++;
            etEnterNameTest.setEnabled(false);
            etEnterNameTest.setTextColor(getResources().getColor(R.color.colorGray));
            etEnterQuestion.setText("");
            etOption1.setText("");
            etOption2.setText("");
            etOption3.setText("");
            etOption4.setText("");
            mRBAnswerOption1.setChecked(true);
            Toast.makeText(this, R.string.adding, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.empty_text, Toast.LENGTH_SHORT).show();
        }
    }

    // Кнопка для сохранения всего теста в firebase
    public void onClickSaveTest(View view) {
        String mDataBaseId = mDataBase.push().getKey();
//        Log.d("mMap", "Привет это я!");
        if (!mMap.isEmpty()) {
            for (String key : mMap.keySet()) {
                assert mDataBaseId != null;
                mDataBase.child(mDataBaseId).child(key).setValue(mMap.get(key));
//                Log.d("mMap", "mMap2 " + mMap.get(key));
//                String index_key = String.valueOf(initKey.add(key));
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                preferences.edit().putString("key", index_key).apply();
            }
            etEnterNameTest.setText("");
            etEnterNameTest.setEnabled(true);
            Toast.makeText(this, R.string.saving, Toast.LENGTH_SHORT).show();

            // Обновляем переменные
            Constant.INDEX_ID = 0;
            Constant.INDEX_QUEST = 0;
//            Constant.INDEX_TEST++;

            // Сохраняем данные в SharedPreferences для будущего использования
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//            preferences.edit().putInt(Constant.KEY_INDEX_TEST, Constant.INDEX_TEST).apply();
            preferences.edit().putInt(Constant.KEY_INDEX_ID, Constant.INDEX_ID).apply();
            preferences.edit().putInt(Constant.KEY_INDEX_QUEST, Constant.INDEX_QUEST).apply();

            mMap.clear();

        } else {
            Toast.makeText(this, R.string.empty_data, Toast.LENGTH_SHORT).show();
        }
    }
    public void onClickLookTests(View view) {
        Intent intent = new Intent(AddQuizTestsActivity.this, TestsListActivity.class);
        startActivity(intent);
        // Очещаем все поля через 0.2 сек
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMap.clear();
                etEnterNameTest.getText().clear();
                etEnterQuestion.getText().clear();
                etOption1.getText().clear();
                etOption2.getText().clear();
                etOption3.getText().clear();
                etOption4.getText().clear();
                mRBAnswerOption1.setChecked(true);
            }
        }, 200);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.getInt(Constant.KEY_INDEX_ID, Constant.INDEX_ID);
        savedInstanceState.getInt(Constant.KEY_INDEX_QUEST, Constant.INDEX_QUEST);
        //savedInstanceState.getInt(Constant.KEY_INDEX_TEST, Constant.INDEX_TEST);
    }
}
