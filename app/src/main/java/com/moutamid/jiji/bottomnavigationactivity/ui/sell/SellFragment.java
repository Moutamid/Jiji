package com.moutamid.jiji.bottomnavigationactivity.ui.sell;

import static android.app.Activity.RESULT_OK;

import static com.moutamid.jiji.utils.Stash.toast;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.moutamid.jiji.utils.Constants;

public class SellFragment extends Fragment {

    public FragmentSellBinding b;

    public SellController controller;
    private SellModel model;

    public ProductModel productModel = new ProductModel();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentSellBinding.inflate(inflater, container, false);
        View root = b.getRoot();
        controller = new SellController(this);
        model = new SellModel(this);

        productModel.type = Constants.TYPE_PRODUCT;

        b.productButton.setOnClickListener(view -> {
            b.productButton.setBackgroundColor(getResources().getColor(R.color.default_green));
            b.servicesBtn.setBackgroundColor(getResources().getColor(R.color.darkerGrey));

            productModel.type = Constants.TYPE_PRODUCT;
        });

        b.servicesBtn.setOnClickListener(view -> {
            b.servicesBtn.setBackgroundColor(getResources().getColor(R.color.default_green));
            b.productButton.setBackgroundColor(getResources().getColor(R.color.darkerGrey));

            productModel.type = Constants.TYPE_SERVICE;

        });

        b.categoryBtn.setOnClickListener(view -> {
            AlertDialog dialog;

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            final CharSequence[] items = {"NA", "NI", "NO", "NR", "NB", "CB", "CDBP", "AM", "WN", "SOSO"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    productModel.category = items[position].toString();
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

                                } else toast(task.getException().getMessage());
                            }
                        });
            }
        });

        return root;
    }

    public int CODE_IMAGE_1 = 1234, CODE_IMAGE_2 = 1235, CODE_IMAGE_3 = 1236;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_IMAGE_1 || requestCode == CODE_IMAGE_2
                || requestCode == CODE_IMAGE_3 && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            controller.uploadImage(requestCode, imageUri);
        }
    }
}