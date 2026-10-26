package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.model.dto.NearByAttractionDto;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.tracker.Tracker;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;

	// Пул потоков для параллельной обработки пользователей (внутри сервиса, не в тестах)
	private static final int THREAD_POOL_SIZE = 200;
	private final ExecutorService trackingPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;

		Locale.setDefault(Locale.US);

		if (testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ? user.getLastVisitedLocation()
				: trackUserLocation(user);
		return visitedLocation;
	}

	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	public List<User> getAllUsers() {
		return new ArrayList<>(internalUserMap.values());
	}

	public void addUser(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	/**
	 * Отслеживает текущую локацию пользователя и начисляет награды.
	 * Потокобезопасен благодаря CopyOnWriteArrayList в User.
	 */
	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	/**
	 * Параллельно отслеживает локации для списка пользователей.
	 * Многопоточность инкапсулирована внутри сервиса — тесты остаются последовательными.
	 */
	public void trackAllUsers(List<User> users) {
		List<CompletableFuture<Void>> futures = users.stream()
				.map(u -> CompletableFuture.runAsync(() -> trackUserLocation(u), trackingPool))
				.collect(Collectors.toList());
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	}

	/**
	 * Останавливает пул потоков. Вызывать при завершении приложения.
	 */
	public void shutdownTrackingPool() {
		trackingPool.shutdown();
	}

	/**
	 * Возвращает 5 ближайших аттракционов относительно локации пользователя.
	 * Расстояние считается через RewardsService.getDistance (в милях).
	 *
	 * @deprecated используйте {@link #getNearByAttractionsDto(VisitedLocation, User)}
	 *             для получения полного DTO с координатами и наградой.
	 */
	@Deprecated
	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		return gpsUtil.getAttractions().stream()
				.sorted(Comparator.comparingDouble(a -> rewardsService.getDistance(a, visitedLocation.location)))
				.limit(5)
				.collect(Collectors.toList());
	}

	/**
	 * Возвращает 5 ближайших аттракционов в виде DTO, содержащего:
	 * имя аттракциона, координаты аттракциона, координаты пользователя,
	 * расстояние в милях и награду за посещение.
	 */
	public List<NearByAttractionDto> getNearByAttractionsDto(VisitedLocation visitedLocation, User user) {
		return gpsUtil.getAttractions().stream()
				.sorted(Comparator.comparingDouble(a -> rewardsService.getDistance(a, visitedLocation.location)))
				.limit(5)
				.map(a -> new NearByAttractionDto(
						a.attractionName,
						a.latitude,
						a.longitude,
						visitedLocation.location.latitude,
						visitedLocation.location.longitude,
						rewardsService.getDistance(a, visitedLocation.location),
						rewardsService.getRewardPoints(a, user)))
				.collect(Collectors.toList());
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
				shutdownTrackingPool();
			}
		});
	}

	/**********************************************************************************
	 *
	 * Methods Below: For Internal Testing
	 *
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	private final Map<String, User> internalUserMap = new ConcurrentHashMap<>();

	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i -> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
					new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	private double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
}