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
import com.daisybell.myapp.check_list.SaveResultCheckList;
import com.daisybell.myapp.theory.Theory;
import com.daisybell.myapp.theory.TheoryListActivity;
import com.daisybell.myapp.theory.TheoryShowActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ResultCheckListNameActivity extends AppCompatActivity {
    private static String TAG = "myLog";

    private ListView lvResultCheckListName;
//    private ArrayAdapter<String> mAdapter;
    private List<String> mListFullName;
    private List<String> mListNamePhoto;
    private List<SaveResultCheckList> mListCheckList;
    private DatabaseReference mDataBase;
    private DatabaseReference mDataBaseUser;
    private FirebaseStorage mStorage;
    private StorageReference storageRef;
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
//    private ConstraintLayout constLayoutCheckList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_check_list_name);

        init();

        if (Constant.EMAIL_VERIFIED) {
            getDataFromDB();
        } else {
            getDataFromDBForUser();
        }

//        onClickItem();

        loadingDialog = new LoadingDialog(ResultCheckListNameActivity.this);
        loadingDialog.startLoadingDialog();
    }

    // Инициализация переменных
    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        lvResultCheckListName = findViewById(R.id.lvResultCheckListName);
        mListFullName = new ArrayList<>();
        mListNamePhoto = new ArrayList<>();
        mListCheckList = new ArrayList<>();
        mCustomAdapter = new CustomAdapter(mListCheckList, this);
        lvResultCheckListName.setAdapter(mCustomAdapter);

//        constLayoutCheckList = findViewById(R.id.constLayoutCheckList);

        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getUid();
//        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListFullName);
//        lvResultCheckListName.setAdapter(mAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.RESULT_CHECK_LIST_KEY);
        mDataBaseUser = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID)
                .child(Constant.USER_KEY).child(idUser).child(Constant.RESULT_CHECK_LIST_KEY);

        tvNotData = findViewById(R.id.tvNotData);

        mStorage = FirebaseStorage.getInstance();
        storageRef = mStorage.getReference();
    }

    // Получение данных с Firebase
    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListFullName.size() > 0) mListFullName.clear();
                if (mListNamePhoto.size() > 0) mListNamePhoto.clear();
                if (mListCheckList.size() > 0) mListCheckList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SaveResultCheckList resultCheckList = ds.getChildren().iterator().next().getValue(SaveResultCheckList.class);
                    if (resultCheckList != null) {
                        mListFullName.add(resultCheckList.fullNameUser);
                        mListNamePhoto.add(resultCheckList.namePhoto);
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
    // Получение данных с Firebase для User'ов
    private void getDataFromDBForUser() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListFullName.size() > 0) mListFullName.clear();
                if (mListNamePhoto.size() > 0) mListNamePhoto.clear();
                if (mListCheckList.size() > 0) mListCheckList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    SaveResultCheckList resultCheckList = ds.getChildren().iterator().next().getValue(SaveResultCheckList.class);
                    if (resultCheckList != null) {
                        mListFullName.add(resultCheckList.fullNameUser);
                        mListNamePhoto.add(resultCheckList.namePhoto);
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
        mDataBaseUser.addValueEventListener(vListener);
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
            final TextView tvDate = view.findViewById(R.id.tvDate);
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

            // Обработчик при долгом нажатии(в данном случае происходит удаление)
            if (!filter) { // Если фильтер активен удалить нельзя
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String equals = mListFullName.get(position);
                        final String namePhoto = mListNamePhoto.get(position);

                        new AlertDialog.Builder(ResultCheckListNameActivity.this)
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
                                                                    // Удаление сперва фотографии с storage, а после если все успешно с RealtimeDatabase
                                                                    StorageReference deleteRef = storageRef.child("images/"+namePhoto);
                                                                    deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override // Успешно
                                                                        public void onSuccess(Void aVoid) {
                                                                            mDataBase.child(key1).child(key2).removeValue();
                                                                            delete = true;
                                                                            itemIndex = -1;
                                                                            Toast.makeText(mContext, "Удалено", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override // Не удалось удалить
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(mContext, "Не удалось удалить, попробуйте позже снова", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
//                                                                    Snackbar.make(constLayoutCheckList, "Удалено", Snackbar.LENGTH_LONG).show();
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
                                                                    // Удаление сперва фотографии с storage, а после если все успешно с RealtimeDatabase
                                                                    StorageReference deleteRef = storageRef.child("images/"+namePhoto);
                                                                    deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override // Успешно
                                                                        public void onSuccess(Void aVoid) {
                                                                            mDataBase.child(key1).child(key2).removeValue();
                                                                            delete = true;
                                                                            itemIndex = -1;
                                                                            Toast.makeText(mContext, "Удалено", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override // Не удалось удалить
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(mContext, "Не удалось удалить, попробуйте позже снова", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
//                                                                    Snackbar.make(constLayoutCheckList, "Удалено", Snackbar.LENGTH_LONG).show();
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

                    mCheckListFiltered = (List<SaveResultCheckList>) results.values;
                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }


}
