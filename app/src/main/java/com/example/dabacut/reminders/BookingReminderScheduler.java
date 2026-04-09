package com.example.dabacut.reminders;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.dabacut.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class BookingReminderScheduler {

    private static final String TAG_PREFIX = "dabacut_booking_";

    private BookingReminderScheduler() {
    }

    public static void cancelForBooking(Context context, long bookingId) {
        WorkManager wm = WorkManager.getInstance(context);
        wm.cancelAllWorkByTag(tagFor(bookingId, "24h"));
        wm.cancelAllWorkByTag(tagFor(bookingId, "1h"));
    }

    private static String tagFor(long bookingId, String kind) {
        return TAG_PREFIX + bookingId + "_" + kind;
    }

    /**
     * Planifie deux rappels (J-24h et H-1) une fois la réservation confirmée.
     */
    public static void scheduleAfterConfirmation(Context context, long bookingId,
                                                   String dateDdMmYyyy, String timeHhMm,
                                                   String salonName) {
        cancelForBooking(context, bookingId);

        Date appt = parseAppointment(dateDdMmYyyy, timeHhMm);
        if (appt == null) {
            return;
        }
        long apptMs = appt.getTime();
        long now = System.currentTimeMillis();

        String salon = salonName != null ? salonName : "";

        long delay24h = apptMs - 24L * 60L * 60L * 1000L - now;
        if (delay24h >= 60_000L) {
            enqueue(context, bookingId, "24h",
                    context.getString(R.string.booking_reminder_notif_title),
                    context.getString(R.string.booking_reminder_24h_body, salon, dateDdMmYyyy, timeHhMm),
                    delay24h);
        }

        long delay1h = apptMs - 60L * 60L * 1000L - now;
        if (delay1h >= 60_000L) {
            enqueue(context, bookingId, "1h",
                    context.getString(R.string.booking_reminder_notif_title),
                    context.getString(R.string.booking_reminder_1h_body, salon, timeHhMm),
                    delay1h);
        }
    }

    private static void enqueue(Context context, long bookingId, String kind, String title, String text, long delayMs) {
        Data data = new Data.Builder()
                .putString(BookingReminderWorker.KEY_TITLE, title)
                .putString(BookingReminderWorker.KEY_TEXT, text)
                .build();

        OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(BookingReminderWorker.class)
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag(tagFor(bookingId, kind))
                .build();

        WorkManager.getInstance(context.getApplicationContext()).enqueue(req);
    }

    private static Date parseAppointment(String dateStr, String timeStr) {
        if (dateStr == null || timeStr == null) {
            return null;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(dateStr.trim() + " " + timeStr.trim()));
            return cal.getTime();
        } catch (ParseException e) {
            return null;
        }
    }
}
