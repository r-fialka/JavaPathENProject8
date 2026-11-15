package com.openclassrooms.tourguide.model;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

/**
 * Represents a reward earned by a user for visiting a specific attraction.
 * Stores the visited location, the attraction, and the points awarded.
 */
public class UserReward {

	public final VisitedLocation visitedLocation;
	public final Attraction attraction;
	private int rewardPoints;

	/**
	 * Constructs a UserReward with the specified points.
	 *
	 * @param visitedLocation The location where the attraction was visited.
	 * @param attraction      The attraction that was visited.
	 * @param rewardPoints    The number of points awarded.
	 */
	public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
		this.rewardPoints = rewardPoints;
	}

	/**
	 * Constructs a UserReward without specifying points (defaults to 0).
	 *
	 * @param visitedLocation The location where the attraction was visited.
	 * @param attraction      The attraction that was visited.
	 */
	public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
	}

	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

	public int getRewardPoints() {
		return rewardPoints;
	}
}