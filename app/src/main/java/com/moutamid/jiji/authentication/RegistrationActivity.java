package com.moutamid.jiji.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.jiji.R;
import com.moutamid.jiji.model.UserModel;
import com.moutamid.jiji.utils.Constants;

public class RegistrationActivity extends AppCompatActivity {

    public com.moutamid.jiji.databinding.ActivityRegistrationBinding b;

    public UserModel userModel = new UserModel();
    public RegistrationModel model;
    public String REGISTER_TYPE = Constants.SIGN_UP;

    public int CODE_ID_CARD = 1234;
    public int CODE_TAX_CTF = 1235;
    public int CODE_REGISTRATION_CTF = 1236;

    public RegistrationController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = com.moutamid.jiji.databinding.ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        controller = new RegistrationController(this);
        model = new RegistrationModel(this);

        userModel.user_type = getIntent().getStringExtra(Constants.PARAMS);

        if (userModel.user_type.equals(Constants.TYPE_BUYER)) {
            b.sellerLayout.setVisibility(View.GONE);
        }

        b.loginLayout.setOnClickListener(view -> {
            REGISTER_TYPE = Constants.LOGIN;

            b.loginLayout.setBackgroundColor(getResources().getColor(R.color.default_green));
            b.loginTextView.setTextColor(getResources().getColor(R.color.white));

            b.signUpLayout.setBackgroundColor(getResources().getColor(R.color.grey));
            b.signUpTextView.setTextColor(getResources().getColor(R.color.default_green));

            b.nameEditText.setVisibility(View.GONE);
            b.numberEditText.setVisibility(View.GONE);
            b.sellerLayout.setVisibility(View.GONE);

        });

        b.signUpLayout.setOnClickListener(view -> {
            REGISTER_TYPE = Constants.SIGN_UP;

            b.signUpLayout.setBackgroundColor(getResources().getColor(R.color.default_green));
            b.signUpTextView.setTextColor(getResources().getColor(R.color.white));

            b.loginLayout.setBackgroundColor(getResources().getColor(R.color.grey));
            b.loginTextView.setTextColor(getResources().getColor(R.color.default_green));

            b.nameEditText.setVisibility(View.VISIBLE);
            b.numberEditText.setVisibility(View.VISIBLE);

            if (userModel.user_type.equals(Constants.TYPE_SELLER)) {
                b.sellerLayout.setVisibility(View.VISIBLE);
            }

        });

        b.uploadIdCardBtn.setOnClickListener(view -> {
            controller.startGalleryIntent(CODE_ID_CARD);
        });
        b.uploadTaxCertificateBtn.setOnClickListener(view -> {
            controller.startGalleryIntent(CODE_TAX_CTF);
        });
        b.uploadRegistrationCertificateBtn.setOnClickListener(view -> {
            controller.startGalleryIntent(CODE_REGISTRATION_CTF);
        });

        b.doneBtn.setOnClickListener(view -> {
            if (model.isEveryThingCompleted()) {
                model.getUserLocation();
            }
        });

    }

    public ProgressDialog progressDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_ID_CARD || requestCode == CODE_TAX_CTF
                || requestCode == CODE_REGISTRATION_CTF && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            controller.uploadImage(requestCode, imageUri);
        }
    }
}