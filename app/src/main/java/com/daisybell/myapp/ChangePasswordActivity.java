package com.daisybell.myapp;

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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.auth.RegActivity;
import com.daisybell.myapp.auth.User;
import com.daisybell.myapp.test.Test;
import com.daisybell.myapp.user_email.AddNewUserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ChangePasswordActivity extends AppCompatActivity {

    private static String TAG = "myLog";

    private TextInputLayout etYourPassword, etNewPassword, etConfirmPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDataBase;
    private DatabaseReference mDataBaseUser;
    private String sEmail, sPassword;

    private ProgressDialog mProgressDialog;
    private String userId;
    private String passwordUser;
    private String emailUser;
    private String nameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init();

        
        if (Constant.EMAIL_VERIFIED) {
            getPasswordDBAdmin();
        } else {
            getPasswordDBUser();
        }
    }
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        sEmail = "handbook.OTandTB@gmail.com";
        sPassword = "tBr!5(8G";

        etYourPassword = findViewById(R.id.etYourPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.ADMIN_DATE);
        mDataBaseUser = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.USER_KEY).child(userId);
    }

    public void onClickConfirmPassword(View view) {
        String yourPassword = etYourPassword.getEditText().getText().toString().trim();
        final String newPassword = etNewPassword.getEditText().getText().toString().trim();
        String confirmPassword = etConfirmPassword.getEditText().getText().toString().trim();

        if (!TextUtils.isEmpty(yourPassword) && !TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(confirmPassword)) { // Проверяем на пустые поля
            if (yourPassword.equals(passwordUser)) { // Сверяем введеный пароль с существующим
                if (newPassword.equals(confirmPassword)) { // Сверяем два новых пароля

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {

                        nameUser = user.getDisplayName();

//                        mProgressDialog = ProgressDialog.show(ChangePasswordActivity.this
//                                , "Пожалуйста подождите", "Идет загрузка...", true, false);
                        mProgressDialog = ProgressDialog.show(ChangePasswordActivity.this
                                ,"Пожалуйста подождите", "Данные обрабатываются...", true, false);

                        user.updatePassword(newPassword) // Обновляем пароль пользователя
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) { // Успешно

                                            if (Constant.EMAIL_VERIFIED) { // Обновляем пароли в базе данных
                                                mDataBase.child("password").setValue(newPassword);
                                            } else {
                                                mDataBaseUser.child("password").setValue(newPassword);
                                            }

//                                            mProgressDialog.dismiss();
//
//                                            //Initialize alert dialog
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
//                                            builder.setCancelable(false);
//                                            builder.setTitle(Html.fromHtml("<font color='#0084D1'>Успешно</font>"));
//                                            builder.setMessage("Данные успешно обновлены!");
//                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                    // Закрываем activity и возвращаемся на главное окно
//                                                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
//                                                    startActivity(intent);
//                                                    finish();
//                                                }
//                                            });
//                                            //Show alert dialog
//                                            builder.show();

                                            // Отправка пользователю на указанную почту даных о смене пароля
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
                                                        InternetAddress.parse(emailUser));
                                                //Email subject
                                                message.setSubject("Смена пароля");
                                                //Email message
                                                message.setText("Здравствуйте "+ nameUser+
                                                        ", к вашему аккаунту: " +emailUser +", приложения \"Справочник по ОТ и ТБ\", "
                                                        + "был изменен пароль."
                                                        + "\n\nВаш новый пароль: "+ newPassword
                                                        + ".\n\nЕсли это были не вы, авторизуйтесь с данным паролем и вновь поменяйте его!"
                                                        + "\n\n\nС уважением, \nадминистрация приложения.");

                                                //Send email
                                                new SendMail().execute(message);
                                            } catch (MessagingException e) {
                                                e.printStackTrace();
                                            }


                                        } else { // Не успешно
                                            mProgressDialog.dismiss();
                                            Toast.makeText(ChangePasswordActivity.this, "Ошибка, попробуйте позже", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                } else {
                    Toast.makeText(this, R.string.various_password, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Вы вели не действительный пароль", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.empty_text, Toast.LENGTH_SHORT).show();
        }
    }

    // Получение пароля admin'a
    private void getPasswordDBAdmin() {
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    passwordUser = user.password;
                    Log.d(TAG, "passwordUser: " + passwordUser);
                    emailUser = user.email;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG ,"Failed to read value: ", error.toException());
            }
        });
    }
    // Получение пароля user'a
    private void getPasswordDBUser() {
        mDataBaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    passwordUser = user.password;
                    Log.d(TAG, "passwordUser: " + passwordUser);
                    emailUser = user.email;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG ,"Failed to read value: ", error.toException());
            }
        });
    }

    // Письмо на почту пользователя
    private class SendMail extends AsyncTask<Message, String, String> {
        // Initialize progress dialog
//        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Create and show progress dialog
//            mProgressDialog = ProgressDialog.show(ChangePasswordActivity.this
//                    ,"Пожалуйста подождите", "Данные обрабатываются...", true, false);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color='#0084D1'>Успешно</font>"));
                builder.setMessage("Данные успешно обновленны!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // clear editText'ы
                        etYourPassword.getEditText().setText("");
                        etNewPassword.getEditText().setText("");
                        etConfirmPassword.getEditText().setText("");
                        // Закрываем activity и возвращаемся на главное окно
                        Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                //Show alert dialog
                builder.show();
            }else {
                //When error
                Toast.makeText(getApplicationContext()
                        , "Ошибка, попробуйте позже!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}