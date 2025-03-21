package com.hubspot.hubspotintegration.controller;

import com.hubspot.hubspotintegration.service.HubSpotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/hubspot")
public class HubSpotController {
    private static final Logger logger = LoggerFactory.getLogger(HubSpotController.class);
    private final HubSpotService hubSpotService;

    public HubSpotController(HubSpotService hubSpotService) {
        this.hubSpotService = hubSpotService;
    }

    @GetMapping("/auth/url")
    public ResponseEntity<String> getAuthorizationUrl() {
        String authUrl = hubSpotService.generateAuthorizationUrl();
        return ResponseEntity.ok(authUrl);
    }

    @PostMapping("/auth/callback")
    public ResponseEntity<String> handleOAuthCallback(@RequestParam String code) {
        try {
            String token = hubSpotService.exchangeAuthCodeForToken(code);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing OAuth callback");
        }
    }

    @PostMapping("/contacts")
    public ResponseEntity<String> createContact(@RequestBody Map<String, Object> contactData) {
        try {
            String response = hubSpotService.createContact(contactData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating contact");
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> eventData) {
        try {
            hubSpotService.processWebhook(eventData);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing webhook");
        }
    }
}
