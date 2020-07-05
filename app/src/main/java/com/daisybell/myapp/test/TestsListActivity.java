package com.daisybell.myapp.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.LoadingDialog;
import com.daisybell.myapp.R;
import com.daisybell.myapp.check_list.CheckListNameActivity;
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
import java.util.Map;
import java.util.TreeMap;

public class TestsListActivity extends AppCompatActivity {

    private static String TAG = "myLog";

    private DatabaseReference mDataBase;
    private ArrayAdapter<String> mAdapter;
    private List<String> ListNameTest;
    private List<Test> ListTest;
    private ListView lvTests;
    private String key;
    private Map<String, Test> mMapTest = new TreeMap<>();
    Test test;
    String nameTest;

    LoadingDialog loadingDialog;
    private TextView tvNotData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_list);

        init();
        getDataFromDB();
        setOnClickItem();
        if (Constant.EMAIL_VERIFIED) {
            longDeleteClick();
        }

        loadingDialog = new LoadingDialog(TestsListActivity.this);
        loadingDialog.startLoadingDialog();

    }
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        Constant.INDEX_TEST = preferences.getInt(Constant.KEY_INDEX_TEST, 0);
        Constant.INDEX_ID = preferences.getInt(Constant.KEY_INDEX_ID, 0);
        Constant.INDEX_QUEST = preferences.getInt(Constant.KEY_INDEX_QUEST, 0);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");
//        key = preferences.getString("key", "");

        lvTests = findViewById(R.id.lvTests);
        ListNameTest = new ArrayList<>();
        ListTest = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ListNameTest);
        lvTests.setAdapter(mAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.TESTS_KEY);

        tvNotData = findViewById(R.id.tvNotData);
    }

   // Получаем данные с firebase
    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (ListNameTest.size() > 0)ListNameTest.clear();
                if (ListTest.size() > 0)ListTest.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Test test = ds.getChildren().iterator().next().getValue(Test.class);
                        assert test != null;
                        ListNameTest.add(test.nameTest);
                        ListTest.add(test);
                }
                mAdapter.notifyDataSetChanged();
                goneText();
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG ,"Failed to read value.", databaseError.toException());
            }
        };
        mDataBase.addValueEventListener(vListener);
    }
    private void setOnClickItem() {
        lvTests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Test test = ListTest.get(position);
                Intent intent = new Intent(TestsListActivity.this, QuizTestActivity.class);
                intent.putExtra(Constant.TEST_NAME, test.nameTest);
                intent.putExtra(Constant.TEST_QUESTION, test.question);
                intent.putExtra(Constant.TEST_OPTION1, test.option1);
                intent.putExtra(Constant.TEST_OPTION2, test.option2);
                intent.putExtra(Constant.TEST_OPTION3, test.option3);
                intent.putExtra(Constant.TEST_OPTION4, test.option4);
                intent.putExtra(Constant.TEST_RIGHT_ANSWER, test.rightAnswer);
                intent.putExtra(Constant.POSITION, position);
                startActivity(intent);
            }
        });
    }
    //Проверка данных, если их нет вывести текст
    private void goneText() {

        if (ListTest.size() == 0) {
            tvNotData.setVisibility(View.VISIBLE);
        } else if (ListTest.size() > 1) {
            tvNotData.setVisibility(View.GONE);
        }
    }

    // Метод для удаления данных при долгом нажатии
    private void longDeleteClick() {
        lvTests.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(TestsListActivity.this)
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setTitle("Удаление данных")
                        .setMessage("Вы уверены, что хотите удалить: \"" + ListNameTest.get(position) + "\" ?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDataBase.child(ListNameTest.get(position)).removeValue();
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(TestsListActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .show();

                return true;
            }
        });
    }

}
