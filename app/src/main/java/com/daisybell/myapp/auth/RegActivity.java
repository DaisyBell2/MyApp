package com.daisybell.myapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegActivity extends AppCompatActivity {

    private EditText etName, etSurname, etEmail, etPassword, etPasswordAgain;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        setTitle("Регистрация");

        init();
    }
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.USER_ID = preferences.getInt(Constant.INDEX_USER_ID, 0);

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordAgain = findViewById(R.id.etPasswordAgain);
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.USER_KEY);
    }

    // Обработчик кнопки "Зарегестрироваться" (сохраняет введеные пользователем данные)
    public void onClickSaveSingUp(View view) {
        int id = Constant.USER_ID;
        String name = etName.getText().toString().trim();
        String surname = etSurname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordAgain = etPasswordAgain.getText().toString().trim();
        final User newUser = new User(id, name, surname, email, password);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname) && !TextUtils.isEmpty(email) // Проверяем на пустые поля
                && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordAgain)) {
            if (password.equals(passwordAgain)) { // Сверяем пароли
                mAuth.createUserWithEmailAndPassword(email, password) // Создаем аккаунт
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) { // Проверяет, все ли успешно

                                    String authUserId = mAuth.getUid();
                                    assert authUserId != null;
                                    mDataBase.child(authUserId).setValue(newUser);
                                    Constant.USER_ID++;

                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    preferences.edit().putInt(Constant.INDEX_USER_ID, Constant.USER_ID).apply();

                                    Toast.makeText(RegActivity.this, "Успешно!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegActivity.this, MainActivity.class)); // Переходит на другое окно
                                    finish();
                                } else {
                                    Toast.makeText(RegActivity.this, "Данные введены некорректно либо почта уже существует!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            } else {
                Toast.makeText(this, R.string.various_password, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, R.string.empty_text, Toast.LENGTH_SHORT).show();
        }
    }
}
