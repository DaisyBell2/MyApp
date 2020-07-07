package com.daisybell.myapp.theory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTheoryLetterFragment extends Fragment {

    private static final String TAG = "myLog";

    Handler handler = new Handler();

    private EditText mEtTitle, mEtText;
    private Button mBtnSaveTheory, mBtnReadTheory;
    private DatabaseReference mDataBase;

    public AddTheoryLetterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        View v = inflater.inflate(R.layout.fragment_add_theory_letter, container, false);
        mEtTitle = v.findViewById(R.id.etTitle);
        mEtText = v.findViewById(R.id.etText);
        mBtnSaveTheory = v.findViewById(R.id.btSaveTheory);
        mBtnReadTheory = v.findViewById(R.id.btReadTheory);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.THEORY_KEY);

        // Обработчик кнопки "Сохранить". Сохраняет в firebase, то что ввел пользователь
        mBtnSaveTheory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        // Обработчик кнопки "Перейти,посмотреть". Переходит на другое активити
        mBtnReadTheory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TheoryListActivity.class);
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
        });
        return  v;
    }

    private void showToast(int string) {
        Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
    }
}