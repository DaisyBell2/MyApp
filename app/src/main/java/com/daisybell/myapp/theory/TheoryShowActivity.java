package com.daisybell.myapp.theory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.R;

public class TheoryShowActivity extends AppCompatActivity {

    private TextView mTvTheoryShowTitle, mTvTheoryShowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory_show);
        setTitle("Теория");

        init();
        getIntentMain();
    }
    private void init() {
        mTvTheoryShowTitle = findViewById(R.id.tvTheoryShowTitle);
        mTvTheoryShowText = findViewById(R.id.tvTheoryShowText);
    }
    private void getIntentMain() {
        Intent intent = getIntent();
        if (intent != null) {
            mTvTheoryShowTitle.setText(intent.getStringExtra(Constant.THEORY_TITLE));
            mTvTheoryShowText.setText(intent.getStringExtra(Constant.THEORY_TEXT));
        }
    }
}
