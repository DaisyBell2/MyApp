package com.daisybell.myapp.result;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.check_list.SaveResultCheckList;
import com.daisybell.myapp.menu.AboutApplicationActivity;
import com.daisybell.myapp.menu.SettingsActivity;
import com.daisybell.myapp.menu.UsersActivity;
import com.daisybell.myapp.theory.Theory;
import com.daisybell.myapp.user_email.AddNewUserActivity;
import com.google.firebase.auth.FirebaseAuth;

import pl.droidsonroids.gif.GifImageView;

public class ResultCheckListActivity extends AppCompatActivity {

    private static String TAG = "ResCheck";

    private TextView tvFullName, tvNameCL, tvDate, tvTime, tvDone, tvNotDone;
    private GifImageView ivPhoto;
    private Uri photoUri;

    private SaveResultCheckList mSaveResultCheckList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_check_list);

        init();
        getIntentMain();
    }

    private void init() {
        tvFullName = findViewById(R.id.tvFullName);
        tvNameCL = findViewById(R.id.tvNameCL);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvDone = findViewById(R.id.tvDone);
        tvNotDone = findViewById(R.id.tvNotDone);
        ivPhoto = findViewById(R.id.ivPhoto);
    }

    private void getIntentMain() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mSaveResultCheckList = (SaveResultCheckList) intent.getSerializableExtra("item");

            if (mSaveResultCheckList != null) {
                tvFullName.setText(mSaveResultCheckList.getFullNameUser());
                tvNameCL.setText(mSaveResultCheckList.getNameCheckList());
                tvDate.setText(mSaveResultCheckList.getDate());
                tvTime.setText(mSaveResultCheckList.getTime());
                tvDone.setText(mSaveResultCheckList.getDoneCheckList());
                tvNotDone.setText(mSaveResultCheckList.getNotDoneCheckList());
                photoUri = Uri.parse(mSaveResultCheckList.getPhotoUri());
                Glide.with(this)
                    .load(photoUri)
                    .placeholder(R.drawable.loading_3)
                    .into(ivPhoto);
            }
        }

//        Intent intent = getIntent();
//        if (intent != null) {
//            tvFullName.setText(intent.getStringExtra(Constant.RESULT_CHECK_LIST_NAME));
//            tvNameCL.setText(intent.getStringExtra(Constant.RESULT_CHECK_LIST_NAME_CL));
//            tvDate.setText(intent.getStringExtra(Constant.RESULT_CHECK_LIST_DATE));
//            tvTime.setText(intent.getStringExtra(Constant.RESULT_CHECK_LIST_TIME));
//            tvDone.setText(intent.getStringExtra(Constant.RESULT_CHECK_LIST_DONE));
//            tvNotDone.setText(intent.getStringExtra(Constant.RESULT_CHECK_LIST_NOT_DONE));
//            photoUri = Uri.parse(intent.getStringExtra(Constant.RESULT_CHECK_LIST_PHOTO_URI));
//            Log.d(TAG, "photoUri: " + photoUri);
//            Glide.with(this)
//                    .load(photoUri)
//                    .placeholder(R.drawable.loading_3)
//                    .into(ivPhoto);
//        }
    }
    public void onClickBackToHome(View view) {
        Intent intent = new Intent(ResultCheckListActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
                startActivity(new Intent(ResultCheckListActivity.this, UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(ResultCheckListActivity.this, SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(ResultCheckListActivity.this, AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ResultCheckListActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
