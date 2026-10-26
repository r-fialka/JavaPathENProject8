package com.openclassrooms.tourguide.model.dto;

public class NearByAttractionDto {

    public String attractionName;
    public double attractionLatitude;
    public double attractionLongitude;
    public double userLatitude;
    public double userLongitude;
    public double distanceInMiles;
    public int rewardPoints;

    public NearByAttractionDto() {
    }

    public NearByAttractionDto(String attractionName,
                               double attractionLatitude, double attractionLongitude,
                               double userLatitude, double userLongitude,
                               double distanceInMiles, int rewardPoints) {
        this.attractionName = attractionName;
        this.attractionLatitude = attractionLatitude;
        this.attractionLongitude = attractionLongitude;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.distanceInMiles = distanceInMiles;
        this.rewardPoints = rewardPoints;
    }
}