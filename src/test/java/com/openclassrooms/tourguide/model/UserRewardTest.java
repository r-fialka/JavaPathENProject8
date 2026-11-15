package com.openclassrooms.tourguide.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

/**
 * Unit tests for {@link UserReward}.
 * Verifies constructors and reward points management.
 */
public class UserRewardTest {

    @Test
    public void constructorWithPoints() {
        UUID userId = UUID.randomUUID();
        VisitedLocation visitedLocation = new VisitedLocation(userId, new gpsUtil.location.Location(0, 0), new Date());
        Attraction attraction = new Attraction("Test", "City", "State", 10, 20);

        UserReward reward = new UserReward(visitedLocation, attraction, 100);

        assertEquals(visitedLocation, reward.visitedLocation);
        assertEquals(attraction, reward.attraction);
        assertEquals(100, reward.getRewardPoints());
    }

    @Test
    public void constructorWithoutPoints() {
        UUID userId = UUID.randomUUID();
        VisitedLocation visitedLocation = new VisitedLocation(userId, new gpsUtil.location.Location(0, 0), new Date());
        Attraction attraction = new Attraction("Test", "City", "State", 10, 20);

        UserReward reward = new UserReward(visitedLocation, attraction);

        assertEquals(visitedLocation, reward.visitedLocation);
        assertEquals(attraction, reward.attraction);
        assertEquals(0, reward.getRewardPoints()); // Reward points default to 0.
    }

    @Test
    public void setRewardPoints() {
        UUID userId = UUID.randomUUID();
        VisitedLocation visitedLocation = new VisitedLocation(userId, new gpsUtil.location.Location(0, 0), new Date());
        Attraction attraction = new Attraction("Test", "City", "State", 10, 20);

        UserReward reward = new UserReward(visitedLocation, attraction);
        reward.setRewardPoints(250);

        assertEquals(250, reward.getRewardPoints());
    }
}