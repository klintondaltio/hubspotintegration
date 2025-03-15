package com.hubspot.hubspotintegration.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class HubSpotControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private HubSpotController hubSpotController;

    @Test
    void testGetAuthorizationUrl() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(hubSpotController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/hubspot/auth/url"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("mocked-auth-url"));
    }
}