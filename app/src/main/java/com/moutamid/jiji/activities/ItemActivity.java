package com.moutamid.jiji.activities;

import static com.moutamid.jiji.utils.Stash.toast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.jiji.R;
import com.moutamid.jiji.databinding.ActivityItemBinding;
import com.moutamid.jiji.model.ProductModel;
import com.moutamid.jiji.model.UserModel;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

import java.util.ArrayList;

public class ItemActivity extends AppCompatActivity {
    private ActivityItemBinding b;

    ProductModel productModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        b = ActivityItemBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        productModel = (ProductModel) Stash.getObject(Constants.CURRENT_PRODUCT, ProductModel.class);

        ArrayList<SlideModel> arrayList = new ArrayList<>();
//        arrayList.add(new SlideModel("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/800px-Image_created_with_a_mobile_phone.png", "", ScaleTypes.CENTER_CROP));
        arrayList.add(new SlideModel(productModel.image1 == null ? "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/800px-Image_created_with_a_mobile_phone.png" : productModel.image1, "", ScaleTypes.CENTER_CROP));
        arrayList.add(new SlideModel(productModel.image2 == null ? "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/800px-Image_created_with_a_mobile_phone.png" : productModel.image2, "", ScaleTypes.CENTER_CROP));
        arrayList.add(new SlideModel(productModel.image3 == null ? "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/800px-Image_created_with_a_mobile_phone.png" : productModel.image3, "", ScaleTypes.CENTER_CROP));

        b.imageSlider.setImageList(arrayList);

        b.imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                toast(arrayList.get(i).getImageUrl());
            }
        });

        b.backBtn.setOnClickListener(view -> {
            finish();
        });

        if (Stash.getBoolean(productModel.pushKey))
            b.starImageview.setBackgroundResource(R.drawable.ic_baseline_star_24);
        else
            b.starImageview.setBackgroundResource(R.drawable.ic_baseline_star_border_24);

        getStarCount();

        b.starImageview.setOnClickListener(view -> {
            if (Stash.getBoolean(productModel.pushKey, false)) {
                Stash.put(productModel.pushKey, false);
                Constants.databaseReference()
                        .child(productModel.type)
                        .child(productModel.pushKey)
                        .child(Constants.STAR_COUNT)
                        .child(Constants.auth().getUid())
                        .removeValue();
                Constants.databaseReference()
                        .child(Constants.USERS)
                        .child(Constants.auth().getUid())
                        .child(Constants.STARS)
                        .child(productModel.pushKey)
                        .removeValue();

                b.starImageview.setImageResource(R.drawable.ic_baseline_star_border_24);
            } else {
                Stash.put(productModel.pushKey, true);
                Constants.databaseReference()
                        .child(productModel.type)
                        .child(productModel.pushKey)
                        .child(Constants.STAR_COUNT)
                        .child(Constants.auth().getUid())
                        .setValue(true);
                Constants.databaseReference()
                        .child(Constants.USERS)
                        .child(Constants.auth().getUid())
                        .child(Constants.STARS)
                        .child(productModel.pushKey)
                        .setValue(productModel);

                b.starImageview.setImageResource(R.drawable.ic_baseline_star_24);
            }
        });

        b.titleTv.setText(productModel.name);

        b.addressTv.setText(productModel.address);

        b.priceTv.setText("KSh " + productModel.price);

        b.postedBy.setText("Posted by: " + productModel.postedBy);

        b.makeAnOfferBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ItemActivity.this, ConversationActivity.class);
            intent.putExtra("first", true);
            intent.putExtra("name", productModel.postedBy);
            intent.putExtra("uid", productModel.uid);
            intent.putExtra(Constants.PARAMS, "I will pay: ");

            startActivity(intent);
        });

        b.callBtn.setOnClickListener(view -> {
            Intent intentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + productModel.number));
            startActivity(intentDial);
        });

        b.descTv.setText(productModel.description);

        b.startChatBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ItemActivity.this, ConversationActivity.class);
            intent.putExtra("first", true);
            intent.putExtra("name", productModel.postedBy);
            intent.putExtra("uid", productModel.uid);
            intent.putExtra(Constants.PARAMS, b.startMessageEt.getText().toString());

            startActivity(intent);
        });

        b.requestSellerToCallBack.setOnClickListener(view -> {
            UserModel userModel = (UserModel) Stash.getObject(Constants.CURRENT_USER_MODEL, UserModel.class);
            Intent intent = new Intent(ItemActivity.this, ConversationActivity.class);
            intent.putExtra("first", true);
            intent.putExtra("name", productModel.postedBy);
            intent.putExtra("uid", productModel.uid);
            intent.putExtra(Constants.PARAMS, "Please call me on my number: " + userModel.number);

            startActivity(intent);
        });

    }

    private void getStarCount() {
        Constants.databaseReference()
                .child(productModel.type)
                .child(productModel.pushKey)
                .child(Constants.STAR_COUNT)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            b.startCountTv.setText("" + snapshot.getChildrenCount());

                        } else b.startCountTv.setText("");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}