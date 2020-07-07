package com.daisybell.myapp.theory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.daisybell.myapp.Constant;
import com.daisybell.myapp.MainActivity;
import com.daisybell.myapp.R;
import com.daisybell.myapp.ViewPagerAdapter;
import com.daisybell.myapp.auth.LoginActivity;
import com.daisybell.myapp.menu.AboutApplicationActivity;
import com.daisybell.myapp.menu.SettingsActivity;
import com.daisybell.myapp.menu.UsersActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddTheoryActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private AddTheoryLetterFragment mAddTheoryLetterFragment;
    private AddTheoryPDFFragment mAddTheoryPDFFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_theory);

        mViewPager = findViewById(R.id.viewPager);
        mTabLayout = findViewById(R.id.tabLayout);

        mAddTheoryLetterFragment = new AddTheoryLetterFragment();
        mAddTheoryPDFFragment = new AddTheoryPDFFragment();

        mTabLayout.setupWithViewPager(mViewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(mAddTheoryLetterFragment, "Написать");
        viewPagerAdapter.addFragment(mAddTheoryPDFFragment, "Загрузить");
        mViewPager.setAdapter(viewPagerAdapter);

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_write);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_download);
    }

//    private class ViewPagerAdapter extends FragmentPagerAdapter {
//        private List<Fragment> mFragments = new ArrayList<>();
//        private List<String> fragmentTitle = new ArrayList<>();
//
//        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
//            super(fm, behavior);
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragments.add(fragment);
//            fragmentTitle.add(title);
//        }
//
//        @NonNull
//        @Override
//        public Fragment getItem(int position) {
//            return mFragments.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragments.size();
//        }
//
//        @Nullable
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return fragmentTitle.get(position);
//        }
//    }


    // Метод для отображения 3х точек на toolbar'e
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.support_menu, menu);
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
            case R.id.support_item:
                new AlertDialog.Builder(AddTheoryActivity.this)
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setTitle(R.string.support_title)
                        .setMessage(R.string.support_message)
                        .setPositiveButton("Понятно", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return true;
            case R.id.users: // Пользователи
                startActivity(new Intent(AddTheoryActivity.this, UsersActivity.class));
                return true;
            case R.id.settings: // Настройки
                startActivity(new Intent(AddTheoryActivity.this, SettingsActivity.class));
                return true;
            case R.id.about_application: // О приложении
                startActivity(new Intent(AddTheoryActivity.this, AboutApplicationActivity.class));
                return true;
            case R.id.sing_out: // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AddTheoryActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
