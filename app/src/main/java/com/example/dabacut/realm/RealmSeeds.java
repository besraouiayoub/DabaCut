package com.example.dabacut.realm;

import io.realm.Realm;

public final class RealmSeeds {

    private RealmSeeds() {
    }

    /**
     * Call inside {@link Realm#executeTransaction}.
     */
    public static void insertDefaultEstablishments(Realm realm) {
        long id = 1;
        add(realm, id++, "Elite Barber", "Salon Homme", "Premium", "Rabat, Agdal", "0661223344",
                "Le meilleur service pour hommes.", 0, 0);
        add(realm, id++, "The Gent Shop", "Salon Homme", "Class", "Casablanca, Maarif", "0661556677",
                "Style classique et moderne.", 0, 0);
        add(realm, id++, "Simple Cut", "Salon Homme", "Standard", "Rabat, Centre", "0661889900",
                "Coupe rapide et efficace.", 0, 0);
        add(realm, id++, "Beauty Queen", "Salon Filles", "Premium", "Marrakech, Gueliz", "0770112233",
                "Un espace de beauté luxueux.", 0, 0);
        add(realm, id++, "Pink Rose", "Salon Filles", "Class", "Tanger, Malabata", "0770445566",
                "Soins de qualité pour femmes.", 0, 0);
        add(realm, id++, "City Nails", "Salon Filles", "Standard", "Fès, Ville Nouvelle", "0770778899",
                "Manucure et coiffure standard.", 0, 0);
    }

    private static void add(Realm realm, long id, String name, String type, String category,
                            String address, String phone, String description, double lat, double lng) {
        EstablishmentRealm e = realm.createObject(EstablishmentRealm.class, id);
        e.setName(name);
        e.setTypeSalon(type);
        e.setCategory(category);
        e.setAddress(address);
        e.setPhone(phone);
        e.setDescription(description);
        e.setLatitude(lat);
        e.setLongitude(lng);
        e.setOwnerUsername(null);
        e.setWeekdayOpenFrom("09:00");
        e.setWeekdayOpenTo("19:00");
        e.setOpenSaturday(true);
        e.setSaturdayOpenFrom("09:00");
        e.setSaturdayOpenTo("18:00");
        e.setOpenSunday(false);
        e.setSundayOpenFrom("10:00");
        e.setSundayOpenTo("16:00");
    }
}
