package com.openclassrooms.tourguide.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class NearByAttractionDtoTest {

    @Test
    public void noArgsConstructor() {
        NearByAttractionDto dto = new NearByAttractionDto();
        assertNotNull(dto);
    }

    @Test
    public void allArgsConstructor() {
        NearByAttractionDto dto = new NearByAttractionDto(
                "Eiffel Tower", 48.8584, 2.2945, 48.8566, 2.3522, 1.5, 100
        );

        assertEquals("Eiffel Tower", dto.attractionName);
        assertEquals(48.8584, dto.attractionLatitude);
        assertEquals(2.2945, dto.attractionLongitude);
        assertEquals(48.8566, dto.userLatitude);
        assertEquals(2.3522, dto.userLongitude);
        assertEquals(1.5, dto.distanceInMiles);
        assertEquals(100, dto.rewardPoints);
    }
}