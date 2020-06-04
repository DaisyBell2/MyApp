package com.daisybell.myapp.check_list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
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
import com.daisybell.myapp.ProgressButton;
import com.daisybell.myapp.R;
import com.daisybell.myapp.auth.User;
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
    private DatabaseReference mDataBase;
    private FirebaseAuth mAuth;

    private String idUser;
    private String fullNameUser;
    private String doneCheckList = "";
    private String notDoneCheckList = "";

    private boolean imageViewVisible = false;

    private View mView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_show);
        setTitle("Чек-лист");

        init();
        getIntentMain();
        getFullNameUser();

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
                                    Log.d(TAG, "downloadPhotoURI: " + downloadPhotoURI);
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
                                                    dateText, timeText, doneCheckList, notDoneCheckList);
                                            mDataBase.push().setValue(newResultCheckList);
//                                    Toast.makeText(CheckListShowActivity.this, "Все данные сохранены!", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Все данные сохранены в firebase");

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
        mDBUser = FirebaseDatabase.getInstance().getReference(Constant.USER_KEY);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.RESULT_CHECK_LIST_KEY);
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

    public void onClickSaveCheckListUser(View view) {
        if (imageViewVisible) {
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
                            Log.d(TAG, "downloadPhotoURI: " + downloadPhotoURI);
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
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() { //
                            String nameCheckList = tvCheckListTitle.getText().toString() + " / " + tvCheckListPoint.getText().toString();
                            SaveResultCheckList newResultCheckList = new SaveResultCheckList(idUser, fullNameUser, nameCheckList, downloadPhotoURI,
                                    dateText, timeText, doneCheckList, notDoneCheckList);
                            mDataBase.push().setValue(newResultCheckList);
                            Toast.makeText(CheckListShowActivity.this, "Все данные сохранены!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Все данные сохранены в firebase");

                            ////////////////////////////////////
                            doneCheckList = "";
                            notDoneCheckList = "";
                            cleaning();
                            blockUnblock(true);

                            finish();
                        }
                    }, 800);



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CheckListShowActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Ошибка: " + e);
                }
            });


        } else {
            Toast.makeText(this, "Вы не сделали фотографию рабочего места!", Toast.LENGTH_SHORT).show();
        }
    }
    // Отбработка кнопки "Сохранить"

//    // Обработчик ImageView, для увеличения картинки по нажатию
//    public void onClickImageScaled(View view) {
//        if (!isImageScaled) view.animate().scaleX(1.4f).scaleY(1.4f).setDuration(500);
//        if (isImageScaled) view.animate().scaleX(1f).scaleY(1f).setDuration(500);
//        isImageScaled = !isImageScaled;
//    }

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
}
