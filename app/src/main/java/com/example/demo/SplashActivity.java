package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by LiShen on 2019/3/15 14:39
 * Project: ProjectBaseDiffAdapter
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnSplashGO) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}