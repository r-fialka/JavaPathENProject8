package com.openclassrooms.tourguide.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

/**
 * Represents a user of the TourGuide application.
 * This class holds personal information, visited locations, rewards, and trip deals.
 * It is designed to be thread-safe for concurrent processing.
 */
public class User {
	private final UUID userId;
	private final String userName;
	private String phoneNumber;
	private String emailAddress;
	private Date latestLocationTimestamp;

	// Thread-safe lists for storing user locations and rewards during concurrent processing.
	private final List<VisitedLocation> visitedLocations = new CopyOnWriteArrayList<>();
	private final List<UserReward> userRewards = new CopyOnWriteArrayList<>();
	private UserPreferences userPreferences = new UserPreferences();
	private List<Provider> tripDeals = new CopyOnWriteArrayList<>();

	public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
		this.userId = userId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}

	public UUID getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setLatestLocationTimestamp(Date latestLocationTimestamp) {
		this.latestLocationTimestamp = latestLocationTimestamp;
	}

	public Date getLatestLocationTimestamp() {
		return latestLocationTimestamp;
	}

	public void addToVisitedLocations(VisitedLocation visitedLocation) {
		visitedLocations.add(visitedLocation);
	}

	/**
	 * Returns the thread-safe list of visited locations.
	 * @return List of visited locations.
	 */
	public List<VisitedLocation> getVisitedLocations() {
		return visitedLocations;
	}

	public void clearVisitedLocations() {
		visitedLocations.clear();
	}

	/**
	 * Adds a reward to the user if they haven't already earned it for this attraction.
	 * The method is synchronized to prevent duplicate entries when called concurrently.
	 *
	 * @param userReward The reward to add.
	 */
	public synchronized void addUserReward(UserReward userReward) {
		// Add the reward only if the attraction has not already been rewarded.
		boolean alreadyRewarded = userRewards.stream()
				.anyMatch(r -> r.attraction.attractionName.equals(userReward.attraction.attractionName));

		if (!alreadyRewarded) {
			userRewards.add(userReward);
		}
	}

	public List<UserReward> getUserRewards() {
		return userRewards;
	}

	public UserPreferences getUserPreferences() {
		return userPreferences;
	}

	public void setUserPreferences(UserPreferences userPreferences) {
		this.userPreferences = userPreferences;
	}

	/**
	 * Returns the last visited location, or null if the user has not visited any location yet.
	 * @return The last visited location or null.
	 */
	public VisitedLocation getLastVisitedLocation() {
		if (visitedLocations.isEmpty()) {
			return null;
		}
		return visitedLocations.get(visitedLocations.size() - 1);
	}

	public void setTripDeals(List<Provider> tripDeals) {
		this.tripDeals = tripDeals;
	}

	public List<Provider> getTripDeals() {
		return tripDeals;
	}
}