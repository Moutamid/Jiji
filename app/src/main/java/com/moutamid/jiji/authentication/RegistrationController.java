package com.moutamid.jiji.authentication;

import static com.moutamid.jiji.utils.Stash.toast;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moutamid.jiji.bottomnavigationactivity.BottomNavigationActivity;
import com.moutamid.jiji.bottomnavigationactivity.HomeActivity;
import com.moutamid.jiji.model.UserModel;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

public class RegistrationController {
    private RegistrationActivity activity;
    private RegistrationModel model;

    public RegistrationController(RegistrationActivity activity) {
        this.activity = activity;
        model = new RegistrationModel(activity);
    }


    public void uploadImage(int requestCode, Uri imageUri) {
        activity.progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("documents_path");

        final StorageReference filePath = storageReference
                .child(System.currentTimeMillis() + imageUri.getLastPathSegment());

        filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                filePath.getDownloadUrl().addOnSuccessListener(photoUrl -> {
                    activity.progressDialog.dismiss();

                    if (requestCode == activity.CODE_ID_CARD) {
                        activity.b.idCardTextView.setText(imageUri.getLastPathSegment());
                        activity.userModel.id_card_link = photoUrl.toString();
                    }

                    if (requestCode == activity.CODE_TAX_CTF) {
                        activity.b.uploadTaxCertificateText.setText(imageUri.getLastPathSegment());
                        activity.userModel.tax_certificate_link = photoUrl.toString();
                    }

                    if (requestCode == activity.CODE_REGISTRATION_CTF) {
                        activity.b.uploadRegistrationCertificateText.setText(imageUri.getLastPathSegment());
                        activity.userModel.registration_certificate_link = photoUrl.toString();
                    }

                    toast("Upload done!");

                })
        ).addOnFailureListener(e -> {
            activity.progressDialog.dismiss();

            toast(e.getMessage());

        });
    }

    public void startGalleryIntent(int CODE) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        activity.startActivityForResult(galleryIntent, CODE);
    }

    public void signUp() {
        String email = activity.b.emailEditText.getText().toString();
        String password = activity.b.passwordEditText.getText().toString();

        Constants.auth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            model.uploadUserDetails();
                        } else {
                            activity.progressDialog.dismiss();
                            toast(task.getException().getMessage());
                        }

                    }
                });
    }

    public void login() {
        String email = activity.b.emailEditText.getText().toString();
        String password = activity.b.passwordEditText.getText().toString();

        Constants.auth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Constants.databaseReference()
                                    .child(Constants.USERS)
                                    .child(Constants.auth().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            activity.progressDialog.dismiss();
                                            if (snapshot.exists()) {
                                                toast("Login success!");
                                                UserModel userModell = snapshot.getValue(UserModel.class);
                                                Stash.put(Constants.CURRENT_USER_MODEL, userModell);

                                                activity.startActivity(new Intent(activity, BottomNavigationActivity.class));
                                                activity.finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        } else {
                            toast(task.getException().getMessage());
                        }
                    }
                });

    }
}
