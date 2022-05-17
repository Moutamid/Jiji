package com.moutamid.jiji.bottomnavigationactivity.ui.sell;

import static com.moutamid.jiji.utils.Stash.toast;

import com.google.android.gms.maps.model.LatLng;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

public class SellModel {
    private SellFragment fragment;

    public SellModel(SellFragment fragment) {
        this.fragment = fragment;
    }

    public boolean isEveryThingCompleted() {
        fragment.productModel.location = (LatLng) Stash.getObject(Constants.USER_LOCATION, LatLng.class);
        fragment.productModel.number = Stash.getString(Constants.USER_NUMBER);
        fragment.productModel.uid = Constants.auth().getUid();

        if (fragment.b.conditionEt.getText().toString().isEmpty()) {
            toast("Condition is empty!");
            return false;
        } else {
            fragment.productModel.condition = fragment.b.conditionEt.getText().toString();
        }
        if (fragment.b.nameEt.getText().toString().isEmpty()) {
            toast("Name is empty!");
            return false;
        } else {
            fragment.productModel.name = fragment.b.nameEt.getText().toString();
        }
        if (fragment.b.descriptionEt.getText().toString().isEmpty()) {
            toast("Description is empty!");
            return false;
        } else {
            fragment.productModel.description = fragment.b.descriptionEt.getText().toString();
        }
        if (fragment.b.priceEt.getText().toString().isEmpty()) {
            toast("Price is empty!");
            return false;
        } else {
            fragment.productModel.price = fragment.b.priceEt.getText().toString();
        }

        if (fragment.productModel.category == null) {
            toast("Please select a category!");
            return false;
        }
        if (fragment.productModel.image1 == null) {
            toast("Please upload image 1!");
            return false;
        }
        if (fragment.productModel.image2 == null) {
            toast("Please upload image 2!");
            return false;
        }
        if (fragment.productModel.image3 == null) {
            toast("Please upload image 3!");
            return false;
        }

        return true;
    }

}
