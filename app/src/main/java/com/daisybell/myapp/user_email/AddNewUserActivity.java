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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.auth.User;
import com.daisybell.myapp.menu.AboutApplicationActivity;
import com.daisybell.myapp.menu.SettingsActivity;
import com.daisybell.myapp.menu.UsersActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AddNewUserActivity extends AppCompatActivity {

    private static String TAG = "myLog";

    private EditText etName, etSurname, etEmail, etPassword;
    private Button btSingUp;
    private FirebaseAuth mAuth1;
    private FirebaseAuth mAuth2;
    private DatabaseReference mDataBase;
    String sEmail, sPassword;

    private FirebaseApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        init();


        boolean hasBeenInitialized = false;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(this);
        for (FirebaseApp app : firebaseApps) {
            if (app.getName().equals("HandbookOT&TB")) {
                hasBeenInitialized = true;
            }
        }
        mAuth1 = FirebaseAuth.getInstance();
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("[https://handbook-ot-and-tb.firebaseio.com/]")
                .setApiKey("AIzaSyAvgRn_TdUiWXYa8mjLaSAyJV6YRc0xrUY")
                .setApplicationId("handbook-ot-and-tb").build();

        if (!hasBeenInitialized) {
            myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions,
                    "HandbookOT&TB");
        } else {
            myApp = FirebaseApp.getInstance("HandbookOT&TB");
        }

        mAuth2 = FirebaseAuth.getInstance(myApp);


    }

    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        sEmail = "handbook.OTandTB@gmail.com";
        sPassword = "tBr!5(8G";

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btSingUp = findViewById(R.id.btSingUp);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.USER_KEY);
    }

    // Обработчик кнопки "Зарегестрироваться" (сохраняет введеные пользователем данные)
    public void onClickSaveSingUp(View view) {
//        int id = Constant.USER_ID;
        final String name = etName.getText().toString().trim();
        final String surname = etSurname.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final Boolean admin = false;
//        String passwordAgain = etPasswordAgain.getText().toString().trim();
//        final User newUser = new User(name, surname, email, password);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname) && !TextUtils.isEmpty(email) // Проверяем на пустые поля
                && !TextUtils.isEmpty(password)) {
                mAuth2.createUserWithEmailAndPassword(email, password) // Создаем аккаунт
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) { // Проверяет, все ли успешно

                                    Toast.makeText(AddNewUserActivity.this,
                                            "Данные введены некорректно либо почта уже существует!", Toast.LENGTH_SHORT).show();

                                } else {
                                    // Метод для указания имени пользователя
                                    FirebaseUser user = mAuth2.getCurrentUser();
                                    if (user != null) {
                                        // Указываем имя пользователю
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
                                    }

                                    String authUserId = mAuth2.getUid();
                                    Constant.USER_ID = authUserId;
                                    String fullName = name + " " + surname;
                                    assert authUserId != null;
                                    String id = authUserId;
                                    final User newUser = new User(id, name, surname, email, password, admin);
                                    mDataBase.child(fullName).setValue(newUser);
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


                                    mAuth2.signOut();
//                                    Toast.makeText(AddNewUserActivity.this, "Пользователь создан!", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(AddNewUserActivity.this, MainActivity.class)); // Переходит на другое окно
//                                    finish();
                                }

                            }
                        });

        } else {
            Toast.makeText(this, R.string.empty_text, Toast.LENGTH_SHORT).show();
        }
    }

    // Кнопка для заполнения рандомным паролем поле
    public void onClickGeneratePassword(View view) {
        final int min = 7;
        final int max = 9;
        int rnd = rnd(min, max);

        etPassword.setText(generateString(rnd));
    }
    public static int rnd(int min, int max) {
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }
    // Рандомное генерирование пароля
    private String generateString(int length) {
        char[] chars = "QWERTYUIOPASDFGHJKLZXCVBNMmnbvcxzlkjhgfdsapoiuytrewq1234567890!@#$%^&*()".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
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
                builder.setTitle(Html.fromHtml("<font color='#0084D1'>Успешно</font>"));
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
                    }
                });
                //Show alert dialog
                builder.show();
            }else {
                //When error
                Toast.makeText(getApplicationContext()
                        , "Данные введены некорректно либо почта уже существует!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Метод для отображения 3х точек на toolbar'e
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem search_item = menu.findItem(R.id.search_view);
        MenuItem users_item = menu.findItem(R.id.users);
        search_item.setVisible(false);
        if (!Constant.EMAIL_VERIFIED) {
            users_item.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.users: // Пользователи
                startActivity(new Intent(AddNewUserActivity.this, UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(AddNewUserActivity.this, SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(AddNewUserActivity.this, AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AddNewUserActivity.this, LoginActivity.class));
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}