package com.daisybell.myapp.user_email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.auth.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AddNewUserActivity extends AppCompatActivity {

    private EditText etName, etSurname, etEmail, etPassword, etPasswordAgain;
    private Button btSingUp;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;
    String sEmail, sPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);
        setTitle("Добавление нового пользователя");

        init();
    }

    private void init() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        Constant.USER_ID = preferences.getInt(Constant.INDEX_USER_ID, 0);

        sEmail = "handbook.OTandTB@gmail.com";
        sPassword = "tBr!5(8G";

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPasswordAgain = findViewById(R.id.etPasswordAgain);
        btSingUp = findViewById(R.id.btSingUp);
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID);
    }

    // Обработчик кнопки "Зарегестрироваться" (сохраняет введеные пользователем данные)
    public void onClickSaveSingUp(View view) {
//        int id = Constant.USER_ID;
        final String name = etName.getText().toString().trim();
        final String surname = etSurname.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final Boolean admin = false;
        String passwordAgain = etPasswordAgain.getText().toString().trim();
//        final User newUser = new User(name, surname, email, password);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname) && !TextUtils.isEmpty(email) // Проверяем на пустые поля
                && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordAgain)) {
            if (password.equals(passwordAgain)) { // Сверяем пароли
                mAuth.createUserWithEmailAndPassword(email, password) // Создаем аккаунт
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) { // Проверяет, все ли успешно

                                    String authUserId = mAuth.getUid();
                                    Constant.USER_ID = authUserId;
                                    assert authUserId != null;
                                    String id = authUserId;
                                    final User newUser = new User(id, name, surname, email, password, admin);
                                    mDataBase.child(Constant.USER_KEY).child(authUserId).setValue(newUser);
//                                    Constant.USER_ID++;

//                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                                    preferences.edit().putInt(Constant.INDEX_USER_ID, Constant.USER_ID).apply();

                                    // Отправка пользователю на указанную почту логина и пароля
                                    // Initialize properties
                                    Properties properties = new Properties();
                                    properties.put("mail.smtp.auth", "true");
                                    properties.put("mail.smtp.starttls.enable", "true");
                                    properties.put("mail.smtp.host", "smtp.gmail.com");
                                    properties.put("mail.smtp.port", "587");

                                    // Initialize session
                                    Session session = Session.getInstance(properties, new Authenticator() {
                                        @Override
                                        protected PasswordAuthentication getPasswordAuthentication() {
                                            return new PasswordAuthentication(sEmail, sPassword);
                                        }
                                    });


                                    try {
                                        // Initialize email content
                                        Message message = new MimeMessage(session);
                                        // Sender email
                                        message.setFrom(new InternetAddress(sEmail));
                                        //Recipient email
                                        message.setRecipients(Message.RecipientType.TO,
                                                InternetAddress.parse(email));
                                        //Email subject
                                        message.setSubject("Данные для входа");
                                        //Email message
                                        message.setText("Здравствуйте "+ name+
                                                ", вы получили доступ для входа в приложение \"Справочник по ОТ и ТБ\"."
                                                + "\n\nВаши данные для авторизации:"
                                                + "\nЛогин: "+email+"\nПароль: "+ password
                                                + "\n\n\nС уважением, \nадминистрация приложения!");

                                        //Send email
                                        new SendMail().execute(message);
                                    } catch (MessagingException e) {
                                        e.printStackTrace();
                                    }


//                                    Toast.makeText(AddNewUserActivity.this, "Пользователь создан!", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(AddNewUserActivity.this, MainActivity.class)); // Переходит на другое окно
//                                    finish();
                                } else {
                                    Toast.makeText(AddNewUserActivity.this, "Данные введены некорректно либо почта уже существует!", Toast.LENGTH_SHORT).show();
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

    private class SendMail extends AsyncTask<Message, String, String> {
        // Initialize progress dialog
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Create and show progress dialog
            mProgressDialog = ProgressDialog.show(AddNewUserActivity.this
                    ,"Пожалуйста подождите", "Пользователь создается...", true, false);

        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                //When success
                Transport.send(messages[0]);
                return "Success";

            } catch (MessagingException e) {
                //When error
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Dismiss progress dialog
            mProgressDialog.dismiss();
            if (s.equals("Success")) {
                //When Success

                //Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNewUserActivity.this);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color='#509324'>Успешно</font>"));
                builder.setMessage("Пользователь успешно создан!\nНа указаную почту придет логин и пароль для входа.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //Clear all edit text
                        etName.setText("");
                        etSurname.setText("");
                        etEmail.setText("");
                        etPassword.setText("");
                        etPasswordAgain.setText("");
                    }
                });
                //Show alert dialog
                builder.show();
            }else {
                //When error
                Toast.makeText(getApplicationContext()
                        , "Данные введены некорректно либо почта уже существует!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}