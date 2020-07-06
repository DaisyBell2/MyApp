package com.daisybell.myapp.check_list;

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
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.menu.AboutApplicationActivity;
import com.daisybell.myapp.menu.SettingsActivity;
import com.daisybell.myapp.menu.UsersActivity;
import com.daisybell.myapp.theory.Theory;
import com.daisybell.myapp.theory.TheoryListActivity;
import com.daisybell.myapp.theory.TheoryShowActivity;
import com.google.firebase.auth.FirebaseAuth;
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

        init();
        getDataFromDB();
        onClickItem();
        if (Constant.EMAIL_VERIFIED) {
            longDeleteClick();
        }


        loadingDialog = new LoadingDialog(CheckListNameActivity.this);
        loadingDialog.startLoadingDialog();

    }

    // Инициализация переменных
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        lvCheckListName = findViewById(R.id.lvCheckListName);
        mListNameCheckList = new ArrayList<>();
        mListCheckList = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListNameCheckList);
        lvCheckListName.setAdapter(mAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.CHECK_LIST_KEY);

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
    // Метод для удаления данных при долгом нажатии
    private void longDeleteClick() {
        lvCheckListName.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(CheckListNameActivity.this)
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setTitle("Удаление данных")
                        .setMessage("Вы уверены, что хотите удалить: \"" + mListNameCheckList.get(position) + "\" ?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDataBase.child(mListNameCheckList.get(position)).removeValue();
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(CheckListNameActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Нет", null)
                        .show();

                return true;
            }
        });
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
                startActivity(new Intent(CheckListNameActivity.this, UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(CheckListNameActivity.this, SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(CheckListNameActivity.this, AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CheckListNameActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
