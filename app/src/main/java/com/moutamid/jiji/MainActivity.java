package com.moutamid.jiji;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.moutamid.jiji.authentication.RegistrationActivity;
import com.moutamid.jiji.databinding.ActivityMainBinding;
import com.moutamid.jiji.utils.Constants;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b.buyerBtn.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class)
                    .putExtra(Constants.PARAMS, Constants.TYPE_BUYER));
        });

        b.sellerBtn.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class)
                    .putExtra(Constants.PARAMS, Constants.TYPE_SELLER));
        });

    }
}