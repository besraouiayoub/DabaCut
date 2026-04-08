package com.example.dabacut.utils;

import com.example.dabacut.models.Booking;
import com.example.dabacut.models.Establishment;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Booking> bookings = new ArrayList<>();
    private List<Establishment> establishments = new ArrayList<>();

    private DataManager() {
        // Init with some dummy establishments
        establishments.add(new Establishment("Elite Barber", "Salon Homme", "Premium", "Rabat, Agdal", "0661223344", "Le meilleur service pour hommes."));
        establishments.add(new Establishment("The Gent Shop", "Salon Homme", "Class", "Casablanca, Maarif", "0661556677", "Style classique et moderne."));
        establishments.add(new Establishment("Simple Cut", "Salon Homme", "Standard", "Rabat, Centre", "0661889900", "Coupe rapide et efficace."));
        establishments.add(new Establishment("Beauty Queen", "Salon Filles", "Premium", "Marrakech, Gueliz", "0770112233", "Un espace de beauté luxueux."));
        establishments.add(new Establishment("Pink Rose", "Salon Filles", "Class", "Tanger, Malabata", "0770445566", "Soins de qualité pour femmes."));
        establishments.add(new Establishment("City Nails", "Salon Filles", "Standard", "Fès, Ville Nouvelle", "0770778899", "Manucure et coiffure standard."));
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public List<Establishment> getEstablishments() {
        return establishments;
    }

    public void addEstablishment(Establishment establishment) {
        establishments.add(establishment);
    }
}