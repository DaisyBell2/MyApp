package com.daisybell.myapp.tests;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.daisybell.myapp.R;

public class QuizTestsActivity extends AppCompatActivity {

    private TextView mTextViewScore;
    private TextView mTextViewQuestion;
    private TextView mTextViewAnswer0;
    private TextView mTextViewAnswer1;
    private TextView mTextViewAnswer2;
    private TextView mTextViewAnswer3;

    private String question;
    private int rightAnswer;
    private int rightAnswerPosition;
    private int countOfQuestions = 0;
    private int countOfRightAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_tests);
    }
}
