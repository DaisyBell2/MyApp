package com.daisybell.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;

public class LoadingDialog {

    private Activity mActivity;
    private AlertDialog mDialog;

    public LoadingDialog(Activity myActivity) {
        mActivity = myActivity;
    }
    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog, null));
        builder.setCancelable(false);

        mDialog = builder.create();
        mDialog.show();
    }
    public void dismissDialog() {
        mDialog.dismiss();
    }
}
