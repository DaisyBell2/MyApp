package com.daisybell.myapp.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daisybell.myapp.ChangePasswordActivity;
import com.daisybell.myapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onClickChangePassword(View view) {
        startActivity(new Intent(SettingsActivity.this, ChangePasswordActivity.class));
        finish();
    }
}