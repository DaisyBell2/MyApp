package com.daisybell.myapp.theory;

import androidx.annotation.NonNull;
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
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.menu.AboutApplicationActivity;
import com.daisybell.myapp.menu.SettingsActivity;
import com.daisybell.myapp.menu.UsersActivity;
import com.google.firebase.auth.FirebaseAuth;

public class TheoryShowActivity extends AppCompatActivity {

    private TextView mTvTheoryShowTitle, mTvTheoryShowText;
    private ScrollView mScrollView;
    private Theory mTheory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory_show);

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
                startActivity(new Intent(TheoryShowActivity.this, UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(TheoryShowActivity.this, SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(TheoryShowActivity.this, AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(TheoryShowActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
