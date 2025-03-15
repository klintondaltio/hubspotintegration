package com.hubspot.hubspotintegration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
@ExtendWith(SpringExtension.class)
@SpringBootTest
class HubSpotServiceTest {
    @InjectMocks
    private HubSpotService hubSpotService;

    @Test
    void testExchangeAuthCodeForToken_Success() {
        String token = hubSpotService.exchangeAuthCodeForToken("valid-code");
        assertEquals("mocked-access-token", token);
    }

    @Test
    void testExchangeAuthCodeForToken_Failure() {
        assertThrows(RuntimeException.class, () -> hubSpotService.exchangeAuthCodeForToken(""));
    }

    @Test
    void testCreateContact_Success() {
        Map<String, Object> contactData = new HashMap<>();
        contactData.put("name", "Test Contact");
        String response = hubSpotService.createContact(contactData);
        assertEquals("mocked-contact-response", response);
    }

    @Test
    void testCreateContact_Failure() {
        assertThrows(RuntimeException.class, () -> hubSpotService.createContact(new HashMap<>()));
    }
}