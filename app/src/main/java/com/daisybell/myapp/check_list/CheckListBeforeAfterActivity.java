package com.daisybell.myapp.check_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.LoadingDialog;
import com.daisybell.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckListBeforeAfterActivity extends AppCompatActivity {

    private String TAG = "getQueTest";

    private ListView lvBeforeAfter;
    private ArrayAdapter<String> mAdapter;
    private List<String> mListNameCheckList;
    private List<String> mListBeforeAfterCheckList;
    private List<CheckList> mListCheckList;
    private DatabaseReference mDataBase;

    private int testPosition = -1;;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_before_after);

        init();
        getDataFromDB();
        onClickItem();

//        if (Constant.EMAIL_VERIFIED) {
//            longDeleteClick();
//        }

    }

    // Инициализация переменных
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        lvBeforeAfter = findViewById(R.id.lvBeforeAfter);
        mListNameCheckList = new ArrayList<>();
        mListBeforeAfterCheckList = new ArrayList<>();
        mListCheckList = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListBeforeAfterCheckList);
        lvBeforeAfter.setAdapter(mAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.CHECK_LIST_KEY);

        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra(Constant.POSITION, 0);
        }
    }

    // Получение данных с Firebase
    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListNameCheckList.size() > 0) mListNameCheckList.clear();
                if (mListBeforeAfterCheckList.size() > 0) mListBeforeAfterCheckList.clear();
                if (mListCheckList.size() > 0) mListCheckList.clear();
                for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                    testPosition++;
                    for (DataSnapshot ds2 : ds1.getChildren()) {
                        if (position == testPosition) {
                        CheckList checkList = ds2.getValue(CheckList.class);
                            if (checkList != null) {
                                mListNameCheckList.add(checkList.nameCheckList);
                                mListBeforeAfterCheckList.add(checkList.beforeAfter);
                                mListCheckList.add(checkList);
                            }
                        Log.d(TAG, "mListBeforeAfterCheckList: " + mListBeforeAfterCheckList);
                        Log.d(TAG, "mListCheckList: " + mListCheckList);
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBase.addValueEventListener(vListener);
    }

    // Обработчик нажатия на list с получения данных от position
    private void onClickItem() {
        lvBeforeAfter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckList checkList = mListCheckList.get(position);
                Intent intent = new Intent(CheckListBeforeAfterActivity.this, CheckListShowActivity.class);
                intent.putExtra(Constant.CHECK_LIST_NAME, checkList.nameCheckList);
                intent.putExtra(Constant.CHECK_LIST_BEFORE_AFTER, checkList.beforeAfter);
                intent.putExtra(Constant.CHECK_LIST_PAR1, checkList.par1);
                intent.putExtra(Constant.CHECK_LIST_PAR2, checkList.par2);
                intent.putExtra(Constant.CHECK_LIST_PAR3, checkList.par3);
                intent.putExtra(Constant.CHECK_LIST_PAR4, checkList.par4);
                intent.putExtra(Constant.CHECK_LIST_PAR5, checkList.par5);
                startActivity(intent);
            }
        });
    }

    // Метод для удаления данных при долгом нажатии
//    private void longDeleteClick() {
//        lvBeforeAfter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//
//                new AlertDialog.Builder(CheckListBeforeAfterActivity.this)
//                        .setIcon(android.R.drawable.ic_menu_delete)
//                        .setTitle("Удаление данных")
//                        .setMessage("Вы уверены, что хотите удалить: \"" + mListBeforeAfterCheckList.get(position) + "\" ?")
//                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                mDataBase.child(mListNameCheckList.get(position)).child(mListBeforeAfterCheckList.get(position)).removeValue();
//                                mAdapter.notifyDataSetChanged();
//                            }
//                        })
//                        .setNegativeButton("Нет", null)
//                        .show();
//
//                return true;
//            }
//        });
//    }

}
