package com.moutamid.jiji.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

import com.moutamid.jiji.MainActivity;
import com.moutamid.jiji.R;
import com.moutamid.jiji.utils.Constants;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_home);

        if (Constants.auth().getCurrentUser() == null){
            finish();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }

    }
}