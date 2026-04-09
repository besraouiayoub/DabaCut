package com.example.dabacut.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.dabacut.models.Booking;
import com.example.dabacut.models.BookingStatus;
import com.example.dabacut.models.Establishment;
import com.example.dabacut.models.PaymentMethod;
import com.example.dabacut.realm.BookingRealm;
import com.example.dabacut.realm.EstablishmentRealm;
import com.example.dabacut.realm.UserRealm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.dabacut.utils.SlotSchedulingHelper;
import com.example.dabacut.utils.SlotSchedulingHelper.SalonBusinessHours;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class SalonRepository {

    /** Créneaux possibles (même salon : une réservation par date + heure). */
    private static final String[] SLOT_TIMES = {
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"
    };

    public static final class SlotSuggestion {
        public final String date;
        public final String time;

        public SlotSuggestion(String date, String time) {
            this.date = date;
            this.time = time;
        }
    }

    private static volatile SalonRepository instance;

    private final Executor io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    private SalonRepository() {
    }

    public static SalonRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (SalonRepository.class) {
                if (instance == null) {
                    instance = new SalonRepository();
                }
            }
        }
        return instance;
    }

    public void registerUser(String username, String password, String role, String language,
                             OnRegisterResult callback) {
        final String u = username.trim();
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                UserRealm existing = realm.where(UserRealm.class).equalTo("username", u).findFirst();
                if (existing != null) {
                    main.post(() -> callback.onResult(RegisterResult.USERNAME_TAKEN));
                    return;
                }
                realm.executeTransaction(r -> {
                    UserRealm row = r.createObject(UserRealm.class, u);
                    row.setPassword(password);
                    row.setRole(role);
                    row.setLanguage(language);
                });
                main.post(() -> callback.onResult(RegisterResult.SUCCESS));
            } finally {
                realm.close();
            }
        });
    }

    public void authenticate(String username, String password, OnAuthResult callback) {
        final String u = username.trim();
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                UserRealm found = realm.where(UserRealm.class).equalTo("username", u).findFirst();
                if (found != null && password.equals(found.getPassword())) {
                    String role = found.getRole();
                    String lang = found.getLanguage() != null ? found.getLanguage() : "";
                    main.post(() -> callback.onSuccess(u, role, lang));
                } else {
                    main.post(callback::onFailure);
                }
            } finally {
                realm.close();
            }
        });
    }

    public void hasEstablishmentForOwner(String ownerUsername, OnResult<Boolean> callback) {
        final String o = ownerUsername != null ? ownerUsername.trim() : "";
        if (o.isEmpty()) {
            main.post(() -> callback.onResult(false));
            return;
        }
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                long c = realm.where(EstablishmentRealm.class)
                        .equalTo("ownerUsername", o)
                        .count();
                main.post(() -> callback.onResult(c > 0));
            } finally {
                realm.close();
            }
        });
    }

    public void getSalonNameForOwner(String ownerUsername, OnResult<String> callback) {
        final String o = ownerUsername != null ? ownerUsername.trim() : "";
        if (o.isEmpty()) {
            main.post(() -> callback.onResult(null));
            return;
        }
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                EstablishmentRealm e = realm.where(EstablishmentRealm.class)
                        .equalTo("ownerUsername", o)
                        .findFirst();
                String name = e != null ? e.getName() : null;
                main.post(() -> callback.onResult(name));
            } finally {
                realm.close();
            }
        });
    }

    public void getEstablishmentBySalonName(String salonName, OnResult<Establishment> callback) {
        final String s = salonName != null ? salonName.trim() : "";
        if (s.isEmpty()) {
            main.post(() -> callback.onResult(null));
            return;
        }
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                EstablishmentRealm er = findEstablishmentRealmBySalonName(realm, s);
                Establishment out = er != null ? toEstablishment(realm.copyFromRealm(er)) : null;
                main.post(() -> callback.onResult(out));
            } finally {
                realm.close();
            }
        });
    }

    public void getEstablishments(OnResult<List<Establishment>> callback) {
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                RealmResults<EstablishmentRealm> results = realm.where(EstablishmentRealm.class)
                        .sort("name", Sort.ASCENDING)
                        .findAll();
                List<EstablishmentRealm> detached = realm.copyFromRealm(results);
                List<Establishment> list = new ArrayList<>(detached.size());
                for (EstablishmentRealm r : detached) {
                    list.add(toEstablishment(r));
                }
                main.post(() -> callback.onResult(list));
            } finally {
                realm.close();
            }
        });
    }

    public void addEstablishment(Establishment establishment, String ownerUsername, Runnable onComplete) {
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                realm.executeTransaction(r -> {
                    long id = nextEstablishmentId(r);
                    EstablishmentRealm row = r.createObject(EstablishmentRealm.class, id);
                    applyEstablishmentFields(row, establishment);
                    row.setOwnerUsername(safe(ownerUsername));
                });
            } finally {
                realm.close();
            }
            if (onComplete != null) {
                main.post(onComplete);
            }
        });
    }

    public void updateEstablishment(Establishment establishment, Runnable onComplete) {
        if (establishment == null || establishment.getId() <= 0) {
            if (onComplete != null) {
                main.post(onComplete);
            }
            return;
        }
        final long eid = establishment.getId();
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                realm.executeTransaction(r -> {
                    EstablishmentRealm row = r.where(EstablishmentRealm.class)
                            .equalTo("id", eid)
                            .findFirst();
                    if (row != null) {
                        applyEstablishmentFields(row, establishment);
                    }
                });
            } finally {
                realm.close();
            }
            if (onComplete != null) {
                main.post(onComplete);
            }
        });
    }

    public void getEstablishmentForOwner(String ownerUsername, OnResult<Establishment> callback) {
        final String o = ownerUsername != null ? ownerUsername.trim() : "";
        if (o.isEmpty()) {
            main.post(() -> callback.onResult(null));
            return;
        }
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                EstablishmentRealm e = realm.where(EstablishmentRealm.class)
                        .equalTo("ownerUsername", o)
                        .findFirst();
                Establishment out = e != null ? toEstablishment(realm.copyFromRealm(e)) : null;
                main.post(() -> callback.onResult(out));
            } finally {
                realm.close();
            }
        });
    }

    private static void applyEstablishmentFields(EstablishmentRealm row, Establishment establishment) {
        row.setName(establishment.getName());
        row.setTypeSalon(establishment.getTypeSalon());
        row.setCategory(establishment.getCategory());
        row.setAddress(establishment.getAddress());
        row.setPhone(establishment.getPhone());
        row.setDescription(establishment.getDescription());
        row.setLatitude(establishment.getLatitude());
        row.setLongitude(establishment.getLongitude());
        row.setWeekdayOpenFrom(safe(establishment.getWeekdayOpenFrom()));
        row.setWeekdayOpenTo(safe(establishment.getWeekdayOpenTo()));
        row.setOpenSaturday(establishment.isOpenSaturday());
        row.setSaturdayOpenFrom(safe(establishment.getSaturdayOpenFrom()));
        row.setSaturdayOpenTo(safe(establishment.getSaturdayOpenTo()));
        row.setOpenSunday(establishment.isOpenSunday());
        row.setSundayOpenFrom(safe(establishment.getSundayOpenFrom()));
        row.setSundayOpenTo(safe(establishment.getSundayOpenTo()));
    }

    /** Réservations dont le nom du salon correspond (ignore la casse), pour le tableau de bord propriétaire. */
    public void getBookingsForSalonName(String salonName, OnResult<List<Booking>> callback) {
        final String target = salonName != null ? salonName.trim() : "";
        getBookings(all -> {
            if (target.isEmpty()) {
                main.post(() -> callback.onResult(new ArrayList<>()));
                return;
            }
            List<Booking> mine = new ArrayList<>();
            for (Booking b : all) {
                if (b.getSalonName() != null && b.getSalonName().trim().equalsIgnoreCase(target)) {
                    mine.add(b);
                }
            }
            main.post(() -> callback.onResult(mine));
        });
    }

    public void getBookings(OnResult<List<Booking>> callback) {
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                RealmResults<BookingRealm> results = realm.where(BookingRealm.class)
                        .sort("id", Sort.DESCENDING)
                        .findAll();
                List<BookingRealm> detached = realm.copyFromRealm(results);
                List<Booking> list = new ArrayList<>(detached.size());
                for (BookingRealm b : detached) {
                    list.add(toBooking(b));
                }
                main.post(() -> callback.onResult(list));
            } finally {
                realm.close();
            }
        });
    }

    public void getBookingById(long id, OnResult<Booking> callback) {
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                BookingRealm found = realm.where(BookingRealm.class).equalTo("id", id).findFirst();
                Booking b = found != null ? toBooking(realm.copyFromRealm(found)) : null;
                main.post(() -> callback.onResult(b));
            } finally {
                realm.close();
            }
        });
    }

    /**
     * Prochain créneau libre pour ce salon (à partir de demain), en évitant date+heure déjà réservées.
     */
    public void findNextAvailableSlot(String salonName, OnResult<SlotSuggestion> callback) {
        final String salon = salonName != null ? salonName.trim() : "";
        if (salon.isEmpty()) {
            main.post(() -> callback.onResult(null));
            return;
        }
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                EstablishmentRealm er = findEstablishmentRealmBySalonName(realm, salon);
                Establishment est = er != null ? toEstablishment(realm.copyFromRealm(er)) : null;
                SalonBusinessHours hours = SlotSchedulingHelper.hoursFromEstablishment(est);

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                for (int day = 0; day < 60; day++) {
                    String dateStr = df.format(cal.getTime());
                    for (String time : SLOT_TIMES) {
                        if (!SlotSchedulingHelper.isSlotAllowed(cal, time, hours)) {
                            continue;
                        }
                        if (!isSlotTaken(realm, salon, dateStr, time)) {
                            SlotSuggestion slot = new SlotSuggestion(dateStr, time);
                            main.post(() -> callback.onResult(slot));
                            return;
                        }
                    }
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                }
                main.post(() -> callback.onResult(null));
            } finally {
                realm.close();
            }
        });
    }

    private static EstablishmentRealm findEstablishmentRealmBySalonName(Realm realm, String salonName) {
        final String target = salonName.trim();
        RealmResults<EstablishmentRealm> all = realm.where(EstablishmentRealm.class).findAll();
        for (EstablishmentRealm e : all) {
            if (e.getName() != null && e.getName().trim().equalsIgnoreCase(target)) {
                return e;
            }
        }
        return null;
    }

    private static boolean isSlotTaken(Realm realm, String salon, String date, String time) {
        final String s = salon.trim();
        final String d = date != null ? date.trim() : "";
        final String t = time != null ? time.trim() : "";
        RealmResults<BookingRealm> all = realm.where(BookingRealm.class).findAll();
        for (BookingRealm b : all) {
            String st = b.getStatus() != null ? b.getStatus() : BookingStatus.CONFIRMED;
            if (!BookingStatus.occupiesSlot(st)) {
                continue;
            }
            if (b.getSalonName() != null && b.getSalonName().trim().equalsIgnoreCase(s)) {
                String bd = b.getDate() != null ? b.getDate().trim() : "";
                String bt = b.getTime() != null ? b.getTime().trim() : "";
                if (d.equals(bd) && t.equals(bt)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setBookingStatus(long bookingId, String newStatus, Runnable onComplete) {
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                realm.executeTransaction(r -> {
                    BookingRealm row = r.where(BookingRealm.class).equalTo("id", bookingId).findFirst();
                    if (row != null && newStatus != null) {
                        row.setStatus(newStatus);
                    }
                });
            } finally {
                realm.close();
            }
            if (onComplete != null) {
                main.post(onComplete);
            }
        });
    }

    public void cancelBookingForClient(long bookingId, String clientUsername, OnResult<Boolean> callback) {
        final String u = clientUsername != null ? clientUsername.trim() : "";
        if (u.isEmpty()) {
            main.post(() -> callback.onResult(false));
            return;
        }
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            final boolean[] ok = {false};
            try {
                realm.executeTransaction(r -> {
                    BookingRealm row = r.where(BookingRealm.class).equalTo("id", bookingId).findFirst();
                    if (row == null) {
                        return;
                    }
                    String cu = row.getClientUsername() != null ? row.getClientUsername().trim() : "";
                    if (!u.equalsIgnoreCase(cu)) {
                        return;
                    }
                    String st = row.getStatus();
                    if (BookingStatus.CANCELLED.equals(st) || BookingStatus.REJECTED.equals(st)) {
                        return;
                    }
                    row.setStatus(BookingStatus.CANCELLED);
                    ok[0] = true;
                });
            } finally {
                realm.close();
            }
            boolean success = ok[0];
            main.post(() -> callback.onResult(success));
        });
    }

    public interface OnAddBookingResult {
        void onSuccess(long newBookingId);

        void onSlotTaken();
    }

    /** Insertion seulement si le créneau est encore libre (évite la course entre deux clients). */
    public void addBookingIfSlotFree(Booking booking, OnAddBookingResult callback) {
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                String salon = safe(booking.getSalonName()).trim();
                String d = safe(booking.getDate()).trim();
                String t = safe(booking.getTime()).trim();
                if (salon.isEmpty() || isSlotTaken(realm, salon, d, t)) {
                    main.post(callback::onSlotTaken);
                    return;
                }
                final long[] newId = {-1L};
                realm.executeTransaction(r -> newId[0] = insertBookingRow(r, booking));
                long nid = newId[0];
                main.post(() -> callback.onSuccess(nid));
            } finally {
                realm.close();
            }
        });
    }

    public void addBooking(Booking booking, Runnable onComplete) {
        io.execute(() -> {
            Realm realm = Realm.getDefaultInstance();
            try {
                realm.executeTransaction(r -> insertBookingRow(r, booking));
            } finally {
                realm.close();
            }
            if (onComplete != null) {
                main.post(onComplete);
            }
        });
    }

    private static long insertBookingRow(Realm r, Booking booking) {
        long id = nextBookingId(r);
        BookingRealm row = r.createObject(BookingRealm.class, id);
        row.setClientName(safe(booking.getClientName()));
        row.setClientPhone(safe(booking.getClientPhone()));
        row.setClientUsername(safe(booking.getClientUsername()));
        row.setSalonName(safe(booking.getSalonName()));
        row.setService(safe(booking.getService()));
        row.setDate(safe(booking.getDate()));
        row.setTime(safe(booking.getTime()));
        row.setNotes(safe(booking.getNotes()));
        row.setPriceTotal(booking.getPriceTotal());
        row.setStatus(safe(booking.getStatus()));
        row.setPaymentMethod(safe(booking.getPaymentMethod()));
        row.setDurationMinutes(booking.getDurationMinutes());
        return id;
    }

    private static long nextEstablishmentId(Realm realm) {
        Number max = realm.where(EstablishmentRealm.class).max("id");
        return max == null ? 1L : max.longValue() + 1L;
    }

    private static long nextBookingId(Realm realm) {
        Number max = realm.where(BookingRealm.class).max("id");
        return max == null ? 1L : max.longValue() + 1L;
    }

    private static String safe(String s) {
        return s != null ? s : "";
    }

    private static Establishment toEstablishment(EstablishmentRealm e) {
        Establishment x = new Establishment(
                e.getName(), e.getTypeSalon(), e.getCategory(),
                e.getAddress(), e.getPhone(), e.getDescription()
        );
        x.setId(e.getId());
        x.setLatitude(e.getLatitude());
        x.setLongitude(e.getLongitude());
        x.setOwnerUsername(e.getOwnerUsername() != null ? e.getOwnerUsername() : "");
        x.setWeekdayOpenFrom(e.getWeekdayOpenFrom());
        x.setWeekdayOpenTo(e.getWeekdayOpenTo());
        x.setOpenSaturday(e.isOpenSaturday());
        x.setSaturdayOpenFrom(e.getSaturdayOpenFrom());
        x.setSaturdayOpenTo(e.getSaturdayOpenTo());
        x.setOpenSunday(e.isOpenSunday());
        x.setSundayOpenFrom(e.getSundayOpenFrom());
        x.setSundayOpenTo(e.getSundayOpenTo());
        return x;
    }

    private static Booking toBooking(BookingRealm b) {
        String st = b.getStatus() != null ? b.getStatus() : BookingStatus.CONFIRMED;
        String pay = b.getPaymentMethod() != null ? b.getPaymentMethod() : PaymentMethod.AT_SALON;
        int dur = b.getDurationMinutes();
        return new Booking(
                b.getId(),
                b.getClientName(),
                b.getClientPhone(),
                b.getClientUsername(),
                b.getSalonName(),
                b.getService(),
                b.getDate(),
                b.getTime(),
                b.getNotes() != null ? b.getNotes() : "",
                b.getPriceTotal(),
                st,
                pay,
                dur
        );
    }

    public interface OnResult<T> {
        void onResult(T data);
    }

    public interface OnAuthResult {
        void onSuccess(String username, String role, String language);

        void onFailure();
    }

    public enum RegisterResult {
        SUCCESS,
        USERNAME_TAKEN
    }

    public interface OnRegisterResult {
        void onResult(RegisterResult result);
    }
}
