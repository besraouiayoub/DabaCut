package com.example.dabacut;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.dabacut.realm.DabaCutRealmMigration;
import com.example.dabacut.realm.EstablishmentRealm;
import com.example.dabacut.realm.RealmSeeds;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DabaCutApp extends Application {

    public static final String NOTIFICATION_CHANNEL_BOOKINGS = "dabacut_bookings";

    private static final Executor DB_IO = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("dabacut.realm")
                .schemaVersion(4)
                .migration(new DabaCutRealmMigration())
                .build();
        Realm.setDefaultConfiguration(configuration);

        DB_IO.execute(this::seedIfEmpty);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationChannel ch = new NotificationChannel(
                NOTIFICATION_CHANNEL_BOOKINGS,
                getString(R.string.notification_channel_bookings_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        ch.setDescription(getString(R.string.notification_channel_bookings_desc));
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (nm != null) {
            nm.createNotificationChannel(ch);
        }
    }

    private void seedIfEmpty() {
        Realm realm = Realm.getDefaultInstance();
        try {
            if (realm.where(EstablishmentRealm.class).count() == 0) {
                realm.executeTransaction(r -> RealmSeeds.insertDefaultEstablishments(r));
            }
        } finally {
            realm.close();
        }
    }
}
