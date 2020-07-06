package com.daisybell.myapp.theory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.menu.AboutApplicationActivity;
import com.daisybell.myapp.menu.SettingsActivity;
import com.daisybell.myapp.menu.UsersActivity;
import com.google.firebase.auth.FirebaseAuth;
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

    // Метод для отображения 3х точек на toolbar'e
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.support_menu, menu);
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
            case R.id.support_item:
                new AlertDialog.Builder(AddTheoryActivity.this)
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setTitle(R.string.support_title)
                        .setMessage(R.string.support_message)
                        .setPositiveButton("Понятно", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return true;
            case R.id.users: // Пользователи
                startActivity(new Intent(AddTheoryActivity.this, UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(AddTheoryActivity.this, SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(AddTheoryActivity.this, AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AddTheoryActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
