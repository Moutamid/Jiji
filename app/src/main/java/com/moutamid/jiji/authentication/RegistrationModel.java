package com.moutamid.jiji.authentication;

import static com.moutamid.jiji.utils.Stash.toast;

import com.moutamid.jiji.utils.Constants;

public class RegistrationModel {

    private RegistrationActivity activity;

    public RegistrationModel(RegistrationActivity activity) {
        this.activity = activity;
    }

    public boolean isEveryThingCompleted() {
        if (activity.b.emailEditText.getText().toString().isEmpty()) {
            toast("Email is empty!");
            return false;
        }
        if (activity.b.passwordEditText.getText().toString().isEmpty()) {
            toast("Password is empty!");
            return false;
        }

        if (activity.REGISTER_TYPE.equals(Constants.SIGN_UP)) {

            if (activity.b.nameEditText.getText().toString().isEmpty()) {
                toast("Name is empty!");
                return false;
            }

            if (activity.b.numberEditText.getText().toString().isEmpty()) {
                toast("Number is empty!");
                return false;
            }

            if (activity.userModel.user_type.equals(Constants.TYPE_SELLER)) {
                if (activity.userModel.id_card_link == null) {
                    toast("Please upload a id card!");
                    return false;
                }
                if (activity.userModel.tax_certificate_link == null) {
                    toast("Please upload a tax certificate!");
                    return false;
                }

            }
        }

        return true;
    }

    public void uploadUserDetails() {

    }
}
