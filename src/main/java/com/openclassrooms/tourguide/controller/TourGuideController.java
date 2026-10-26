package com.openclassrooms.tourguide.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.model.dto.NearByAttractionDto;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

import tripPricer.Provider;

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @GetMapping("/getLocation")
    public VisitedLocation getLocation(@RequestParam String userName) {
        return tourGuideService.getUserLocation(getUser(userName));
    }

    /**
     * Возвращает 5 ближайших аттракционов к последней локации пользователя.
     * Для каждого: имя, координаты аттракциона, координаты пользователя,
     * расстояние в милях и награда за посещение.
     */
    @GetMapping("/getNearbyAttractions")
    public List<NearByAttractionDto> getNearbyAttractions(@RequestParam String userName) {
        User user = getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        return tourGuideService.getNearByAttractionsDto(visitedLocation, user);
    }

    @GetMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        return tourGuideService.getUserRewards(getUser(userName));
    }

    @GetMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
        return tourGuideService.getTripDeals(getUser(userName));
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }
}