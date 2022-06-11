package com.moutamid.jiji.activities;

import static com.moutamid.jiji.utils.Stash.toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.moutamid.jiji.R;
import com.moutamid.jiji.bottomnavigationactivity.BottomNavigationActivity;
import com.moutamid.jiji.databinding.ActivityVerifyEmailBinding;
import com.moutamid.jiji.utils.Constants;

public class VerifyEmailActivity extends AppCompatActivity {
    private ActivityVerifyEmailBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityVerifyEmailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Constants.auth().getCurrentUser().sendEmailVerification();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        b.desc.setText("We sent a verification email to " +
                getIntent().getStringExtra(Constants.PARAMS) +
                ". If not found, you can check your spam folder also and then verify your email to unlock the app or change your email instead.");

        b.backBtn.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure?")
                    .setMessage("Do you really want to change your email?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        deleteUser();
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .show();
        });

        b.changeEmailBtn.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure?")
                    .setMessage("Do you really want to change your email?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        deleteUser();
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .show();
        });

        b.checkBtn.setOnClickListener(view -> {
            progressDialog.show();
            Constants.auth().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (Constants.auth().getCurrentUser().isEmailVerified()) {
                        Toast.makeText(VerifyEmailActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerifyEmailActivity.this, BottomNavigationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(VerifyEmailActivity.this, "Email is not verified!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private ProgressDialog progressDialog;

    private void deleteUser() {
        progressDialog.show();

        Constants.databaseReference()
                .child(Constants.USERS)
                .child(Constants.auth().getUid())
                .removeValue();

        Constants.auth().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                finish();
            }
        });
    }

    boolean bbb = false;

    @Override
    public void onBackPressed() {
        if (bbb)
            super.onBackPressed();
    }
}