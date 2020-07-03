package com.daisybell.myapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.user_email.AddNewUserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegActivity extends AppCompatActivity {

    private static String TAG = "myLog";

    private EditText etName, etSurname, etEmail;
    private TextInputLayout etPassword, etPasswordAgain;
    private Button btSingUp;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;
    private Boolean successReg = false;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        setTitle("Регистрация");

        init();
    }
    private void init() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        Constant.USER_INDEX_ID = preferences.getInt(Constant.INDEX_USER_ID, 0);

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordAgain = findViewById(R.id.etPasswordAgain);
        btSingUp = findViewById(R.id.btSingUp);
        mAuth = FirebaseAuth.getInstance();
//        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY + mAuth.getUid());
    }

    // Обработчик кнопки "Зарегестрироваться" (сохраняет введеные пользователем данные)
    public void onClickSaveSingUp(View view) {
//        int id = Constant.USER_ID;
        final String name = etName.getText().toString().trim();
        final String surname = etSurname.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getEditText().getText().toString().trim();
        final Boolean admin = true;
        String passwordAgain = etPasswordAgain.getEditText().getText().toString().trim();
//        final User newUser = new User(name, surname, email, password);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname) && !TextUtils.isEmpty(email) // Проверяем на пустые поля
                && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordAgain)) {
            if (password.equals(passwordAgain)) { // Сверяем пароли
                // Выведем окно закрузки
                mProgressDialog = ProgressDialog.show(RegActivity.this
                        ,"Пожалуйста подождите", "Ваш аккаунт создается...", true, false);

                mAuth.createUserWithEmailAndPassword(email, password) // Создаем аккаунт
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) { // Проверяет, все ли успешно

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // Метод для указания имени пользователя
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name).build();
                                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User profile updated.");
                                                }
                                            }
                                        });
                                        // Метод для отправки письма на указаную почту(Верефикация почты)
                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    String authUserId = mAuth.getUid();
                                                    Constant.ADMIN_ID = authUserId;
                                                    String id = authUserId;
                                                    mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ mAuth.getUid());
                                                    assert authUserId != null;
                                                    final User newUser = new User(id, name, surname, email, password,admin);
                                                    mDataBase.child(Constant.ADMIN_DATE).setValue(newUser);

                                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                    preferences.edit().putString(Constant.ADMIN_ID_INDEX, Constant.ADMIN_ID).apply();

//                                                    Toast.makeText(getApplicationContext(), R.string.verification_email, Toast.LENGTH_LONG).show();

                                                    successReg = true;

                                                    etEmail.setText("");
                                                    etName.setText("");
                                                    etPassword.getEditText().setText("");
                                                    etPasswordAgain.getEditText().setText("");
                                                    etSurname.setText("");
                                                    btSingUp.setEnabled(true);

                                                    // Закрываем прогресс бар
                                                    mProgressDialog.dismiss();

                                                    //Initialize alert dialog
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegActivity.this);
                                                    builder.setCancelable(false);
                                                    builder.setTitle(Html.fromHtml("<font color='#0084D1'>Успешно</font>"));
                                                    builder.setMessage(R.string.verification_email);
                                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            // Закрываем activity и возвращаемся на окно авторизации
                                                            Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                                                            intent.putExtra("email", email);
                                                            intent.putExtra("password", password);
                                                            intent.putExtra("success", successReg);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                    //Show alert dialog
                                                    builder.show();


                                                } else {
                                                    Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
//                                    Toast.makeText(RegActivity.this, "Успешно!", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(RegActivity.this, MainActivity.class)); // Переходит на другое окно
//                                    finish();
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

    // Метод для отправки письма на указаную почту(Верефикация почты)
//    private void sendEmailVer() {
//        FirebaseUser user = mAuth.getCurrentUser();
//
//        if (user != null) {
//            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(getApplicationContext(), R.string.verification_email, Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }
}
