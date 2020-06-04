package com.daisybell.myapp.theory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.R;

public class TheoryShowActivity extends AppCompatActivity {

    private TextView mTvTheoryShowTitle, mTvTheoryShowText;
    private ScrollView mScrollView;
    private Theory mTheory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory_show);
        setTitle("Теория");

        init();
        getIntentMain();
    }
    private void init() {
        mTvTheoryShowTitle = findViewById(R.id.tvTheoryShowTitle);
        mTvTheoryShowText = findViewById(R.id.tvTheoryShowText);
        mScrollView = findViewById(R.id.scrollView);
    }
    private void getIntentMain() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mTheory = (Theory) intent.getSerializableExtra("item");

            if (mTheory != null) {
                mTvTheoryShowTitle.setText(mTheory.getTitle());
                mTvTheoryShowText.setText(mTheory.getText());
            }
        }
//        Intent intent = getIntent();
//        if (intent != null) {
//            mTvTheoryShowTitle.setText(intent.getStringExtra(Constant.THEORY_TITLE));
//            mTvTheoryShowText.setText(intent.getStringExtra(Constant.THEORY_TEXT));
//        }
    }

    // Делаем поиск в меню
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//
//        MenuItem menuItem = menu.findItem(R.id.search_view);
//        SearchView searchView = (SearchView) menuItem.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Layout layout = mTvTheoryShowText.getLayout();
//                mScrollView.scrollTo(0, layout.getLineTop(layout.getLineForOffset(startPos)));
//                return true;
//            }
//        });
//
//        return true;
//    }
}
