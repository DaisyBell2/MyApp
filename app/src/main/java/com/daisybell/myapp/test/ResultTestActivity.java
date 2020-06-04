package com.daisybell.myapp.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.auth.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResultTestActivity extends AppCompatActivity {

    private static double needsPoints = 75.0; // Сколько нужно баллов для прохода
    private static String TAG = "ResultTests";

    private static long back_pressed;
    private TextView tvResultTest2, tvCongratulation;
    private double resultTest;
    private double countOfAnswer;
    private double posNumQuest;

    private FirebaseAuth mAuth;
    private DatabaseReference mDBUser;
    private DatabaseReference mDataBase;

    Handler handler = new Handler();

    private String idUser;
    private String result;
    private String fullNameUser;
    private String startTimeTest;
    private String finishTimeTest;
    private String nameTest;
    private String dateTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_test);
        setTitle("Результат теста");

        init();
        getFullNameUser();
        getIntentMain();
        getDateAndTime();
        getResult();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() { // Сохраняем данные через 1сек
                setDataInDB();
            }
        }, 1000);

    }

    // Метод для проверки хочет ли пользователь выйти
//    @Override
//    public void onBackPressed() {
//        if (back_pressed + 2000 > System.currentTimeMillis()) {
//            super.onBackPressed();
//        } else {
//            Toast.makeText(getBaseContext(), "Нажмите ещё раз для выхода!", Toast.LENGTH_SHORT).show();
//        }
//        back_pressed = System.currentTimeMillis();
//    }
    private void init() { // Инициализируем переменые
        tvResultTest2 = findViewById(R.id.tvResultTest2);
        tvCongratulation = findViewById(R.id.tvCongratulation);

        mAuth = FirebaseAuth.getInstance();
        mDBUser = FirebaseDatabase.getInstance().getReference(Constant.USER_KEY);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.RESULT_TESTS);

        Intent intent = getIntent();
        countOfAnswer = intent.getIntExtra(Constant.RESULT_TEST, 0);
        posNumQuest = intent.getIntExtra(Constant.NUM_QUEST_TEST, 0);
        Log.d(TAG, "countOfAnswer " + countOfAnswer);
        Log.d(TAG, "posNumQuest " + posNumQuest);
    }

    private void getIntentMain() { // Получаем данные с прошлой активити
        Intent intent = getIntent();
        if (intent != null) {
            nameTest = intent.getStringExtra(Constant.TEST_NAME);
            startTimeTest = intent.getStringExtra(Constant.START_TIME_TEST);
        }
    }

    private void getDateAndTime() {
        // Текущее время
        Date currentDate = new Date();
        // Форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        dateTest = dateFormat.format(currentDate);
        // Форматирование времени как "часы:минуты:секунды"
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        finishTimeTest = timeFormat.format(currentDate);
    }

    private void getResult() { // Вычисляем количество правильных вопросов в процентах
        double t = (countOfAnswer / posNumQuest) * 100;
        resultTest = Math.round(t);
        Log.d(TAG, "resultTest " + resultTest);

        result = resultTest + "%";
        tvResultTest2.setText(result); // Присваеваем полученый результат в TextView
        if (resultTest >= needsPoints) { // Проверяем больше ли пользователь набрал от допустимого
            tvResultTest2.setTextColor(getResources().getColor(R.color.colorGreen)); // Если да - зеленный
            tvCongratulation.setText(R.string.test_congratulation);
            tvCongratulation.setTextColor(getResources().getColor(R.color.colorDarkGreen));
        } else {
            tvResultTest2.setTextColor(getResources().getColor(R.color.colorRed)); // Нет - красный
            tvCongratulation.setText(R.string.test_not_congratulation);
            tvCongratulation.setTextColor(getResources().getColor(R.color.colorDarkRed));
        }
    }

    private void getFullNameUser() {

        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idUser = mAuth.getUid();
                Log.d(TAG, "idUser: " + idUser);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    if (idUser != null && idUser.equals(key)) {
                        User user = ds.getValue(User.class);
                        if (user != null) {
                            fullNameUser = (user.name + " " + user.surname);
                            Log.d(TAG, "fullNameUser: " + fullNameUser);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDBUser.addValueEventListener(vListener);
    }

    private void setDataInDB() {
        SaveResultTests resultTests = new SaveResultTests(fullNameUser, nameTest, result, dateTest, startTimeTest, finishTimeTest);
        mDataBase.push().setValue(resultTests);
        Log.d(TAG, "Данные успешно добавленны!");
    }


    // Обработчик кнопки "Домой", возвращает на главную страницу
    public void onClickHome(View view) {
        startActivity(new Intent(ResultTestActivity.this, MainActivity.class));
        finish();
    }
}
