package com.daisybell.myapp.result;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.menu.AboutApplicationActivity;
import com.daisybell.myapp.menu.SettingsActivity;
import com.daisybell.myapp.menu.UsersActivity;
import com.daisybell.myapp.test.SaveResultTests;
import com.daisybell.myapp.theory.TheoryListActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResultTestsNameActivity extends AppCompatActivity {

    private static String TAG = "myLog";

    private ListView lvResultTestsName;
//    private ArrayAdapter<String> mAdapter;
    private List<String> mListFullName;
    private List<SaveResultTests> mListTests;
    private DatabaseReference mDataBase;
    private DatabaseReference mDataBaseUser;
    private String idUser;

    private FirebaseAuth mAuth;

    CustomAdapter mCustomAdapter;

    LoadingDialog loadingDialog;
    private TextView tvNotData;

    private String key1 = "";
    private String key2 = "";
    private boolean delete;

    private int itemIndex = -1;
    private boolean filter = false;
//    private ConstraintLayout constLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_tests_name);

        init();

        if (Constant.EMAIL_VERIFIED) {
            getDataFromDB();
        } else {
            getDataFromDBForUser();
        }
//        onClickItem();
        Log.d(TAG, "filter1: " + filter);

        loadingDialog = new LoadingDialog(ResultTestsNameActivity.this);
        loadingDialog.startLoadingDialog();

    }

    // Инициализация переменных
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        lvResultTestsName = findViewById(R.id.lvResultTestsName);
        mListFullName = new ArrayList<>();
        mListTests = new ArrayList<>();
        mCustomAdapter = new CustomAdapter(mListTests, this);
        lvResultTestsName.setAdapter(mCustomAdapter);

//        constLayout = findViewById(R.id.constLayoutTest);
//        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListFullName);
//        lvResultTestsName.setAdapter(mAdapter);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getUid();

        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.RESULT_TESTS);
        mDataBaseUser = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID)
                .child(Constant.USER_KEY).child(idUser).child(Constant.RESULT_TESTS);

        tvNotData = findViewById(R.id.tvNotData);
    }

    // Получение данных с Firebase
    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListFullName.size() > 0) mListFullName.clear();
                if (mListTests.size() > 0) mListTests.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SaveResultTests resultTests = ds.getChildren().iterator().next().getValue(SaveResultTests.class);
                    if (resultTests != null) {
                        mListFullName.add(resultTests.nameTest+"_"+resultTests.fullName);
                        mListTests.add(resultTests);
                    }
                }
//                mAdapter.notifyDataSetChanged();
                mCustomAdapter.notifyDataSetChanged();
                goneText();
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBase.addValueEventListener(vListener);
    }
    // Получение данных с Firebase для User'ов
    private void getDataFromDBForUser() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListFullName.size() > 0) mListFullName.clear();
                if (mListTests.size() > 0) mListTests.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SaveResultTests resultTests = ds.getChildren().iterator().next().getValue(SaveResultTests.class);
                    if (resultTests != null) {
                        mListFullName.add(resultTests.nameTest+"_"+resultTests.fullName);
                        mListTests.add(resultTests);
                    }
                }
//                mAdapter.notifyDataSetChanged();
                mCustomAdapter.notifyDataSetChanged();
                goneText();
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDataBaseUser.addValueEventListener(vListener);
    }
    //Проверка данных, если их нет вывести текст
    private void goneText() {

        if (mListTests.size() == 0) {
            tvNotData.setVisibility(View.VISIBLE);
        } else if (mListTests.size() > 1) {
            tvNotData.setVisibility(View.GONE);
        }
    }

    // Обработчик нажатия на list с получения данных от position
//    private void onClickItem() {
//        lvResultTestsName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                SaveResultTests resultTests = mListTests.get(position);
//                Intent intent = new Intent(ResultTestsNameActivity.this, ResultTestsActivity.class);
//                intent.putExtra(Constant.RESULT_TESTS_NAME, resultTests.fullName);
//                intent.putExtra(Constant.RESULT_TESTS_NAME_TEST, resultTests.nameTest);
//                intent.putExtra(Constant.RESULT_TESTS_R, resultTests.resultTest);
//                intent.putExtra(Constant.RESULT_TESTS_DATE, resultTests.dateTest);
//                intent.putExtra(Constant.RESULT_TESTS_START_TIME, resultTests.startTimeTest);
//                intent.putExtra(Constant.RESULT_TESTS_FINISH_TIME, resultTests.finishTimeTest);
//                startActivity(intent);
//            }
//        });
//    }

    // Делаем поиск в меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_view);
        MenuItem users_item = menu.findItem(R.id.users);
        if (!Constant.EMAIL_VERIFIED) {
            users_item.setVisible(false);
        }
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mCustomAdapter.getFilter().filter(newText);
                filter = true;
                Log.d(TAG, "filter2: " + filter);
                return true;
            }
        });

        return true;
    }
    // Метод для поиска
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.search_view:
                return true;
            case R.id.users: // Пользователи
                startActivity(new Intent(ResultTestsNameActivity.this, UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(ResultTestsNameActivity.this, SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(ResultTestsNameActivity.this, AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ResultTestsNameActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // Создаем свой адаптер
    public class CustomAdapter extends BaseAdapter implements Filterable {
        private List<SaveResultTests> mTestList;
        private List<SaveResultTests> mTestListFiltered;
        private Context mContext;

        public CustomAdapter(List<SaveResultTests> mTestList, Context mContext) {
            this.mTestList = mTestList;
            this.mTestListFiltered = mTestList;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mTestListFiltered.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.row_items, null);

            TextView tvName = view.findViewById(R.id.tvItems);
            TextView tvDate = view.findViewById(R.id.tvDate);
            TextView tvTime = view.findViewById(R.id.tvTime);
            tvName.setText(mTestListFiltered.get(position).getFullName());
            tvDate.setText(mTestListFiltered.get(position).getDateTest());
            tvTime.setText(mTestListFiltered.get(position).getFinishTimeTest());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ResultTestsNameActivity.this, ResultTestsActivity.class)
                            .putExtra("item", mTestListFiltered.get(position)));
                }
            });

            // Обработчик при долгом нажатии(в данном случае происходит удаление)
            if (!filter) { // Если фильтер активен удалить нельзя
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String equals = mListFullName.get(position);

                        new AlertDialog.Builder(ResultTestsNameActivity.this)
                                .setIcon(android.R.drawable.ic_menu_delete)
                                .setTitle("Удаление данных")
                                .setMessage("Вы уверены, что хотите удалить данный пункт?")
                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {

                                        // Удаление у админа
                                        if (Constant.EMAIL_VERIFIED) {
                                            delete = false;
                                            mDataBase.addValueEventListener(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (!delete) {
                                                        for (DataSnapshot ds1 : snapshot.getChildren()) {
                                                            key1 = ds1.getKey();
                                                            itemIndex++;
                                                            key2 = ds1.getChildren().iterator().next().getKey();

                                                            if (key1 != null && key2 != null && key2.equals(equals)) {

                                                                if (itemIndex == position && !filter) {
                                                                    mDataBase.child(key1).child(key2).removeValue();
                                                                    delete = true;
                                                                    itemIndex = -1;
//                                                                    Snackbar.make(constLayout, "Удалено", Snackbar.LENGTH_LONG).show();
                                                                    Toast.makeText(mContext, "Удалено", Toast.LENGTH_SHORT).show();
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        mCustomAdapter.notifyDataSetChanged();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                        } else { // Удаление у пользователя
                                            delete = false;
                                            mDataBaseUser.addValueEventListener(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (!delete) {
                                                        for (DataSnapshot ds1 : snapshot.getChildren()) {
                                                            key1 = ds1.getKey();
                                                            itemIndex++;
                                                            key2 = ds1.getChildren().iterator().next().getKey();

                                                            if (key1 != null && key2 != null && key2.equals(equals)) {

                                                                if (itemIndex == position && !filter) {
                                                                    mDataBaseUser.child(key1).child(key2).removeValue();
                                                                    delete = true;
                                                                    itemIndex = -1;
//                                                                    Snackbar.make(constLayout, "Удалено", Snackbar.LENGTH_LONG).show();
                                                                    Toast.makeText(mContext, "Удалено", Toast.LENGTH_SHORT).show();
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        mCustomAdapter.notifyDataSetChanged();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                    }
                                })
                                .setNegativeButton("Нет", null)
                                .show();

                        return true;
                    }
                });
            }

            return view;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        filterResults.count = mTestList.size();
                        filterResults.values = mTestList;
                    } else {
                        String searchStr = constraint.toString().toLowerCase();

                        List<SaveResultTests> resultDate = new ArrayList<>();
                        for (SaveResultTests saveResultTests : mTestList) {
                            if (saveResultTests.getFullName().toLowerCase().contains(searchStr)
                                    || saveResultTests.getDateTest().toLowerCase().contains(searchStr)
                                    || saveResultTests.getFinishTimeTest().toLowerCase().contains(searchStr)) {
                                resultDate.add(saveResultTests);
                                Log.d("TheoryLog", "searchStr2: " + searchStr);
                            }

                            filterResults.count = resultDate.size();
                            filterResults.values = resultDate;
                        }
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    mTestListFiltered = (List<SaveResultTests>) results.values;
                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }
}
