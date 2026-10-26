package com.openclassrooms.tourguide.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.tourguide.helper.InternalTestHelper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TourGuideControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        // Создаем 1 тестового пользователя, чтобы не нагружать систему 100 000
        InternalTestHelper.setInternalUserNumber(1);
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Greetings from TourGuide!"));
    }

    @Test
    public void testGetLocation() throws Exception {
        mockMvc.perform(get("/getLocation")
                        .param("userName", "internalUser0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.location").exists());
    }

    @Test
    public void testGetNearbyAttractions() throws Exception {
        mockMvc.perform(get("/getNearbyAttractions")
                        .param("userName", "internalUser0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5)) // Проверяем, что вернулось ровно 5
                .andExpect(jsonPath("$[0].attractionName").exists())
                .andExpect(jsonPath("$[0].distanceInMiles").exists());
    }

    @Test
    public void testGetRewards() throws Exception {
        mockMvc.perform(get("/getRewards")
                        .param("userName", "internalUser0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetTripDeals() throws Exception {
        mockMvc.perform(get("/getTripDeals")
                        .param("userName", "internalUser0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}