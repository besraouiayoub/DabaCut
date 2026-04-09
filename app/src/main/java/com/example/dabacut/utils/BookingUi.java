package com.example.dabacut.utils;

import android.content.Context;

import com.example.dabacut.R;
import com.example.dabacut.models.BookingStatus;
import com.example.dabacut.models.PaymentMethod;

public final class BookingUi {

    private BookingUi() {
    }

    public static String statusLabel(Context context, String status) {
        if (status == null) {
            return "—";
        }
        switch (status) {
            case BookingStatus.PENDING:
                return context.getString(R.string.booking_status_pending);
            case BookingStatus.CONFIRMED:
                return context.getString(R.string.booking_status_confirmed);
            case BookingStatus.REJECTED:
                return context.getString(R.string.booking_status_rejected);
            case BookingStatus.CANCELLED:
                return context.getString(R.string.booking_status_cancelled);
            default:
                return status;
        }
    }

    public static String paymentLabel(Context context, String paymentMethod) {
        if (paymentMethod == null) {
            return "—";
        }
        if (PaymentMethod.ONLINE_DEMO.equals(paymentMethod)) {
            return context.getString(R.string.payment_online_demo);
        }
        return context.getString(R.string.payment_at_salon);
    }
}
