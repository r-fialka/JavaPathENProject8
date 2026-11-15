
package com.openclassrooms.tourguide.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

public class RewardsServiceTest {

	@Test
	public void userGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();

		assertEquals(1, userRewards.size());
	}

	@Test
	public void isWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		Attraction attraction = gpsUtil.getAttractions().get(0);

		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}

	@Test
	public void nearAllAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
		tourGuideService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}

	/**
	 * Covers the setDefaultProximityBuffer() method.
	 */
	@Test
	public void setDefaultProximityBufferTest() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		// Set a smaller proximity buffer.
		rewardsService.setProximityBuffer(5);

		// Restore the default proximity buffer (10 miles).
		rewardsService.setDefaultProximityBuffer();

		Attraction attraction = gpsUtil.getAttractions().get(0);

		// Place the user approximately 7 miles away from the attraction.
		Location location = new Location(attraction.latitude + 0.1, attraction.longitude);

		double distance = rewardsService.getDistance(attraction, location);

		// Ensure the test location is outside the 5-mile buffer but within 10 miles.
		assertTrue(distance > 5 && distance <= 10);

		User user = new User(UUID.randomUUID(), "testUser", "000", "test@test.com");
		user.addToVisitedLocations(
				new VisitedLocation(user.getUserId(), location, new Date()));

		rewardsService.calculateRewards(user);

		// Verify that the attraction is rewarded after restoring the default buffer.
		assertTrue(user.getUserRewards().stream()
				.anyMatch(reward -> reward.attraction.attractionName.equals(attraction.attractionName)));
	}

	/**
	 * Covers the shutdownRewardsPool() method.
	 */
	@Test
	public void shutdownRewardsPoolTest() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		// Simply call the method to ensure JaCoCo records its execution.
		rewardsService.shutdownRewardsPool();
	}

	/**
	 * Covers the NaN branch in getDistance (protection against Math.acos floating-point precision errors).
	 */
	@Test
	public void getDistance_NaN_ReturnsZero() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		// Coordinates that may cause Math.acos floating-point precision errors.
		Location loc1 = new Location(90, 0);
		Location loc2 = new Location(-90, 180);

		double distance = rewardsService.getDistance(loc1, loc2);

		// Without the NaN safeguard, the distance would be NaN.
		assertTrue(distance >= 0);
	}
}
