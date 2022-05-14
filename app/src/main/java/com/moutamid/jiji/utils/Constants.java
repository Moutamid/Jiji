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

    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("JijiSellingApp");
        db.keepSynced(true);
        return db;
    }
}
