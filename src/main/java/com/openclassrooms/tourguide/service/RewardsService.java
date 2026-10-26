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

@Service
public class RewardsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;

	// Пул потоков для параллельного расчёта наград (внутри сервиса, не в тестах)
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
	 * Рассчитывает награды для одного пользователя.
	 * Потокобезопасен: userRewards в User — CopyOnWriteArrayList.
	 */
	public void calculateRewards(User user) {
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtil.getAttractions();

		// Локальный Set уже награждённых аттракционов
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
	 * Параллельно рассчитывает награды для списка пользователей.
	 * Многопоточность инкапсулирована внутри сервиса — тесты остаются последовательными.
	 */
	public void calculateRewardsForAllUsers(List<User> users) {
		List<CompletableFuture<Void>> futures = users.stream()
				.map(u -> CompletableFuture.runAsync(() -> calculateRewards(u), rewardsPool))
				.collect(Collectors.toList());
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	}

	/**
	 * Останавливает пул потоков. Вызывать при завершении приложения.
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

	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	public double getDistance(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		// защита от NaN из-за погрешности плавающей точки
		if (Double.isNaN(angle)) {
			angle = 0;
		}

		double nauticalMiles = 60 * Math.toDegrees(angle);
		return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
	}
}