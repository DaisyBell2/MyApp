package com.daisybell.myapp.theory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TheoryListLetterFragment extends Fragment {

    private static String TAG = "myLog";

    private ListView mLvTheory;
    private List<String> mListTitle;
    private List<Theory> mListTheory;
    private DatabaseReference mDataBase;

    CustomAdapter mCustomAdapter;

//    LoadingDialog loadingDialog;
    private ProgressBar mProgressBar;
    private TextView tvNotData;

    private String key1 = "";
    private String key2 = "";
    private boolean delete;

    private int itemIndex = -1;
    private boolean filter = false;

    public TheoryListLetterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        View v = inflater.inflate(R.layout.fragment_theory_list_letter, container, false);
        mLvTheory = v.findViewById(R.id.lvTheory);
        mListTitle = new ArrayList<>();
        mListTheory = new ArrayList<>();
        mCustomAdapter = new CustomAdapter(mListTheory, getContext());
        mLvTheory.setAdapter(mCustomAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.THEORY_KEY);

        tvNotData = v.findViewById(R.id.tvNotData);

        getDataFromDB();

//        loadingDialog = new LoadingDialog(getActivity());
//        loadingDialog.startLoadingDialog();
        mProgressBar = v.findViewById(R.id.pbLetterList);
        mProgressBar.setVisibility(View.VISIBLE);

        return v;
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
//                loadingDialog.dismissDialog();
                mProgressBar.setVisibility(View.GONE);
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
    // Делаем поиск в меню
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);

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
                if (!TextUtils.isEmpty(newText)) {
                    filter = true;
                } else {
                    filter = false;
                }
                return true;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }
    // Метод для поиска
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.search_view:
                return true;
            case R.id.users: // Пользователи
                startActivity(new Intent(getContext(), UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(getContext(), AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    startActivity(new Intent(getContext(), TheoryShowActivity.class)
                            .putExtra("item", mTheoryListFiltered.get(position)));
                }
            });

            // Если не использовался поиск и пользователь админ(долгое нажатие -> удаление)
            if (!filter && Constant.EMAIL_VERIFIED) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String equals = mListTitle.get(position);

                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.ic_menu_delete)
                                .setTitle("Удаление данных")
                                .setMessage("Вы уверены, что хотите удалить: \"" + equals + "\" ?")
                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDataBase.child(equals).removeValue();
                                        mCustomAdapter.notifyDataSetChanged();
                                        Toast.makeText(mContext, "Удалено", Toast.LENGTH_SHORT).show();
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

}