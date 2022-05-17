package com.moutamid.jiji.bottomnavigationactivity.ui.sell;

import static com.moutamid.jiji.utils.Stash.toast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SellController {
    SellFragment fragment;
    public ProgressDialog progressDialog;

    public SellController(SellFragment fragment) {
        this.fragment = fragment;
        progressDialog = new ProgressDialog(fragment.requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
    }

    public void startGalleryIntent(int CODE) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        fragment.startActivityForResult(galleryIntent, CODE);
    }

    public void uploadImage(int requestCode, Uri imageUri) {
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("sells_path");

        final StorageReference filePath = storageReference
                .child(System.currentTimeMillis() + imageUri.getLastPathSegment());

        filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                filePath.getDownloadUrl().addOnSuccessListener(photoUrl -> {
                    progressDialog.dismiss();

                    if (requestCode == fragment.CODE_IMAGE_1) {
                        fragment.b.image1.setImageURI(imageUri);
                        fragment.productModel.image1 = photoUrl.toString();
                    }

                    if (requestCode == fragment.CODE_IMAGE_2) {
                        fragment.b.image2.setImageURI(imageUri);
                        fragment.productModel.image2 = photoUrl.toString();
                    }

                    if (requestCode == fragment.CODE_IMAGE_3) {
                        fragment.b.image3.setImageURI(imageUri);
                        fragment.productModel.image3 = photoUrl.toString();
                    }

                    toast("Upload done!");

                })
        ).addOnFailureListener(e -> {
            progressDialog.dismiss();
            toast(e.getMessage());
        });
    }

}
