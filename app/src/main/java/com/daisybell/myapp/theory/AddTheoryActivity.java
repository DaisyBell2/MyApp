package com.daisybell.myapp.theory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTheoryActivity extends AppCompatActivity {

    Handler handler = new Handler();

    private EditText mEtTitle, mEtText;
    private DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_theory);
        setTitle("Создание теории");

        init();
    }
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        mEtTitle = findViewById(R.id.etTitle);
        mEtText = findViewById(R.id.etText);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.THEORY_KEY);
    }
    private void showToast(int string) {
        Toast.makeText(AddTheoryActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    // Обработчик кнопки "Сохранить". Сохраняет в firebase, то что ввел пользователь
    public void onClickSaveTheory(View view) {
        String id = mDataBase.getKey();
        String title = mEtTitle.getText().toString().trim();
        String text = mEtText.getText().toString().trim();
        Theory newTheory = new Theory(id, title, text);
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(text)) {
            mDataBase.child(title).setValue(newTheory);
            showToast(R.string.save_text);
            mEtTitle.getText().clear();
            mEtText.getText().clear();
        } else {
            showToast(R.string.empty);
        }
    }

    // Обработчик кнопки "Перейти,посмотреть". Переходит на другое активити
    public void onClickReadTheory(View view) {
        Intent intent = new Intent(AddTheoryActivity.this, TheoryListActivity.class);
        startActivity(intent);

        // Очещаем все поля через 0.2 сек
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mEtTitle.getText().clear();
                mEtText.getText().clear();
            }
        }, 200);
    }
}
