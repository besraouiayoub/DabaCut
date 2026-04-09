package com.example.dabacut.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SlotSchedulingHelperTest {

    @Test
    public void weekdaySlotWithinHours() {
        SlotSchedulingHelper.SalonBusinessHours h = SlotSchedulingHelper.SalonBusinessHours.defaults();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2026);
        cal.set(Calendar.MONTH, Calendar.APRIL);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        assertTrue(SlotSchedulingHelper.isSlotAllowed(cal, "10:00", h));
        assertFalse(SlotSchedulingHelper.isSlotAllowed(cal, "20:00", h));
    }

    @Test
    public void sundayClosedByDefault() {
        SlotSchedulingHelper.SalonBusinessHours h = SlotSchedulingHelper.SalonBusinessHours.defaults();
        Calendar sun = Calendar.getInstance();
        sun.set(Calendar.YEAR, 2026);
        sun.set(Calendar.MONTH, Calendar.APRIL);
        sun.set(Calendar.DAY_OF_MONTH, 5);
        assertFalse(SlotSchedulingHelper.isSlotAllowed(sun, "10:00", h));
    }
}
