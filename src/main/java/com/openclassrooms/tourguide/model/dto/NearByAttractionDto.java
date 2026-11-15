package com.openclassrooms.tourguide.model.dto;

/**
 * Data Transfer Object (DTO) representing a nearby tourist attraction.
 * Used to transfer data to the client via the REST API without exposing
 * the internal model entities.
 */
public class NearByAttractionDto {

    /** Name of the tourist attraction. */
    public String attractionName;

    /** Latitude of the attraction. */
    public double attractionLatitude;

    /** Longitude of the attraction. */
    public double attractionLongitude;

    /** Latitude of the user's last known location. */
    public double userLatitude;

    /** Longitude of the user's last known location. */
    public double userLongitude;

    /** Distance between the user and the attraction, in miles. */
    public double distanceInMiles;

    /** Reward points earned for visiting the attraction. */
    public int rewardPoints;

    /**
     * Default constructor required for JSON serialization/deserialization.
     */
    public NearByAttractionDto() {
    }

    /**
     * Constructs a fully populated DTO.
     *
     * @param attractionName      Name of the attraction.
     * @param attractionLatitude  Latitude of the attraction.
     * @param attractionLongitude Longitude of the attraction.
     * @param userLatitude        Latitude of the user.
     * @param userLongitude       Longitude of the user.
     * @param distanceInMiles     Distance in miles.
     * @param rewardPoints        Reward points for visiting.
     */
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