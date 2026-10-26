package com.openclassrooms.tourguide.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

public class UserTest {

    @Test
    public void userInitialization() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "testUser", "123", "test@test.com");

        assertEquals(id, user.getUserId());
        assertEquals("testUser", user.getUserName());
        assertEquals("123", user.getPhoneNumber());
        assertEquals("test@test.com", user.getEmailAddress());
        assertNotNull(user.getUserPreferences());
        assertTrue(user.getVisitedLocations().isEmpty());
        assertTrue(user.getUserRewards().isEmpty());
    }

    @Test
    public void addAndClearVisitedLocations() {
        User user = new User(UUID.randomUUID(), "testUser", "123", "test@test.com");
        VisitedLocation location = new VisitedLocation(user.getUserId(), new Location(0, 0), new Date());

        user.addToVisitedLocations(location);
        assertEquals(1, user.getVisitedLocations().size());
        assertEquals(location, user.getLastVisitedLocation());

        user.clearVisitedLocations();
        assertTrue(user.getVisitedLocations().isEmpty());
        assertNull(user.getLastVisitedLocation()); // Проверка защиты от пустого списка
    }

    @Test
    public void addUserReward_Unique() {
        User user = new User(UUID.randomUUID(), "testUser", "123", "test@test.com");
        Attraction attraction = new Attraction("Eiffel Tower", "Paris", "France", 48.8584, 2.2945);
        VisitedLocation location = new VisitedLocation(user.getUserId(), new Location(0, 0), new Date());

        UserReward reward = new UserReward(location, attraction, 100);
        user.addUserReward(reward);

        assertEquals(1, user.getUserRewards().size());
    }

    @Test
    public void addUserReward_Duplicate() {
        User user = new User(UUID.randomUUID(), "testUser", "123", "test@test.com");
        Attraction attraction = new Attraction("Eiffel Tower", "Paris", "France", 48.8584, 2.2945);
        VisitedLocation location = new VisitedLocation(user.getUserId(), new Location(0, 0), new Date());

        UserReward reward1 = new UserReward(location, attraction, 100);
        UserReward reward2 = new UserReward(location, attraction, 200);

        user.addUserReward(reward1);
        user.addUserReward(reward2); // Должен игнорироваться, так как аттракцион уже есть

        assertEquals(1, user.getUserRewards().size());
    }

    @Test
    public void setPhoneAndEmail() {
        User user = new User(UUID.randomUUID(), "testUser", "123", "test@test.com");
        user.setPhoneNumber("999");
        user.setEmailAddress("new@test.com");
        user.setLatestLocationTimestamp(new Date());

        assertEquals("999", user.getPhoneNumber());
        assertEquals("new@test.com", user.getEmailAddress());
        assertNotNull(user.getLatestLocationTimestamp());
    }
}