package com.daisybell.myapp.check_list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.ProgressButton;
import com.daisybell.myapp.R;
import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.auth.User;
import com.daisybell.myapp.menu.AboutApplicationActivity;
import com.daisybell.myapp.menu.SettingsActivity;
import com.daisybell.myapp.menu.UsersActivity;
import com.daisybell.myapp.test.SaveResultTests;
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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckListShowActivity extends AppCompatActivity {

    private static String TAG = "ResCheck";
    private static final int CAMERA_REQUEST = 101;

    Handler handler = new Handler();

    private ImageView imageView;
    private boolean isImageScaled = false;
    private ImageButton ibAddPhoto;
    private String mCurrentPhotoPath;
    private Uri photoURI;
    private String downloadPhotoURI;

    private TextView tvCheckListTitle, tvCheckListPoint;
    private CheckBox cbPar1, cbPar2, cbPar3, cbPar4, cbPar5;
//    private Button btSaveCheckListUser;
    private String dateText, timeText;

    private FirebaseStorage mStorage;
    private StorageReference storageRef;
    private StorageReference imagesRef;

    private DatabaseReference mDBUser;
    private DatabaseReference mDBAdmin;
    private DatabaseReference mDataBase;
    private DatabaseReference mDataBaseUser;
    private FirebaseAuth mAuth;

    private String idUser;
    private String idAdmin;
    private String fullNameUser;
    private String doneCheckList = "";
    private String notDoneCheckList = "";

    private boolean imageViewVisible = false;

    private View mView;

    private String namePhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_show);

        init();
        getIntentMain();

        if (Constant.EMAIL_VERIFIED) {
            getFullNameAdmin();
        } else {
            getFullNameUser();
        }

        // Отбработка кнопки "Сохранить"
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressButton progressButton = new ProgressButton(CheckListShowActivity.this, mView);

                if (imageViewVisible) {
                    progressButton.buttonActivated();
                    blockUnblock(false);
                    StorageReference referenceToImage = storageRef.child("images/" + photoURI.getLastPathSegment());
                    referenceToImage.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "Загрузка фото в firebase прошла успешно!");
                            /////////////////////////////
                            storageRef.child("images/" + photoURI.getLastPathSegment()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    downloadPhotoURI = String.valueOf(uri);
                                    namePhoto = String.valueOf(photoURI.getLastPathSegment());

                                    Log.d(TAG, "downloadPhotoURI: " + downloadPhotoURI);
                                    Log.d(TAG, "photoURILast: " + photoURI.getLastPathSegment());
                                    Log.d(TAG, "namePhoto: " + namePhoto);
                                    Log.d(TAG,"Uri c firebase получено");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Ошибка в получении uri c firebase");
                                }
                            });

                            ///////////////////////
                            if (cbPar1.isChecked()) doneCheckList += cbPar1.getText() + ", ";
                            else notDoneCheckList += cbPar1.getText() + ", ";
                            if (cbPar2.isChecked()) doneCheckList += cbPar2.getText() + ", ";
                            else notDoneCheckList += cbPar2.getText() + ", ";
                            if (cbPar3.isChecked()) doneCheckList += cbPar3.getText() + ", ";
                            else notDoneCheckList += cbPar3.getText() + ", ";
                            if (cbPar4.isChecked()) doneCheckList += cbPar4.getText() + ", ";
                            else notDoneCheckList += cbPar4.getText() + ", ";
                            if (cbPar5.isChecked()) doneCheckList += cbPar5.getText() + ", ";
                            else notDoneCheckList += cbPar5.getText() + ", ";
                            //////////////////////////////////
                            Handler handler1 = new Handler();
                            handler1.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() { //

                                            String nameCheckList = tvCheckListTitle.getText().toString() + " / " + tvCheckListPoint.getText().toString();
                                            SaveResultCheckList newResultCheckList = new SaveResultCheckList(idUser, fullNameUser, nameCheckList, downloadPhotoURI,
                                                    dateText, timeText, doneCheckList, notDoneCheckList, namePhoto);
                                            mDataBase.push().child(fullNameUser).setValue(newResultCheckList);
//                                    Toast.makeText(CheckListShowActivity.this, "Все данные сохранены!", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Все данные сохранены в firebase");

                                            // Сохранение данных у пользователя
                                            if (!Constant.EMAIL_VERIFIED) {
                                                mDataBaseUser = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID)
                                                        .child(Constant.USER_KEY).child(idUser).child(Constant.RESULT_CHECK_LIST_KEY);
                                                SaveResultCheckList newResultCheckListUser = new SaveResultCheckList(idUser, fullNameUser, nameCheckList,
                                                        downloadPhotoURI, dateText, timeText, doneCheckList, notDoneCheckList, namePhoto);
                                                mDataBaseUser.push().child(fullNameUser).setValue(newResultCheckListUser);
                                                Log.d(TAG, "Данные user'a успешно добавленны!");
                                            }

                                            ////////////////////////////////////
                                            doneCheckList = "";
                                            notDoneCheckList = "";
//                                            cleaning();
//                                            blockUnblock(true);

//                                            finish();
                                        }
                                    }, 800);
                                    progressButton.buttonFinished();
                                    blockUnblock(true);
                                    cleaning();

                                    Handler handler2 = new Handler();
                                    handler2.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
//                                            blockUnblock(true);
                                            finish();
                                        }
                                    }, 1300);
                                }
                            }, 2000);




                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CheckListShowActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Ошибка: " + e);
                        }
                    });


                } else {
                    Toast.makeText(CheckListShowActivity.this, "Вы не сделали фотографию рабочего места!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constant.EMAIL_VERIFIED = preferences.getBoolean(Constant.EMAIL_VERIFIED_INDEX, false);
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        tvCheckListTitle = findViewById(R.id.tvCheckListTitle);
        tvCheckListPoint = findViewById(R.id.tvCheckListPoint);
//        btSaveCheckListUser = findViewById(R.id.btSaveCheckListUser);
        cbPar1 = findViewById(R.id.cbPar1);
        cbPar2 = findViewById(R.id.cbPar2);
        cbPar3 = findViewById(R.id.cbPar3);
        cbPar4 = findViewById(R.id.cbPar4);
        cbPar5 = findViewById(R.id.cbPar5);
        imageView = findViewById(R.id.imageView);
        ibAddPhoto = findViewById(R.id.ibAddPhoto);

        mStorage = FirebaseStorage.getInstance();
        storageRef = mStorage.getReference();
//        imagesRef = storageRef.child("images/");
        mDBUser = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.USER_KEY);
        mDBAdmin = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.ADMIN_DATE);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.RESULT_CHECK_LIST_KEY);
        mAuth = FirebaseAuth.getInstance();

        mView = findViewById(R.id.btSaveCheckListUser);
    }
    private void getIntentMain() {
        Intent intent = getIntent();
        if (intent != null) {
            tvCheckListTitle.setText(intent.getStringExtra(Constant.CHECK_LIST_NAME));
            tvCheckListPoint.setText(intent.getStringExtra(Constant.CHECK_LIST_BEFORE_AFTER));
            cbPar1.setText(intent.getStringExtra(Constant.CHECK_LIST_PAR1));
            cbPar2.setText(intent.getStringExtra(Constant.CHECK_LIST_PAR2));
            cbPar3.setText(intent.getStringExtra(Constant.CHECK_LIST_PAR3));
            cbPar4.setText(intent.getStringExtra(Constant.CHECK_LIST_PAR4));
            cbPar5.setText(intent.getStringExtra(Constant.CHECK_LIST_PAR5));
        }
    }

    // Обработчик кнопки "Камера"
    public void onClickAddPhoto(View view) {
        getDateAndTime();
        imageView.setVisibility(View.VISIBLE);
        takePictureIntent();
    }

    // Обращаемся с помощью intent к камере телефона
    // Этот метод создает файл для фотографии и запускает приложение камеры с помощью интента.
    private void takePictureIntent() {
        Intent takePictureIntent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // проверяем, что есть приложение способное обработать интент
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // создать файл для фотографии
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.d(TAG, "Ошибка: " + e);
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.daisybell.myapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                Log.d(TAG, "photoFile: " + photoFile);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* префикс */
                ".jpg",  /* расширение */
                storageDir /* директория */

        );
        // сохраняем пусть для использования с интентом ACTION_VIEW
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "mCurrentPhotoPath: " + mCurrentPhotoPath);
        Log.d(TAG, "imageFileName: " + imageFileName);
        Log.d(TAG, "storageDir: " + storageDir);
        Log.d(TAG, "image: " + image);
        return image;
    }


    private void getDateAndTime() {
        // Текущее время
        Date currentDate = new Date();
        // Форматирование времени как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        dateText = dateFormat.format(currentDate);
        // Форматирование времени как "часы:минуты:секунды"
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        timeText = timeFormat.format(currentDate);
    }
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            imageView.setImageURI(photoURI);
            imageViewVisible = true;
            Log.d(TAG, "photoURI: " + photoURI);
//            Bitmap thumbnailBitmap = null;
//            if (data != null) {
//                thumbnailBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
//                imageView.setImageBitmap(thumbnailBitmap);
        }
    }

//    public void onClickSaveCheckListUser(View view) {
////        if (imageViewVisible) {
////            blockUnblock(false);
////            StorageReference referenceToImage = storageRef.child("images/" + photoURI.getLastPathSegment());
////            referenceToImage.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
////                @Override
////                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////                    Log.d(TAG, "Загрузка фото в firebase прошла успешно!");
////                    /////////////////////////////
////                    storageRef.child("images/" + photoURI.getLastPathSegment()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
////                        @Override
////                        public void onSuccess(Uri uri) {
////                            downloadPhotoURI = String.valueOf(uri);
////                            Log.d(TAG, "downloadPhotoURI: " + downloadPhotoURI);
////                            Log.d(TAG,"Uri c firebase получено");
////                        }
////                    }).addOnFailureListener(new OnFailureListener() {
////                        @Override
////                        public void onFailure(@NonNull Exception e) {
////                            Log.d(TAG, "Ошибка в получении uri c firebase");
////                        }
////                    });
////
////                    ///////////////////////
////                    if (cbPar1.isChecked()) doneCheckList += cbPar1.getText() + ", ";
////                    else notDoneCheckList += cbPar1.getText() + ", ";
////                    if (cbPar2.isChecked()) doneCheckList += cbPar2.getText() + ", ";
////                    else notDoneCheckList += cbPar2.getText() + ", ";
////                    if (cbPar3.isChecked()) doneCheckList += cbPar3.getText() + ", ";
////                    else notDoneCheckList += cbPar3.getText() + ", ";
////                    if (cbPar4.isChecked()) doneCheckList += cbPar4.getText() + ", ";
////                    else notDoneCheckList += cbPar4.getText() + ", ";
////                    if (cbPar5.isChecked()) doneCheckList += cbPar5.getText() + ", ";
////                    else notDoneCheckList += cbPar5.getText() + ", ";
////                    //////////////////////////////////
////                    handler.postDelayed(new Runnable() {
////                        @Override
////                        public void run() { //
////                            String nameCheckList = tvCheckListTitle.getText().toString() + " / " + tvCheckListPoint.getText().toString();
////                            SaveResultCheckList newResultCheckList = new SaveResultCheckList(idUser, fullNameUser, nameCheckList, downloadPhotoURI,
////                                    dateText, timeText, doneCheckList, notDoneCheckList, namePhoto);
////                            mDataBase.push().setValue(newResultCheckList);
////                            Toast.makeText(CheckListShowActivity.this, "Все данные сохранены!", Toast.LENGTH_SHORT).show();
////                            Log.d(TAG, "Все данные сохранены в firebase");
////
////                            ////////////////////////////////////
////                            doneCheckList = "";
////                            notDoneCheckList = "";
////                            cleaning();
////                            blockUnblock(true);
////
////                            finish();
////                        }
////                    }, 800);
////
////
////
////                }
////            }).addOnFailureListener(new OnFailureListener() {
////                @Override
////                public void onFailure(@NonNull Exception e) {
////                    Toast.makeText(CheckListShowActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
////                    Log.d(TAG, "Ошибка: " + e);
////                }
////            });
////
////
////        } else {
////            Toast.makeText(this, "Вы не сделали фотографию рабочего места!", Toast.LENGTH_SHORT).show();
////        }
////    }


    // Отбработка кнопки "Сохранить"

//    // Обработчик ImageView, для увеличения картинки по нажатию
//    public void onClickImageScaled(View view) {
//        if (!isImageScaled) view.animate().scaleX(1.4f).scaleY(1.4f).setDuration(500);
//        if (isImageScaled) view.animate().scaleX(1f).scaleY(1f).setDuration(500);
//        isImageScaled = !isImageScaled;
//    }

    // Получаем fullName user'a
    private void getFullNameUser() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idUser = mAuth.getUid();
                Log.d(TAG, "idUser: " + idUser);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    if (idUser != null && idUser.equals(key)) {
                        User user = ds.getValue(User.class);
                        if (user != null) {
                            fullNameUser = (user.name +" "+ user.surname);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDBUser.addValueEventListener(vListener);
    }
    // Получаем fullName admin'a
    private void getFullNameAdmin() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idAdmin = mAuth.getUid();
                Log.d(TAG, "idAdmin: " + idAdmin);

                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    fullNameUser = (user.name + " " + user.surname);
                    Log.d(TAG, "fullNameAdmin: " + fullNameUser);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDBAdmin.addValueEventListener(vListener);
    }

    // Метод для зачеркивания текста в checkBox
    private void getCheckedCheckBox(CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {  // Устанавливаем флаг зачёркивания
                    buttonView.setPaintFlags(buttonView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {  // Убираем флаг зачёркивания
                    buttonView.setPaintFlags(buttonView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
    }

    // Обработчик Чек-боксов
    public void onCheckboxClicked(View view) {
        CheckBox checkBox = (CheckBox) view;
        boolean checked = checkBox.isChecked();
        switch (view.getId()) {
            case R.id.cbPar1:
                if (checked) {
                    cbPar1.setPaintFlags(cbPar1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Устанавливаем флаг зачёркивания
                    cbPar1.setTextColor(getResources().getColor(R.color.colorLightGray)); // Делаем текст серым
                } else {
                    cbPar1.setPaintFlags(cbPar1.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // Убираем флаг зачёркивания
                    cbPar1.setTextColor(getResources().getColor(R.color.colorBlack)); // Делаем текст черным
                }
                break;
            case R.id.cbPar2:
                if (checked) {
                    cbPar2.setPaintFlags(cbPar2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    cbPar2.setTextColor(getResources().getColor(R.color.colorLightGray));
                } else {
                    cbPar2.setPaintFlags(cbPar2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    cbPar2.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                break;
            case R.id.cbPar3:
                if (checked) {
                    cbPar3.setPaintFlags(cbPar3.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    cbPar3.setTextColor(getResources().getColor(R.color.colorLightGray));
                } else {
                    cbPar3.setPaintFlags(cbPar3.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    cbPar3.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                break;
            case R.id.cbPar4:
                if (checked) {
                    cbPar4.setPaintFlags(cbPar4.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    cbPar4.setTextColor(getResources().getColor(R.color.colorLightGray));
                } else {
                    cbPar4.setPaintFlags(cbPar4.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    cbPar4.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                break;
            case R.id.cbPar5:
                if (checked) {
                    cbPar5.setPaintFlags(cbPar5.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    cbPar5.setTextColor(getResources().getColor(R.color.colorLightGray));
                } else {
                    cbPar5.setPaintFlags(cbPar5.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    cbPar5.setTextColor(getResources().getColor(R.color.colorBlack));
                }
                break;
        }
    }

    // Метод для блокировки и разблокировки
    private void blockUnblock(Boolean aBoolean) {
        mView.setEnabled(aBoolean);
//        btSaveCheckListUser.setEnabled(aBoolean);
        ibAddPhoto.setEnabled(aBoolean);
        cbPar1.setEnabled(aBoolean);
        cbPar2.setEnabled(aBoolean);
        cbPar3.setEnabled(aBoolean);
        cbPar4.setEnabled(aBoolean);
        cbPar5.setEnabled(aBoolean);
    }
    // Метод для отчистки данных
    private void cleaning() {
        if (cbPar1.isChecked()){
            cbPar1.setChecked(false);
            cbPar1.setPaintFlags(cbPar1.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // Убираем флаг зачёркивания
            cbPar1.setTextColor(getResources().getColor(R.color.colorBlack)); // Делаем текст черным
        }
        if (cbPar2.isChecked()) {
            cbPar2.setChecked(false);
            cbPar2.setPaintFlags(cbPar2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // Убираем флаг зачёркивания
            cbPar2.setTextColor(getResources().getColor(R.color.colorBlack)); // Делаем текст черным
        }
        if (cbPar3.isChecked()) {
            cbPar3.setChecked(false);
            cbPar3.setPaintFlags(cbPar3.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // Убираем флаг зачёркивания
            cbPar3.setTextColor(getResources().getColor(R.color.colorBlack)); // Делаем текст черным
        }
        if (cbPar4.isChecked()) {
            cbPar4.setChecked(false);
            cbPar4.setPaintFlags(cbPar4.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // Убираем флаг зачёркивания
            cbPar4.setTextColor(getResources().getColor(R.color.colorBlack)); // Делаем текст черным
        }
        if (cbPar5.isChecked()) {
            cbPar5.setChecked(false);
            cbPar5.setPaintFlags(cbPar5.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // Убираем флаг зачёркивания
            cbPar5.setTextColor(getResources().getColor(R.color.colorBlack)); // Делаем текст черным
        }
        imageView.setVisibility(View.GONE);
        imageView.setImageDrawable(null);
        imageViewVisible = false;
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
                startActivity(new Intent(CheckListShowActivity.this, UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(CheckListShowActivity.this, SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(CheckListShowActivity.this, AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CheckListShowActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
