package com.hubspot.hubspotintegration.controller;

import com.hubspot.hubspotintegration.service.HubSpotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HubSpotControllerTest {

    @Mock
    private HubSpotService hubSpotService;

    @InjectMocks
    private HubSpotController hubSpotController;

    @BeforeEach
    void setUp() {
        // Pode ser utilizado se houver alguma configuração inicial necessária
    }

    @Test
    void getAuthorizationUrl_ShouldReturnAuthUrl() {
        String expectedUrl = "https://auth.hubspot.com";
        when(hubSpotService.generateAuthorizationUrl()).thenReturn(expectedUrl);

        ResponseEntity<String> response = hubSpotController.getAuthorizationUrl();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedUrl, response.getBody());
    }

    @Test
    void handleOAuthCallback_ShouldReturnToken() {
        String code = "authCode123";
        String expectedToken = "accessTokenXYZ";
        when(hubSpotService.exchangeAuthCodeForToken(code)).thenReturn(expectedToken);

        ResponseEntity<String> response = hubSpotController.handleOAuthCallback(code);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedToken, response.getBody());
    }

    @Test
    void handleOAuthCallback_ShouldHandleException() {
        String code = "invalidCode";
        when(hubSpotService.exchangeAuthCodeForToken(code)).thenThrow(new RuntimeException("Invalid auth code"));

        ResponseEntity<String> response = hubSpotController.handleOAuthCallback(code);

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void createContact_ShouldReturnSuccessResponse() {
        Map<String, Object> contactData = new HashMap<>();
        contactData.put("email", "test@example.com");
        String expectedResponse = "Contact Created";
        when(hubSpotService.createContact(contactData)).thenReturn(expectedResponse);

        ResponseEntity<String> response = hubSpotController.createContact(contactData);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void createContact_ShouldHandleException() {
        Map<String, Object> contactData = new HashMap<>();
        when(hubSpotService.createContact(contactData)).thenThrow(new RuntimeException("Invalid contact data"));

        ResponseEntity<String> response = hubSpotController.createContact(contactData);

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void handleWebhook_ShouldReturnSuccessMessage() {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("event", "contact.created");

        doNothing().when(hubSpotService).processWebhook(eventData);

        ResponseEntity<String> response = hubSpotController.handleWebhook(eventData);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Webhook processed successfully", response.getBody());
    }

    @Test
    void handleWebhook_ShouldHandleException() {
        Map<String, Object> eventData = new HashMap<>();
        doThrow(new RuntimeException("Webhook processing failed")).when(hubSpotService).processWebhook(eventData);

        ResponseEntity<String> response = hubSpotController.handleWebhook(eventData);

        assertEquals(500, response.getStatusCodeValue());
    }
}
