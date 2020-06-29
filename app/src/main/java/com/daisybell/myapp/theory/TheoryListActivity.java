package com.daisybell.myapp.theory;

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
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.check_list.CheckListNameActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TheoryListActivity extends AppCompatActivity {

    private static String TAG = "TheoryLog";

    private ListView mLvTheory;
//    private ArrayAdapter<String> mAdapter;
    private List<String> mListTitle;
    private List<Theory> mListTheory;
    private DatabaseReference mDataBase;

    CustomAdapter mCustomAdapter;

    LoadingDialog loadingDialog;
    private TextView tvNotData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory_list);
        setTitle("Теория");

        init();
        getDataFromDB();
//        onClickItem();
//        longDeleteClick();


        loadingDialog = new LoadingDialog(TheoryListActivity.this);
        loadingDialog.startLoadingDialog();

    }

    // Инициализация переменных
    private void init() {
        mLvTheory = findViewById(R.id.lvTheory);
        mListTitle = new ArrayList<>();
        mListTheory = new ArrayList<>();
        mCustomAdapter = new CustomAdapter(mListTheory, this);
        mLvTheory.setAdapter(mCustomAdapter);
//        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListTitle);
//        mLvTheory.setAdapter(mAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.THEORY_KEY);

        tvNotData = findViewById(R.id.tvNotData);
    }

    // Получение данных с Firebase
    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListTitle.size() > 0)mListTitle.clear();
                if (mListTheory.size() > 0) mListTheory.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Theory theory = ds.getValue(Theory.class);
                    assert theory != null;
                    mListTitle.add(theory.title);
                    mListTheory.add(theory);
                }
//               mAdapter.notifyDataSetChanged();
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

        if (mListTheory.size() == 0) {
            tvNotData.setVisibility(View.VISIBLE);
        } else if (mListTheory.size() > 1) {
            tvNotData.setVisibility(View.GONE);
        }
    }

    // Обработчик нажатия на list с получения данных от position
//    private void onClickItem() {
//        mLvTheory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Theory theory = mListTheory.get(position);
////                Intent intent = new Intent(TheoryListActivity.this, TheoryShowActivity.class);
////                intent.putExtra(Constant.THEORY_TITLE, theory.title);
////                intent.putExtra(Constant.THEORY_TEXT, theory.text);
////                startActivity(intent);
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
        private List<Theory> mTheoryList;
        private List<Theory> mTheoryListFiltered;
        private Context mContext;

        public CustomAdapter(List<Theory> mTheoryList, Context mContext) {
            this.mTheoryList = mTheoryList;
            this.mTheoryListFiltered = mTheoryList;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mTheoryListFiltered.size();
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
            View view = getLayoutInflater().inflate(R.layout.row, null);

            TextView textView = view.findViewById(R.id.tvItems);
            textView.setText(mTheoryListFiltered.get(position).getTitle());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(TheoryListActivity.this, TheoryShowActivity.class)
                    .putExtra("item", mTheoryListFiltered.get(position)));
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
                        filterResults.count = mTheoryList.size();
                        filterResults.values = mTheoryList;
                    } else {
                        String searchStr = constraint.toString().toLowerCase();

                        List<Theory> resultDate = new ArrayList<>();
                        for (Theory theory : mTheoryList) {
                            if (theory.getTitle().toLowerCase().contains(searchStr)) {
                                resultDate.add(theory);
                            }

                            filterResults.count = resultDate.size();
                            filterResults.values = resultDate;
                        }
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    mTheoryListFiltered = (List<Theory>) results.values;
                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }

//    // Метод для удаления данных при долгом нажатии
//    private void longDeleteClick() {
//        mLvTheory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//
//                new AlertDialog.Builder(TheoryListActivity.this)
//                        .setIcon(android.R.drawable.ic_menu_delete)
//                        .setTitle("Удаление данных")
//                        .setMessage("Вы уверены, что хотите удалить: \"" + mListTitle.get(position) + "\" ?")
//                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                mDataBase.child(mListTitle.get(position)).removeValue();
//                                mCustomAdapter.notifyDataSetChanged();
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
