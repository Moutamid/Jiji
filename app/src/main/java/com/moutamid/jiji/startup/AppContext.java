package com.moutamid.jiji.startup;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.jiji.model.LatLng2;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);

        if (Constants.auth().getCurrentUser() == null)
            return;

        Constants.databaseReference()
                .child(Constants.USERS)
                .child(Constants.auth().getUid())
                .child("current_location")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Stash.put(Constants.USER_LOCATION, snapshot.getValue(LatLng2.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}
