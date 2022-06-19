package com.moutamid.jiji.bottomnavigationactivity.ui.sell;

import static android.app.Activity.RESULT_OK;
import static com.moutamid.jiji.utils.Stash.toast;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.moutamid.jiji.R;
import com.moutamid.jiji.databinding.FragmentSellBinding;
import com.moutamid.jiji.model.ProductModel;
import com.moutamid.jiji.model.UserModel;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

import java.util.ArrayList;
import java.util.List;

public class SellFragment extends Fragment {

    public FragmentSellBinding b;

    public SellController controller;
    private SellModel model;
    public UploadDocumentsController uploadDocumentsController;

    public ProductModel productModel = new ProductModel();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentSellBinding.inflate(inflater, container, false);
        View root = b.getRoot();
        controller = new SellController(this);
        model = new SellModel(this);
        uploadDocumentsController = new UploadDocumentsController(this, b);

        productModel.type = Constants.TYPE_PRODUCT;

        b.productButton.setOnClickListener(view -> {
            b.productButton.setBackgroundColor(getResources().getColor(R.color.default_green));
            b.servicesBtn.setBackgroundColor(getResources().getColor(R.color.darkerGrey));

            b.modelEt.setVisibility(View.VISIBLE);
            b.conditionBtn.setVisibility(View.VISIBLE);

            b.categoryBtn.setVisibility(View.VISIBLE);
            b.specialiazationEt.setVisibility(View.GONE);

            productModel.type = Constants.TYPE_PRODUCT;
        });

        b.servicesBtn.setOnClickListener(view -> {
            b.servicesBtn.setBackgroundColor(getResources().getColor(R.color.default_green));
            b.productButton.setBackgroundColor(getResources().getColor(R.color.darkerGrey));

            b.modelEt.setVisibility(View.GONE);
            b.conditionBtn.setVisibility(View.GONE);

            b.categoryBtn.setVisibility(View.GONE);
            b.specialiazationEt.setVisibility(View.VISIBLE);

            productModel.type = Constants.TYPE_SERVICE;
        });

        b.categoryBtn.setOnClickListener(view -> {
            AlertDialog dialog;

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            final CharSequence[] items;
            if (b.categoryText.getText().toString().equals("Category")) {
                items = new CharSequence[]{
                        "Toyota", "Nissan", "Honda", "Mazda", "Suzuki",
                        "BMW", "LEXUS", "AUDI", "Land rover", "Mercedes Benz", "Mitsubishi", "Isuzu", "Hino",
                        "Chevloret", "Volkswagen", "Jeep", "Subaru", "Porsche"};
            } else {
                items = new CharSequence[]{
                        "Specialization 1", "Specialization 2", "Specialization 3",
                        "Specialization 4", "Specialization 5"
                };
            }

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    productModel.category = items[position].toString();
                    b.categoryText.setText(items[position].toString());
                }
            });

            dialog = builder.create();
            dialog.show();
        });

        b.conditionBtn.setOnClickListener(view -> {
            AlertDialog dialog;

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            final CharSequence[] items;
            items = new CharSequence[]{"New", "Used"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    productModel.condition = items[position].toString();
                    b.conditionText.setText(items[position].toString());
                }
            });

            dialog = builder.create();
            dialog.show();
        });


        b.image1.setOnClickListener(view -> controller.startGalleryIntent(CODE_IMAGE_1));
        b.image2.setOnClickListener(view -> controller.startGalleryIntent(CODE_IMAGE_2));
        b.image3.setOnClickListener(view -> controller.startGalleryIntent(CODE_IMAGE_3));

        b.postBtn.setOnClickListener(view -> {
            if (model.isEveryThingCompleted()) {
                UserModel userModel = (UserModel) Stash.getObject(Constants.CURRENT_USER_MODEL, UserModel.class);
                productModel.postedBy = userModel.name;
                controller.progressDialog.show();
                Constants.databaseReference()
                        .child(productModel.type)
                        .push()
                        .setValue(productModel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                controller.progressDialog.dismiss();

                                if (task.isSuccessful()) {

                                    toast("Success!");
                                    requireActivity().recreate();
                                    b.conditionText.setText("Condition");
                                    b.nameEt.setText("");
                                    b.descriptionEt.setText("");
                                    b.priceEt.setText("");

                                } else toast(task.getException().getMessage());
                            }
                        });
            }
        });

        return root;
    }

    public int CODE_IMAGE_1 = 1234, CODE_IMAGE_2 = 1235, CODE_IMAGE_3 = 1236;
    List<String> imagesEncodedList;
    String imageEncoded;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() == null) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            imagesEncodedList = new ArrayList<String>();

            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                if (mClipData.getItemCount() != 3) {
                    toast("Please select 3 images only!");
                    return;
                }
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                    // Get the cursor
                    Cursor cursor = requireActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    imagesEncodedList.add(imageEncoded);
                    cursor.close();

                }
                controller.uploadImage(CODE_IMAGE_1, mArrayUri.get(0));
                controller.uploadImage(CODE_IMAGE_2, mArrayUri.get(1));
                controller.uploadImage(CODE_IMAGE_3, mArrayUri.get(2));
                Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                // UPLOAD IMAGES
            }

        } else {
            if (requestCode == uploadDocumentsController.CODE_ID_CARD || requestCode == uploadDocumentsController.CODE_TAX_CTF
                    || requestCode == uploadDocumentsController.CODE_REGISTRATION_CTF && resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.getData() != null) {
                        Uri imageUri = data.getData();
                        uploadDocumentsController.uploadDocImage(requestCode, imageUri);
                    }
                }
            }
            if (requestCode == CODE_IMAGE_1 || requestCode == CODE_IMAGE_2
                    || requestCode == CODE_IMAGE_3 && resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                controller.uploadImage(requestCode, imageUri);
            }
        }
    }
}