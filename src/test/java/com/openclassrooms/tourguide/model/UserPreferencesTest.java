package com.openclassrooms.tourguide.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UserPreferencesTest {

    @Test
    public void defaultValues() {
        UserPreferences prefs = new UserPreferences();

        assertEquals(Integer.MAX_VALUE, prefs.getAttractionProximity());
        assertEquals(1, prefs.getTripDuration());
        assertEquals(1, prefs.getTicketQuantity());
        assertEquals(1, prefs.getNumberOfAdults());
        assertEquals(0, prefs.getNumberOfChildren());
    }

    @Test
    public void setValues() {
        UserPreferences prefs = new UserPreferences();

        prefs.setAttractionProximity(500);
        prefs.setTripDuration(7);
        prefs.setTicketQuantity(2);
        prefs.setNumberOfAdults(2);
        prefs.setNumberOfChildren(3);

        assertEquals(500, prefs.getAttractionProximity());
        assertEquals(7, prefs.getTripDuration());
        assertEquals(2, prefs.getTicketQuantity());
        assertEquals(2, prefs.getNumberOfAdults());
        assertEquals(3, prefs.getNumberOfChildren());
    }
}