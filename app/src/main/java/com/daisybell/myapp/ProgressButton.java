package com.daisybell.myapp;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ProgressButton {

    private CardView mCardView;
    private ConstraintLayout mLayout;
    private ProgressBar mProgressBar;
    private TextView mTextView;

    Animation fade_in;

    public ProgressButton(Context ct, View view) {

        fade_in = AnimationUtils.loadAnimation(ct, R.anim.fade_in);

        mCardView = view.findViewById(R.id.cardView);
        mLayout = view.findViewById(R.id.constraint_layout);
        mProgressBar = view.findViewById(R.id.progressBar);
        mTextView = view.findViewById(R.id.textView);
    }
    public void buttonActivated() {
        mProgressBar.setAnimation(fade_in);
        mLayout.setBackgroundColor(mCardView.getResources().getColor(R.color.colorButton));
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setAnimation(fade_in);
        mTextView.setText("Подождите...");
    }
    public void buttonFinished() {
        mLayout.setBackgroundColor(mCardView.getResources().getColor(R.color.colorGreen));
        mProgressBar.setVisibility(View.GONE);
        mTextView.setText("Сохранено");
    }
}
