package com.moutamid.jiji.bottomnavigationactivity.ui.sell;

import static com.moutamid.jiji.utils.Stash.toast;

import com.moutamid.jiji.model.LatLng2;
import com.moutamid.jiji.model.UserModel;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

public class SellModel {
    private SellFragment fragment;

    public SellModel(SellFragment fragment) {
        this.fragment = fragment;
    }

    public boolean isEveryThingCompleted() {
        fragment.productModel.location = (LatLng2) Stash.getObject(Constants.USER_LOCATION, LatLng2.class);
        fragment.productModel.city = Stash.getCityName(fragment.productModel.location);
        fragment.productModel.address = Stash.getAddress(fragment.productModel.location);
        UserModel userModell = (UserModel) Stash.getObject(Constants.CURRENT_USER_MODEL, UserModel.class);

        fragment.productModel.number = userModell.number;
        fragment.productModel.uid = Constants.auth().getUid();

        if (fragment.productModel.type.equals(Constants.TYPE_PRODUCT))
            if (fragment.b.modelEt.getText().toString().isEmpty()) {
                toast("Model is empty!");
                return false;
            } else {
                fragment.productModel.model = fragment.b.modelEt.getText().toString();
            }
        if (fragment.productModel.type.equals(Constants.TYPE_PRODUCT))
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

        if (fragment.productModel.type.equals(Constants.TYPE_SERVICE)) {
            if (fragment.b.specialiazationEt.getText().toString().isEmpty()) {
                toast("Please enter a specialization!");
                return false;
            } else {
                fragment.productModel.category = fragment.b.specialiazationEt.getText().toString();
            }
        }
        if (fragment.productModel.type.equals(Constants.TYPE_PRODUCT))
            if (fragment.productModel.category == null) {
                toast("Please select a category!");
                return false;
            }

        fragment.productModel.image1 = "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg";
        fragment.productModel.image2 = "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg";
        fragment.productModel.image3 = "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg";

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
