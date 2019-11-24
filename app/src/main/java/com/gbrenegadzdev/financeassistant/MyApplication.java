package com.gbrenegadzdev.financeassistant;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.gbrenegadzdev.financeassistant.models.realm.Migration;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {

        Realm.init(this);
        // The RealmConfiguration is created using the builder pattern.
        // The Realm file will be located in Context.getFilesDir() with name "myrealm.realm"
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myassistant.realm")
                .schemaVersion(2)
                .migration(new Migration())
                .build();
        // Use the config
        Realm.setDefaultConfiguration(config);

        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
