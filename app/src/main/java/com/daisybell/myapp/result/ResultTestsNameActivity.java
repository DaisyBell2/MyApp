package com.daisybell.myapp.result;

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
import com.daisybell.myapp.check_list.SaveResultCheckList;
import com.daisybell.myapp.test.SaveResultTests;
import com.daisybell.myapp.theory.TheoryListActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResultTestsNameActivity extends AppCompatActivity {

    private ListView lvResultTestsName;
//    private ArrayAdapter<String> mAdapter;
//    private List<String> mListFullName;
    private List<SaveResultTests> mListTests;
    private DatabaseReference mDataBase;

    CustomAdapter mCustomAdapter;

    LoadingDialog loadingDialog;
    private TextView tvNotData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_tests_name);
        setTitle("Результаты тестов");

        init();
        getDataFromDB();
//        onClickItem();

        loadingDialog = new LoadingDialog(ResultTestsNameActivity.this);
        loadingDialog.startLoadingDialog();

    }

    // Инициализация переменных
    private void init() {
        lvResultTestsName = findViewById(R.id.lvResultTestsName);
//        mListFullName = new ArrayList<>();
        mListTests = new ArrayList<>();
        mCustomAdapter = new CustomAdapter(mListTests, this);
        lvResultTestsName.setAdapter(mCustomAdapter);
//        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListFullName);
//        lvResultTestsName.setAdapter(mAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.RESULT_TESTS);

        tvNotData = findViewById(R.id.tvNotData);
    }

    // Получение данных с Firebase
    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (mListFullName.size() > 0) mListFullName.clear();
                if (mListTests.size() > 0) mListTests.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SaveResultTests resultTests = ds.getValue(SaveResultTests.class);
                    if (resultTests != null) {
//                        mListFullName.add(resultTests.fullName + "/"
//                                + resultTests.dateTest + "/"
//                                + resultTests.finishTimeTest);
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
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mCustomAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }
    // Метод для поиска
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.search_view) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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