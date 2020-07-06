package com.daisybell.myapp.menu;

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
import com.daisybell.myapp.auth.User;
import com.daisybell.myapp.check_list.SaveResultCheckList;
import com.daisybell.myapp.result.ResultCheckListActivity;
import com.daisybell.myapp.result.ResultCheckListNameActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private static String TAG = "myLog";

    private ListView lvUsers;
    private List<String> mListFullName;
    private List<String> mListIdUsers;
    private List<User> mListUsers;
    private DatabaseReference mDataBase;
//    private String idUser;

//    private FirebaseAuth mAuth;

    CustomAdapter mCustomAdapter;

    LoadingDialog loadingDialog;
    private TextView tvNotData;

    private String key1 = "";
    private boolean delete;

    private int itemIndex = -1;
    private boolean filter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        init();
        getDataFromDB();


        loadingDialog = new LoadingDialog(UsersActivity.this);
        loadingDialog.startLoadingDialog();
    }

    // Инициализация переменных
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        lvUsers = findViewById(R.id.lvUsers);
        mListFullName = new ArrayList<>();
        mListUsers = new ArrayList<>();
        mCustomAdapter = new CustomAdapter(mListUsers, this);
        lvUsers.setAdapter(mCustomAdapter);

//        mAuth = FirebaseAuth.getInstance();
//        idUser = mAuth.getUid();
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.USER_KEY);

        tvNotData = findViewById(R.id.tvNotData);
    }

    private void getDataFromDB() {
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListFullName.size() > 0) mListFullName.clear();
                if (mListUsers.size() > 0) mListUsers.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user != null) {
                        mListFullName.add(user.name + " " + user.surname);
                        mListUsers.add(user);
                    }
                }
                mCustomAdapter.notifyDataSetChanged();
                goneText();
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Проверка данных, если их нет вывести текст
    private void goneText() {

        if (mListUsers.size() == 0) {
            tvNotData.setVisibility(View.VISIBLE);
        } else if (mListUsers.size() > 1) {
            tvNotData.setVisibility(View.GONE);
        }
    }

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

        if(id == R.id.search_view) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Создаем свой адаптер
    public class CustomAdapter extends BaseAdapter implements Filterable {
        private List<User> mUsersList;
        private List<User> mUsersListFiltered;
        private Context mContext;

        public CustomAdapter(List<User> mUsersList, Context mContext) {
            this.mUsersList = mUsersList;
            this.mUsersListFiltered = mUsersList;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mUsersListFiltered.size();
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
            View view = getLayoutInflater().inflate(R.layout.row_item_users, null);

            TextView tvName = view.findViewById(R.id.tvName);
            final TextView tvSurname = view.findViewById(R.id.tvSurname);
            TextView tvEmail = view.findViewById(R.id.tvEmail);
            tvName.setText(mUsersListFiltered.get(position).getName());
            tvSurname.setText(mUsersListFiltered.get(position).getSurname());
            tvEmail.setText(mUsersListFiltered.get(position).getEmail());

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(UsersActivity.this, ResultCheckListActivity.class)
//                            .putExtra("item", mCheckListFiltered.get(position)));
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
                        filterResults.count = mListUsers.size();
                        filterResults.values = mListUsers;
                    } else {
                        String searchStr = constraint.toString().toLowerCase();

                        List<User> resultDate = new ArrayList<>();
                        for (User user : mListUsers) {
                            if (user.getName().toLowerCase().contains(searchStr)
                                    || user.getSurname().toLowerCase().contains(searchStr)
                                    || user.getEmail().toLowerCase().contains(searchStr)) {
                                resultDate.add(user);
                                Log.d(TAG, "searchStr1: " + searchStr);
                            }

                            filterResults.count = resultDate.size();
                            filterResults.values = resultDate;
                        }
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    mUsersListFiltered = (List<User>) results.values;
                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }
}