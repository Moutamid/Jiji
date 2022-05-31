package com.moutamid.jiji.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {
    public static final String IS_LOGGED_IN = "isloggedin";
    public static final String NULL = "null";

    public static final String PARAMS = "params";
    public static final String TYPE_SELLER = "type_seller";
    public static final String TYPE_BUYER = "type_buyer";
    public static final String LOGIN = "login";
    public static final String SIGN_UP = "sign_up";
    public static final String USERS = "users";
    public static final String TYPE_PRODUCT = "type_product";
    public static final String TYPE_SERVICE = "type_service";
    public static final String USER_LOCATION = "user_location";
    public static final String USER_NUMBER = "user_number";
    public static final String CURRENT_PRODUCT = "current_product";
    public static final String STAR_COUNT = "star_count";
    public static final String CURRENT_USER_MODEL = "current_user_model";
    public static final String STARS = "stars";
    public static final String TYPE_NAME = "type_name";
    public static final String TYPE_NUMBER = "type_number";
    public static final String TYPE_EMAIL = "type_email";

    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("JijiSellingApp");
        db.keepSynced(true);
        return db;
    }
}
