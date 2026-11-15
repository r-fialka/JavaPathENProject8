package com.openclassrooms.tourguide.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

/**
 * Service responsible for calculating user rewards based on their visited locations.
 * Contains logic for proximity checks, distance calculations, and parallel processing.
 */
@Service
public class RewardsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// Distance thresholds are expressed in miles.
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;

	// Thread pool for parallel reward calculations (inside the service, not in tests).
	private static final int THREAD_POOL_SIZE = 200;
	private final ExecutorService rewardsPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}

	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	/**
	 * Calculates rewards for a single user.
	 * Rewards are added through User.addUserReward(), which prevents duplicate
	 * rewards for the same attraction.
	 */
	public void calculateRewards(User user) {
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtil.getAttractions();

		// Local Set of attractions that have already been rewarded.
		Set<String> rewardedNames = user.getUserRewards().stream()
				.map(r -> r.attraction.attractionName)
				.collect(Collectors.toSet());

		for (VisitedLocation visitedLocation : userLocations) {
			for (Attraction attraction : attractions) {
				if (!rewardedNames.contains(attraction.attractionName)) {
					if (nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction,
								getRewardPoints(attraction, user)));
						rewardedNames.add(attraction.attractionName);
					}
				}
			}
		}
	}

	/**
	 * Calculates rewards for a list of users in parallel.
	 * Multithreading is encapsulated within the service — tests remain sequential.
	 */
	public void calculateRewardsForAllUsers(List<User> users) {
		List<CompletableFuture<Void>> futures = users.stream()
				.map(u -> CompletableFuture.runAsync(() -> calculateRewards(u), rewardsPool))
				.collect(Collectors.toList());
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	}

	/**
	 * Shuts down the thread pool. Call when the application is shutting down.
	 */
	public void shutdownRewardsPool() {
		rewardsPool.shutdown();
	}

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) <= attractionProximityRange;
	}

	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) <= proximityBuffer;
	}

	/**
	 * Retrieves the reward points for a specific attraction and user.
	 * Delegates to the external RewardCentral service.
	 *
	 * @param attraction The attraction visited.
	 * @param user       The user who visited it.
	 * @return The reward points.
	 */
	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	/**
	 * Calculates the distance in miles between two locations using the haversine formula.
	 *
	 * @param loc1 The first location.
	 * @param loc2 The second location.
	 * @return The distance in statute miles.
	 */
	public double getDistance(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		// Protect against NaN caused by floating-point precision errors.
		if (Double.isNaN(angle)) {
			angle = 0;
		}

		double nauticalMiles = 60 * Math.toDegrees(angle);
		return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
	}
}