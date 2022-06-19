package com.moutamid.jiji.bottomnavigationactivity.ui.sell;

import static com.moutamid.jiji.utils.Stash.toast;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moutamid.jiji.R;
import com.moutamid.jiji.databinding.FragmentSellBinding;
import com.moutamid.jiji.model.UserModel;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

public class UploadDocumentsController {
    public SellFragment fragment;
    public FragmentSellBinding b;

    public int CODE_ID_CARD = 1234;
    public int CODE_TAX_CTF = 1235;
    public int CODE_REGISTRATION_CTF = 1236;

    public String id_card_link = Constants.NULL;
    public String tax_certificate_link = Constants.NULL;
    public String registration_certificate_link = Constants.NULL;

    public UploadDocumentsController(SellFragment fragment, FragmentSellBinding b) {
        this.fragment = fragment;
        this.b = b;
        progressDialog = new ProgressDialog(fragment.requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
    }

    public void showDocumentDialog() {
        Dialog dialog = new Dialog(fragment.requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_upload_documents_layout);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.findViewById(R.id.doneBtn).setOnClickListener(view -> {
            if (id_card_link.equals(Constants.NULL)){
                toast("Please upload is card");
                return;
            }
            if (tax_certificate_link.equals(Constants.NULL)){
                toast("Please upload is tax certificate");
                return;
            }

            UserModel userModell = (UserModel) Stash.getObject(Constants.CURRENT_USER_MODEL, UserModel.class);

            userModell.id_card_link = id_card_link;
            userModell.tax_certificate_link = tax_certificate_link;
            userModell.registration_certificate_link = registration_certificate_link;

            Stash.put(Constants.CURRENT_USER_MODEL, userModell);

            Constants.databaseReference()
                    .child(Constants.USERS)
                    .child(Constants.auth().getUid())
                    .child("id_card_link")
                    .setValue(id_card_link);

            Constants.databaseReference()
                    .child(Constants.USERS)
                    .child(Constants.auth().getUid())
                    .child("tax_certificate_link")
                    .setValue(tax_certificate_link);

            Constants.databaseReference()
                    .child(Constants.USERS)
                    .child(Constants.auth().getUid())
                    .child("registration_certificate_link")
                    .setValue(registration_certificate_link);

            toast("Success");
            dialog.dismiss();
        });

        dialog.findViewById(R.id.crossBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        idCardTextView = dialog.findViewById(R.id.idCardTextView);
        uploadTaxCertificateText = dialog.findViewById(R.id.uploadTaxCertificateText);
        uploadRegistrationCertificateText = dialog.findViewById(R.id.uploadRegistrationCertificateText);

        dialog.findViewById(R.id.uploadIdCardBtn).setOnClickListener(view -> {
            startGalleryIntent(CODE_ID_CARD);
        });
        dialog.findViewById(R.id.uploadTaxCertificateBtn).setOnClickListener(view -> {
            startGalleryIntent(CODE_TAX_CTF);
        });
        dialog.findViewById(R.id.uploadRegistrationCertificateBtn).setOnClickListener(view -> {
            startGalleryIntent(CODE_REGISTRATION_CTF);
        });

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    TextView idCardTextView;
    TextView uploadTaxCertificateText;
    TextView uploadRegistrationCertificateText;

    public void startGalleryIntent(int CODE) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        fragment.startActivityForResult(galleryIntent, CODE);
    }
    private ProgressDialog progressDialog;

    public void uploadDocImage(int requestCode, Uri imageUri) {
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("documents_path");

        final StorageReference filePath = storageReference
                .child(System.currentTimeMillis() + imageUri.getLastPathSegment());

        filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                filePath.getDownloadUrl().addOnSuccessListener(photoUrl -> {
                    progressDialog.dismiss();

                    if (requestCode == CODE_ID_CARD) {
                        idCardTextView.setText(imageUri.getLastPathSegment());
                        id_card_link = photoUrl.toString();
                    }

                    if (requestCode == CODE_TAX_CTF) {
                        uploadTaxCertificateText.setText(imageUri.getLastPathSegment());
                        tax_certificate_link = photoUrl.toString();
                    }

                    if (requestCode == CODE_REGISTRATION_CTF) {
                        uploadRegistrationCertificateText.setText(imageUri.getLastPathSegment());
                        registration_certificate_link = photoUrl.toString();
                    }

                    toast("Upload done!");

                })
        ).addOnFailureListener(e -> {
            progressDialog.dismiss();

            toast(e.getMessage());

        });
    }
}