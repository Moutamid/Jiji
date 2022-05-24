package com.moutamid.jiji.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.moutamid.jiji.R;
import com.moutamid.jiji.databinding.ActivityItemBinding;

import java.util.ArrayList;

public class ItemActivity extends AppCompatActivity {
    private ActivityItemBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityItemBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        ArrayList<SlideModel> arrayList = new ArrayList<>();
        arrayList.add(new SlideModel("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/800px-Image_created_with_a_mobile_phone.png", "", ScaleTypes.CENTER_CROP));
        arrayList.add(new SlideModel("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/800px-Image_created_with_a_mobile_phone.png", "", ScaleTypes.CENTER_CROP));
        arrayList.add(new SlideModel("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/800px-Image_created_with_a_mobile_phone.png", "", ScaleTypes.CENTER_CROP));
        arrayList.add(new SlideModel("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/800px-Image_created_with_a_mobile_phone.png", "", ScaleTypes.CENTER_CROP));
        arrayList.add(new SlideModel("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/800px-Image_created_with_a_mobile_phone.png", "", ScaleTypes.CENTER_CROP));

        b.imageSlider.setImageList(arrayList);

        b.imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {

            }
        });

    }
}