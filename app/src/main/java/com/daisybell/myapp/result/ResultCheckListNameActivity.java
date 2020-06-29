package com.daisybell.myapp.result;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
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
import com.daisybell.myapp.check_list.CheckListNameActivity;
import com.daisybell.myapp.check_list.SaveResultCheckList;
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

public class ResultCheckListNameActivity extends AppCompatActivity {
    private static String TAG = "myLog";

    private ListView lvResultCheckListName;
//    private ArrayAdapter<String> mAdapter;
    private List<String> mListFullName;
    private List<SaveResultCheckList> mListCheckList;
    private DatabaseReference mDataBase;

    CustomAdapter mCustomAdapter;

    LoadingDialog loadingDialog;
    private TextView tvNotData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_check_list_name);
        setTitle("Результаты чек-листов");

        init();
        lvResultCheckListName.setLongClickable(true);
        getDataFromDB();
//        onClickItem();
//        longDeleteClick();

        loadingDialog = new LoadingDialog(ResultCheckListNameActivity.this);
        loadingDialog.startLoadingDialog();
    }

    // Инициализация переменных
    private void init() {
        lvResultCheckListName = findViewById(R.id.lvResultCheckListName);
        mListFullName = new ArrayList<>();
        mListCheckList = new ArrayList<>();
        mCustomAdapter = new CustomAdapter(mListCheckList, this);
        lvResultCheckListName.setAdapter(mCustomAdapter);
//        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListFullName);
//        lvResultCheckListName.setAdapter(mAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.RESULT_CHECK_LIST_KEY);

        tvNotData = findViewById(R.id.tvNotData);
    }

    // Получение данных с Firebase
    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListFullName.size() > 0) mListFullName.clear();
                if (mListCheckList.size() > 0) mListCheckList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SaveResultCheckList resultCheckList = ds.getValue(SaveResultCheckList.class);
                    if (resultCheckList != null) {
                        mListFullName.add(resultCheckList.fullNameUser + "/"
                                + resultCheckList.date + "/"
                                + resultCheckList.time);
                        mListCheckList.add(resultCheckList);
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

        if (mListCheckList.size() == 0) {
            tvNotData.setVisibility(View.VISIBLE);
        } else if (mListCheckList.size() > 1) {
            tvNotData.setVisibility(View.GONE);
        }
    }
    // Обработчик нажатия на list с получения данных от position
//    private void onClickItem() {
//        lvResultCheckListName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                SaveResultCheckList resultCheckList = mListCheckList.get(position);
//                Intent intent = new Intent(ResultCheckListNameActivity.this, ResultCheckListActivity.class);
//                intent.putExtra(Constant.RESULT_CHECK_LIST_NAME, resultCheckList.fullNameUser);
//                intent.putExtra(Constant.RESULT_CHECK_LIST_NAME_CL, resultCheckList.nameCheckList);
//                intent.putExtra(Constant.RESULT_CHECK_LIST_PHOTO_URI, resultCheckList.photoUri);
//                intent.putExtra(Constant.RESULT_CHECK_LIST_DATE, resultCheckList.date);
//                intent.putExtra(Constant.RESULT_CHECK_LIST_TIME, resultCheckList.time);
//                intent.putExtra(Constant.RESULT_CHECK_LIST_DONE, resultCheckList.doneCheckList);
//                intent.putExtra(Constant.RESULT_CHECK_LIST_NOT_DONE, resultCheckList.notDoneCheckList);
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
        private List<SaveResultCheckList> mCheckList;
        private List<SaveResultCheckList> mCheckListFiltered;
        private Context mContext;

        public CustomAdapter(List<SaveResultCheckList> mCheckList, Context mContext) {
            this.mCheckList = mCheckList;
            this.mCheckListFiltered = mCheckList;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mCheckListFiltered.size();
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
            tvName.setText(mCheckListFiltered.get(position).getFullNameUser());
            tvDate.setText(mCheckListFiltered.get(position).getDate());
            tvTime.setText(mCheckListFiltered.get(position).getTime());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ResultCheckListNameActivity.this, ResultCheckListActivity.class)
                            .putExtra("item", mCheckListFiltered.get(position)));
                }
            });

            // Метод для удаления данных при долгом нажатии
//            view.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//
//                    new AlertDialog.Builder(ResultCheckListNameActivity.this)
//                        .setIcon(android.R.drawable.ic_menu_delete)
//                        .setTitle("Удаление данных")
//                        .setMessage("Вы уверены, что хотите удалить: \"" + mListFullName.get(position) + "\" ?")
//                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                mDataBase.child(mListFullName.get(position)).removeValue();
////                                mCustomAdapter.notifyDataSetChanged();
//                            }
//                        })
//                        .setNegativeButton("Нет", null)
//                        .show();
//
//                    return true;
//                }
//            });

            return view;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        filterResults.count = mCheckList.size();
                        filterResults.values = mCheckList;
                    } else {
                        String searchStr = constraint.toString().toLowerCase();

                        List<SaveResultCheckList> resultDate = new ArrayList<>();
                        for (SaveResultCheckList saveResultCheckList : mCheckList) {
                            if (saveResultCheckList.getFullNameUser().toLowerCase().contains(searchStr)
                                 || saveResultCheckList.getDate().toLowerCase().contains(searchStr)
                                 || saveResultCheckList.getTime().toLowerCase().contains(searchStr)) {
                                resultDate.add(saveResultCheckList);
                                Log.d("TheoryLog", "searchStr1: " + searchStr);
                            }

                            filterResults.count = resultDate.size();
                            filterResults.values = resultDate;
                        }
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    mCheckListFiltered = (List<SaveResultCheckList>) results.values;
                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }

    // Метод для удаления данных при долгом нажатии
//    private void longDeleteClick() {
//        lvResultCheckListName.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//
//                new AlertDialog.Builder(ResultCheckListNameActivity.this)
//                        .setIcon(android.R.drawable.ic_menu_delete)
//                        .setTitle("Удаление данных")
//                        .setMessage("Вы уверены, что хотите удалить: \"" + mListFullName.get(position) + "\" ?")
//                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                String key = mDataBase.getKey();
//                                Log.d(TAG, "key: " + key);
////                                mDataBase.child(mListFullName.get(position)).removeValue();
////                                mCustomAdapter.notifyDataSetChanged();
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
