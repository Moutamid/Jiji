package com.moutamid.jiji.startup;

import android.app.Application;

import com.moutamid.jiji.utils.Stash;

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);
    }
}
