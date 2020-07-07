package com.daisybell.myapp.theory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.R;
import com.daisybell.myapp.user_email.AddNewUserActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

public class AddTheoryPDFFragment extends Fragment {

    private static final String TAG = "myLog";

    private EditText etNamePDFFile;
    private Button btSavePDFFile;

    private StorageReference mStorageReference;
    private DatabaseReference mDataBase;

    private String namePDFFile;

    public AddTheoryPDFFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Constant.ADMIN_ID = preferences.getString(Constant.ADMIN_ID_INDEX, "");

        View v = inflater.inflate(R.layout.fragment_add_theory_pdf, container, false);
        etNamePDFFile = v.findViewById(R.id.etNamePDFFile);
        btSavePDFFile = v.findViewById(R.id.btSavePDFFile);

        mStorageReference = FirebaseStorage.getInstance().getReference(Constant.PDF_FILE_STORAGE_KEY);
        mDataBase = FirebaseDatabase.getInstance().getReference(Constant.ADMIN_KEY +"_"+ Constant.ADMIN_ID).child(Constant.PDF_FILE_KEY);

        btSavePDFFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                namePDFFile = etNamePDFFile.getText().toString().trim();
                if (!TextUtils.isEmpty(namePDFFile)) {
                    selectPDFFile();
                } else {
                    Toast.makeText(getContext(), "Вы не ввели название", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private void selectPDFFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            uploadPDFFile(data.getData());
        }
    }

    private void uploadPDFFile(Uri data) {

        final String namePDFStorage = System.currentTimeMillis() + ".pdf";
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Загрузка...");
        progressDialog.show();

        StorageReference reference = mStorageReference.child(namePDFStorage);
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete());
                        Uri url = uri.getResult();

                        UploadPDF uploadPDF = new UploadPDF(namePDFFile, url.toString(), namePDFStorage);
                        mDataBase.push().setValue(uploadPDF);

                        etNamePDFFile.setText("");
                        progressDialog.dismiss();

                        //Initialize alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setCancelable(false);
                        builder.setTitle(Html.fromHtml("Успешно"));
                        builder.setMessage("Файл успешно загружен");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        //Show alert dialog
                        builder.show();
//                        Toast.makeText(getContext(), "Файл загружен", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0*taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                progressDialog.setMessage("Загружено: " + (int)progress +"%");

            }
        });

    }
}