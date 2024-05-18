package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTabLayout();


    }


    @Override
    protected void onResume() {
        super.onResume();
        Weather.updateAllWidgets(getApplicationContext(), R.layout.weather, Weather.class);
    }

    // Метод для перехода на вкладку авторизации
    public void toLogin(){
        viewPager.setCurrentItem(1);
    }

    // Метод для настройки вкладок
    private void setTabLayout(){
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Создание адаптера вкладок
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        // Добавление фрагментов во вкладки
        pagerAdapter.addFragment(new fragmentRegistration(), "Регистрация");
        pagerAdapter.addFragment(new FragmentLogin() , "Авторизация");

        // Установка адаптера вкладок для ViewPager
        viewPager.setAdapter(pagerAdapter);

        // Связывание TabLayout и ViewPager
        tabLayout.setupWithViewPager(viewPager);
    }
}
