package com.daisybell.myapp.theory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.daisybell.myapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTheoryActivity extends AppCompatActivity {

    private EditText mEtTitle, mEtText;
    private DatabaseReference mDataBase;
    private String THEORY_KEY = "Theory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_theory);

        init();
    }
    private void init() {
        mEtTitle = findViewById(R.id.etTitle);
        mEtText = findViewById(R.id.etText);
        mDataBase = FirebaseDatabase.getInstance().getReference(THEORY_KEY);
    }
    private void showToast(int string) {
        Toast.makeText(AddTheoryActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    public void onClickSaveTheory(View view) {
        String id = mDataBase.getKey();
        String title = mEtTitle.getText().toString().trim();
        String text = mEtText.getText().toString().trim();
        Theory newTheory = new Theory(id, title, text);
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(text)) {
            mDataBase.push().setValue(newTheory);
            showToast(R.string.save);
            mEtTitle.getText().clear();
            mEtText.getText().clear();
        } else {
            showToast(R.string.empty);
        }
    }

    public void onClickReadTheory(View view) {
        Intent intent = new Intent(AddTheoryActivity.this, TheoryListActivity.class);
        startActivity(intent);
        mEtTitle.getText().clear();
        mEtText.getText().clear();
    }
}
