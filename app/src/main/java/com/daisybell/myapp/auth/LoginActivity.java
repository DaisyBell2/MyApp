package com.daisybell.myapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.theory.Theory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static String TAG = "myLog";

    private EditText etEmail;
    private TextInputLayout etPassword;
    private FirebaseAuth mAuth;

//    private Boolean emailVerified;

    private Boolean successReg = false;
    private String email;
    private String password;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Авторизация");

        init();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser cUser = mAuth.getCurrentUser();
        if (cUser != null) {
            Constant.EMAIL_VERIFIED = cUser.isEmailVerified();
            name = cUser.getDisplayName();
            Log.d(TAG, "name: " + name);

            // Если это админ, запоминаем его Uid
            if (Constant.EMAIL_VERIFIED) {
                Constant.ADMIN_ID = mAuth.getUid();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                preferences.edit().putString(Constant.ADMIN_ID_INDEX, Constant.ADMIN_ID).apply();
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            preferences.edit().putBoolean(Constant.EMAIL_VERIFIED_INDEX, Constant.EMAIL_VERIFIED).apply();

            Intent intent = getIntent();
            if (intent != null) {
                email = intent.getStringExtra("email");
                password = intent.getStringExtra("password");
                successReg = intent.getBooleanExtra("success", false);
            }
            if (successReg){
                etEmail.setText(email);
                etPassword.getEditText().setText(password);
            } else {
                Toast.makeText(this, "Вы вошли как: " + cUser.getEmail(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                        .putExtra("emailVerified",  Constant.EMAIL_VERIFIED));
                finish();
            }

        } else {
//            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            Log.d(TAG,"User null");
        }
    }

    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        mAuth = FirebaseAuth.getInstance();
//        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.USER_KEY);
    }
    // Обработчик кнопки "Зарегестрироваться"
    public void onClickSingUp(View view) {
        startActivity(new Intent(LoginActivity.this, RegActivity.class));
    }

    // Обработчик кнопки "Войти"
    public void onClickSingIn(View view) {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getEditText().getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mAuth.signInWithEmailAndPassword(email, password) // Заходит в акаунт(если он есть)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { // Проверяет, все ли успешно

                                // Прооверяем верифицировал ли пользователь почту
                                FirebaseUser cUser = mAuth.getCurrentUser();
                                if (cUser != null) {
                                    Constant.EMAIL_VERIFIED = cUser.isEmailVerified();
                                    name = cUser.getDisplayName();
                                    Log.d(TAG, "name2: " + name);

                                    // Если это админ, запоминаем его Uid
                                    if  (Constant.EMAIL_VERIFIED) {
                                        Constant.ADMIN_ID = mAuth.getUid();

                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        preferences.edit().putString(Constant.ADMIN_ID_INDEX, Constant.ADMIN_ID).apply();
                                    }
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    preferences.edit().putBoolean(Constant.EMAIL_VERIFIED_INDEX, Constant.EMAIL_VERIFIED).apply();

                                    if (successReg) {
                                        if (cUser.isEmailVerified()) {
                                            Toast.makeText(LoginActivity.this, "Добро Пожаловать, "+name+"!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                                    .putExtra("emailVerified",  Constant.EMAIL_VERIFIED));
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Проверьте вашу почту для подтверждения Email адреса", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Добро Пожаловать, "+name+"!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                                .putExtra("emailVerified",  Constant.EMAIL_VERIFIED));
                                        finish();
                                    }

                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "Данного пользователя не существует!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, R.string.empty_text, Toast.LENGTH_SHORT).show();
        }
    }

}
