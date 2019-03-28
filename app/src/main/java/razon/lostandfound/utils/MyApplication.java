package razon.lostandfound.utils;

import android.app.Application;

/**
 * Created by HP on 17-Nov-17.
 */

public class MyApplication extends Application {

    public static MyApplication INSTANCE;
    private static final String DATABASE_NAME = "MyDatabase";
    private static final String PREFERENCES = "RoomDemo.preferences";
    private static final String KEY_FORCE_UPDATE = "force_update";



    public static MyApplication get() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // create database


        INSTANCE = this;
    }


//    public boolean isForceUpdate() {
//        return getSP().getBoolean(KEY_FORCE_UPDATE, true);
//    }
//
//    public void setForceUpdate(boolean force) {
//        SharedPreferences.Editor edit = getSP().edit();
//        edit.putBoolean(KEY_FORCE_UPDATE, force);
//        edit.apply();
//    }
//
//    private SharedPreferences getSP() {
//        return getSharedPreferences(PREFERENCES, MODE_PRIVATE);
//    }
}
