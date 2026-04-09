package com.example.dabacut.reminders;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.dabacut.DabaCutApp;
import com.example.dabacut.R;
import com.example.dabacut.activities.MyBookingsActivity;

public class BookingReminderWorker extends Worker {

    static final String KEY_TITLE = "title";
    static final String KEY_TEXT = "text";

    public BookingReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString(KEY_TITLE);
        String text = getInputData().getString(KEY_TEXT);
        if (title == null) {
            title = getApplicationContext().getString(R.string.booking_reminder_notif_title);
        }
        if (text == null) {
            text = "";
        }

        Intent open = new Intent(getApplicationContext(), MyBookingsActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(
                getApplicationContext(),
                (int) System.currentTimeMillis(),
                open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext(),
                DabaCutApp.NOTIFICATION_CHANNEL_BOOKINGS)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pi)
                .setAutoCancel(true);

        NotificationManagerCompat.from(getApplicationContext()).notify((int) (hashCode() + title.hashCode()), b.build());
        return Result.success();
    }
}
