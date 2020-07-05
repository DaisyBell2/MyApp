package com.daisybell.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

// Класс отвечающий за сброс пароля
public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText etEmailSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        init();
    }

    private void init() {
        etEmailSend = findViewById(R.id.etEmailSend);
    }

    public void onClickSendConfirmPassword(View view) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = etEmailSend.getText().toString().trim();

//        auth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(ForgetPasswordActivity.this, "Отправлено", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(ForgetPasswordActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
    }
}