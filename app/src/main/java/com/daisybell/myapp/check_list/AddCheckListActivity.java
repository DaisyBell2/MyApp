package com.daisybell.myapp.check_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class AddCheckListActivity extends AppCompatActivity {

    private static String TAG = "getQueTest";

    Handler handler = new Handler();

//    private LinearLayout linearLayoutPar;
    private RadioGroup rgPickPoint;
    private RadioButton rbBefore;
    private EditText etNameCheckList;
    private EditText etPar1, etPar2, etPar3, etPar4, etPar5;
    private DatabaseReference mDataBase;
//    private EditText etPar = null;

    int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
    int matchContent = LinearLayout.LayoutParams.MATCH_PARENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_check_list);

        init();
    }

    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

//        linearLayoutPar = findViewById(R.id.linearLayoutPar);
        etNameCheckList = findViewById(R.id.etNameCheckList);
        etPar1 = findViewById(R.id.etPar1);
        etPar2 = findViewById(R.id.etPar2);
        etPar3 = findViewById(R.id.etPar3);
        etPar4 = findViewById(R.id.etPar4);
        etPar5 = findViewById(R.id.etPar5);
        rgPickPoint = findViewById(R.id.rgPickPoint);
        rbBefore = findViewById(R.id.rbBefore);
        rbBefore.setChecked(true);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.CHECK_LIST_KEY);
    }
        // Создаем новый EditText по нажатию на кнопку "Добавить пункт"
//    public void onClickAddPar(View view) {
//        int parId = 1;
//        LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(matchContent, wrapContent);
//        etPar = new EditText(this);
//        etPar.setHint(R.string.par);
//        etPar.setId(parId);
//        linearLayoutPar.addView(etPar, lParam);
//        parId++;
//        for (int i = 1; i <= parId; i++) {
//            Log.d(TAG, "id " + etPar.getId());
//            Log.d(TAG, "text " + etPar.getText().toString());
//        }
//    }

    // Обработчик кнопки "Сохранить" (Сохраняем данные в Firebase)
    public void onClickSaveCheckList(View view) {
        int checkedRadioButtonId = rgPickPoint.getCheckedRadioButtonId();
        RadioButton myRadioButton = findViewById(checkedRadioButtonId);

        int id = Constant.INDEX_CHECK_LIST_ID;
        String nameCheckList = etNameCheckList.getText().toString().trim();
        String beforeAfter = String.valueOf(myRadioButton.getText());
        String par1 = etPar1.getText().toString().trim();
        String par2 = etPar2.getText().toString().trim();
        String par3 = etPar3.getText().toString().trim();
        String par4 = etPar4.getText().toString().trim();
        String par5 = etPar5.getText().toString().trim();
        CheckList newCheckList = new CheckList(id, nameCheckList, beforeAfter, par1, par2, par3, par4, par5);
        if (!TextUtils.isEmpty(nameCheckList) && !TextUtils.isEmpty(par1) && !TextUtils.isEmpty(par2)
                && !TextUtils.isEmpty(par3) && !TextUtils.isEmpty(par4) && !TextUtils.isEmpty(par5)) {
            mDataBase.child(nameCheckList).child(beforeAfter).setValue(newCheckList);
            Toast.makeText(this, R.string.save_text, Toast.LENGTH_SHORT).show();
            Constant.INDEX_CHECK_LIST_ID++;
            rbBefore.setChecked(true);
            etPar1.getText().clear();
            etPar2.getText().clear();
            etPar3.getText().clear();
            etPar4.getText().clear();
            etPar5.getText().clear();
        } else {
            Toast.makeText(this, R.string.empty_text, Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickLookCheckList(View view) {
        startActivity(new Intent(AddCheckListActivity.this, CheckListNameActivity.class));

        // Очещаем все поля через 0.2 сек
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            etNameCheckList.getText().clear();
            rbBefore.setChecked(true);
            etPar1.getText().clear();
            etPar2.getText().clear();
            etPar3.getText().clear();
            etPar4.getText().clear();
            etPar5.getText().clear();
            }
        }, 200);
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
                startActivity(new Intent(AddCheckListActivity.this, UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(AddCheckListActivity.this, SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(AddCheckListActivity.this, AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AddCheckListActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
