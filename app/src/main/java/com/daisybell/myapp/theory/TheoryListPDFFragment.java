package com.daisybell.myapp.theory;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


public class TheoryListPDFFragment extends Fragment {

    private static final String TAG = "myLog";

    private ListView lvPDFName;
    private List<String> mListNamePDF;
    private List<String> mListNamePDFStorage;
    private List<String> mListPDFUri;
    private List<UploadPDF> mListPDF;
    private DatabaseReference mDataBase;
    private StorageReference storageRef;

    CustomAdapter mCustomAdapter;

//    LoadingDialog loadingDialog;
    private ProgressBar mProgressBar;
    private TextView tvNotData;

    private String key1 = "";
    private boolean delete;

    private int itemIndex = -1;
    private boolean filterUse = false;


    public TheoryListPDFFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        View v = inflater.inflate(R.layout.fragment_theory_list_pdf, container, false);
        lvPDFName = v.findViewById(R.id.lvPDFName);
        mListNamePDF = new ArrayList<>();
        mListNamePDFStorage = new ArrayList<>();
        mListPDFUri = new ArrayList<>();
        mListPDF = new ArrayList<>();
        mCustomAdapter = new CustomAdapter(mListPDF, getContext());
        lvPDFName.setAdapter(mCustomAdapter);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.PDF_FILE_KEY);
        storageRef = FirebaseStorage.getInstance().getReference();

        tvNotData = v.findViewById(R.id.tvNotData);

        getDataFromDB();

//        loadingDialog = new LoadingDialog(getActivity());
//        loadingDialog.startLoadingDialog();
        mProgressBar = v.findViewById(R.id.pbPDFList);
        mProgressBar.setVisibility(View.VISIBLE);

        return v;
    }
    // Получение данных с Firebase
    private void getDataFromDB() {
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListNamePDF.size() > 0)mListNamePDF.clear();
                if (mListNamePDFStorage.size() > 0)mListNamePDFStorage.clear();
                if (mListPDFUri.size() > 0)mListPDFUri.clear();
                if (mListPDF.size() > 0) mListPDF.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    UploadPDF uploadPDF = ds.getValue(UploadPDF.class);

                    if (uploadPDF != null) {
                        mListNamePDF.add(uploadPDF.name);
                        mListNamePDFStorage.add(uploadPDF.namePDFStorage);
                        mListPDFUri.add(uploadPDF.url);
                        mListPDF.add(uploadPDF);
                    }
                }
//               mAdapter.notifyDataSetChanged();
                mCustomAdapter.notifyDataSetChanged();
                goneText();
//                loadingDialog.dismissDialog();
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Проверка данных, если их нет вывести текст
    private void goneText() {

        if (mListNamePDF.size() == 0) {
            tvNotData.setVisibility(View.VISIBLE);
        } else if (mListNamePDF.size() > 1) {
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
                    filterUse = true;
                } else {
                    filterUse = false;
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
        private List<UploadPDF> mPDFList;
        private List<UploadPDF> mPDFListFiltered;
        private Context mContext;

        public CustomAdapter(List<UploadPDF> mPDFList, Context mContext) {
            this.mPDFList = mPDFList;
            this.mPDFListFiltered = mPDFList;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mPDFListFiltered.size();
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
            View view = getLayoutInflater().inflate(R.layout.adapter_pdf, null);

            TextView textView = view.findViewById(R.id.tvPDFName);
            textView.setText(mPDFListFiltered.get(position).getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TheoryShowPDFActivity.class);
                    intent.putExtra("item", mPDFListFiltered.get(position));
                    startActivity(intent);
//                    startActivity(new Intent(getContext(), TheoryShowPDFActivity.class)
//                            .putExtra("item", mPDFListFiltered.get(position)));
                }
            });
            // Если не использовался поиск и пользователь админ(долгое нажатие -> удаление)
            if (!filterUse && Constant.EMAIL_VERIFIED) {
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String namePDF = mListNamePDF.get(position);
                        final String namePDFStorage = mListNamePDFStorage.get(position);

                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.ic_menu_delete)
                                .setTitle("Удаление данных")
                                .setMessage("Вы уверены, что хотите удалить: \"" + namePDF + "\" ?")
                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        delete = false;
                                        mDataBase.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (!delete) {
                                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                                        key1 = ds.getKey();
                                                        itemIndex++;
                                                        if (key1 != null && itemIndex == position && !filterUse) {
                                                            StorageReference deleteRef = storageRef.child(Constant.PDF_FILE_STORAGE_KEY + namePDFStorage);
                                                            deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override // Успешно
                                                                public void onSuccess(Void aVoid) {
                                                                    mDataBase.child(key1).removeValue();
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
                                                            break;

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
                        filterResults.count = mListPDF.size();
                        filterResults.values = mListPDF;
                    } else {
                        String searchStr = constraint.toString().toLowerCase();

                        List<UploadPDF> resultDate = new ArrayList<>();
                        for (UploadPDF uploadPDF : mListPDF) {
                            if (uploadPDF.getName().toLowerCase().contains(searchStr)) {
                                resultDate.add(uploadPDF);
                            }

                            filterResults.count = resultDate.size();
                            filterResults.values = resultDate;
                        }
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    mPDFListFiltered = (List<UploadPDF>) results.values;
                    notifyDataSetChanged();

                }
            };
            return filter;
        }
    }
}