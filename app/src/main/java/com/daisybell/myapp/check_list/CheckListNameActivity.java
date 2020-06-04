package com.daisybell.myapp.check_list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.LoadingDialog;
import com.daisybell.myapp.R;
import com.daisybell.myapp.theory.Theory;
import com.daisybell.myapp.theory.TheoryListActivity;
import com.daisybell.myapp.theory.TheoryShowActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckListNameActivity extends AppCompatActivity {

    private static String TAG = "myLog";

    private ListView lvCheckListName;
    private ArrayAdapter<String> mAdapter;
    private List<String> mListNameCheckList;
    private List<CheckList> mListCheckList;
    private DatabaseReference mDataBase;

    LoadingDialog loadingDialog;
    private TextView tvNotData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_name);
        setTitle("Чек-лист");

        init();
        getDataFromDB();
        onClickItem();


        loadingDialog = new LoadingDialog(CheckListNameActivity.this);
        loadingDialog.startLoadingDialog();

    }

    // Инициализация переменных
    private void init() {
        lvCheckListName = findViewById(R.id.lvCheckListName);
        mListNameCheckList = new ArrayList<>();
        mListCheckList = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListNameCheckList);
        lvCheckListName.setAdapter(mAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.CHECK_LIST_KEY);

        tvNotData = findViewById(R.id.tvNotData);
    }

    // Получение данных с Firebase
    private void getDataFromDB() {

        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mListNameCheckList.size() > 0) mListNameCheckList.clear();
                if (mListCheckList.size() > 0) mListCheckList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    CheckList checkList = ds.getChildren().iterator().next().getValue(CheckList.class);
                    assert checkList != null;
                    mListNameCheckList.add(checkList.nameCheckList);
                    mListCheckList.add(checkList);
                }
                mAdapter.notifyDataSetChanged();
                goneText();
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBase.addValueEventListener(vListener);
    }

//     Обработчик нажатия на list с получения данных от position
    private void onClickItem() {
        lvCheckListName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckList checkList = mListCheckList.get(position);
                Intent intent = new Intent(CheckListNameActivity.this, CheckListBeforeAfterActivity.class);
                intent.putExtra(Constant.POSITION, position);
                startActivity(intent);
            }
        });
    }
    //Проверка данных, если их нет вывести текст
    private void goneText() {

        if (mListCheckList.size() == 0) {
            tvNotData.setVisibility(View.VISIBLE);
        } else if (mListCheckList.size() > 1) {
            tvNotData.setVisibility(View.GONE);
        }
    }

}
