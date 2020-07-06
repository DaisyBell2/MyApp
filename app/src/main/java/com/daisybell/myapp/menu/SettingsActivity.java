package com.daisybell.myapp.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import com.daisybell.myapp.ChangePasswordActivity;
import com.daisybell.myapp.Constant;
import com.daisybell.myapp.R;
import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.result.ResultCheckListNameActivity;
import com.daisybell.myapp.user_email.AddNewUserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mDataBase;
    private DatabaseReference mDataBaseUser;
    private FirebaseAuth mAuth;
    private String idUser;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();
    }
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getUid();

        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.ADMIN_DATE);
        mDataBaseUser = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID)
                .child(Constant.USER_KEY).child(idUser);
    }

    // Кнопка для смены пароля
    public void onClickChangePassword(View view) {
        startActivity(new Intent(SettingsActivity.this, ChangePasswordActivity.class));
        finish();
    }

    // Кнопка для удаления аккаунта
    public void onClickDeleteAccount(View view) {

            new AlertDialog.Builder(SettingsActivity.this)
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .setTitle("Удаление данных")
                    .setMessage("Вы уверены, что хотите удалить свой аккаунт?\nВсе ваши данные будут безвозвратно утеряны.")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                mProgressDialog = ProgressDialog.show(SettingsActivity.this
                                        ,"Пожалуйста подождите", "Аккаунт удаляется...", true, false);

                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (Constant.EMAIL_VERIFIED) {
                                                        mDataBase.removeValue();
                                                    } else {
                                                        mDataBaseUser.removeValue();
                                                    }

                                                    mProgressDialog.dismiss();

                                                    new AlertDialog.Builder(SettingsActivity.this)
                                                            .setCancelable(false)
                                                            .setTitle(Html.fromHtml("<font color='#0084D1'>Успешно</font>"))
                                                            .setMessage("Аккаунт успешно удален")
                                                            .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                }
                                                            }).show();
                                                } else {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Ошибка, попробуйте позже", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    })
                    .setNegativeButton("Нет", null)
                    .show();

    }

}