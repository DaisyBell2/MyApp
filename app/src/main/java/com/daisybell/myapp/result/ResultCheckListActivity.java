package com.daisybell.myapp.result;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.check_list.SaveResultCheckList;
import com.daisybell.myapp.theory.Theory;

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
        startActivity(new Intent(ResultCheckListActivity.this, MainActivity.class));
        finish();
    }
}
