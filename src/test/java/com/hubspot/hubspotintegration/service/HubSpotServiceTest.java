package com.hubspot.hubspotintegration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HubSpotServiceTest {

    @InjectMocks
    private HubSpotService hubSpotService;

    @BeforeEach
    void setUp() {
        hubSpotService = new HubSpotService();
    }

    @Test
    void processWebhook_ShouldCreateContactForValidEvent() {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "contact.creation");
        eventData.put("email", "test@example.com");
        eventData.put("firstname", "John");
        eventData.put("lastname", "Doe");

        HubSpotService spyService = Mockito.spy(hubSpotService);
        doReturn("Contact Created").when(spyService).createContact(any());

        spyService.processWebhook(eventData);

        verify(spyService, times(1)).createContact(any());
    }

    @Test
    void processWebhook_ShouldIgnoreInvalidEvent() {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", "deal.creation");

        HubSpotService spyService = Mockito.spy(hubSpotService);
        spyService.processWebhook(eventData);

        verify(spyService, never()).createContact(any());
    }

    @Test
    void createContact_ShouldThrowException_WhenContactDataIsEmpty() {
        assertThrows(RuntimeException.class, () -> hubSpotService.createContact(new HashMap<>()), "Invalid contact data");
    }
}
