package com.example.dabacut.realm;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public final class DabaCutRealmMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        final RealmSchema schema = realm.getSchema();
        if (oldVersion < 4) {
            RealmObjectSchema booking = schema.get("BookingRealm");
            booking.addField("status", String.class);
            booking.addField("paymentMethod", String.class);
            booking.addField("durationMinutes", int.class);
            booking.transform(obj -> {
                obj.setString("status", "confirmed");
                obj.setString("paymentMethod", "at_salon");
                obj.setInt("durationMinutes", 60);
            });

            RealmObjectSchema est = schema.get("EstablishmentRealm");
            est.addField("weekdayOpenFrom", String.class);
            est.addField("weekdayOpenTo", String.class);
            est.addField("openSaturday", boolean.class);
            est.addField("saturdayOpenFrom", String.class);
            est.addField("saturdayOpenTo", String.class);
            est.addField("openSunday", boolean.class);
            est.addField("sundayOpenFrom", String.class);
            est.addField("sundayOpenTo", String.class);
            est.transform(obj -> {
                obj.setString("weekdayOpenFrom", "09:00");
                obj.setString("weekdayOpenTo", "19:00");
                obj.setBoolean("openSaturday", true);
                obj.setString("saturdayOpenFrom", "09:00");
                obj.setString("saturdayOpenTo", "18:00");
                obj.setBoolean("openSunday", false);
                obj.setString("sundayOpenFrom", "10:00");
                obj.setString("sundayOpenTo", "16:00");
            });
        }
    }
}
