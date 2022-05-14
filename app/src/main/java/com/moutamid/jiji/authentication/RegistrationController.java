package com.moutamid.jiji.authentication;

import static com.moutamid.jiji.utils.Stash.toast;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moutamid.jiji.utils.Constants;

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
                .child(Constants.auth().getCurrentUser().getUid() + imageUri.getLastPathSegment());

        filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                filePath.getDownloadUrl().addOnSuccessListener(photoUrl -> {
                    activity.progressDialog.dismiss();

                    if (requestCode == activity.CODE_ID_CARD)
                        activity.userModel.id_card_link = photoUrl.toString();

                    if (requestCode == activity.CODE_TAX_CTF)
                        activity.userModel.tax_certificate_link = photoUrl.toString();

                    if (requestCode == activity.CODE_REGISTRATION_CTF)
                        activity.userModel.registration_certificate_link = photoUrl.toString();

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
        activity.progressDialog.show();
        String email = activity.b.emailEditText.getText().toString();
        String password = activity.b.passwordEditText.getText().toString();

        Constants.auth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            model.uploadUserDetails();
                        }else {
                            activity.progressDialog.dismiss();
                            toast(task.getException().getMessage());
                        }

                    }
                });
    }
}
