package com.example.dabacut.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class BookingPriceHelperTest {

    @Test
    public void categoryMultiplier_tiered() {
        assertEquals(1.0, BookingPriceHelper.categoryMultiplier("Standard"), 0.001);
        assertEquals(1.15, BookingPriceHelper.categoryMultiplier("Class"), 0.001);
        assertEquals(1.35, BookingPriceHelper.categoryMultiplier("Premium"), 0.001);
    }

    @Test
    public void priceForCategory_defaults() {
        assertEquals(25.0, BookingPriceHelper.priceForCategory("Standard"), 0.001);
        assertEquals(70.0, BookingPriceHelper.priceForCategory("Premium"), 0.001);
    }
}
