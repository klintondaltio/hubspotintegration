package com.hubspot.hubspotintegration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class HubSpotService {
    private static final Logger logger = LoggerFactory.getLogger(HubSpotService.class);

    @Value("${hubspot.client.id}")
    private String clientId;

    @Value("${hubspot.redirect.uri}")
    private String redirectUri;

    @Value("${hubspot.client.secret}")
    private String clientSecret;

    @Value("${hubspot.scopes}")
    private String scopes;

    public String generateAuthorizationUrl() {
        String formattedScopes = scopes.replace(",", "%20");
        return String.format("https://app.hubspot.com/oauth/authorize?client_id=%s&redirect_uri=%s&scope=%s", clientId, redirectUri, formattedScopes);
    }

    public String exchangeAuthCodeForToken(String code) {
        logger.info("Exchanging authorization code for access token");

        String tokenUrl = "https://api.hubapi.com/oauth/v1/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("access_token")) {
            return (String) responseBody.get("access_token");
        } else {
            throw new RuntimeException("Failed to retrieve access token");
        }
    }

    public String createContact(Map<String, Object> contactData) {
        if (contactData == null || contactData.isEmpty()) {
            throw new RuntimeException("Invalid contact data");
        }

        String createContactUrl = "https://api.hubapi.com/contacts/v1/contact";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken()); // Assumes you have a method to get the access token

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(contactData, headers);

        int maxRetries = 3;
        int retryCount = 0;
        int backoff = 1000; // Initial backoff time in milliseconds

        while (retryCount < maxRetries) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(createContactUrl, request, String.class);
                if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                    return response.getBody();
                } else if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    retryCount++;
                    Thread.sleep(backoff);
                    backoff *= 2; // Exponential backoff
                } else {
                    throw new RuntimeException("Failed to create contact: " + response.getStatusCode());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted", e);
            }
        }

        throw new RuntimeException("Failed to create contact after " + maxRetries + " retries");
    }

    public void processWebhook(Map<String, Object> eventData) {
        logger.info("Processing webhook event: {}", eventData);

        if (eventData == null || !eventData.containsKey("eventType") || !"contact.creation".equals(eventData.get("eventType"))) {
            logger.warn("Ignoring non-contact.creation event");
            return;
        }

        Map<String, Object> contactData = extractContactData(eventData);
        if (contactData != null) {
            createContact(contactData);
        } else {
            logger.warn("Failed to extract contact data from event");
        }
    }

    private String getAccessToken() {
        // Implement the logic to retrieve the access token
        return "CLfggtzZMhIHAAEAQAAAARja7c8XIJaFtiUo5PC9BDIUtMiqwmI-XdbTn2um_NxlOfTMm6M6MAAAAEEAAAAAAAAAAAAAAAAAgAAAAAAAAAAAACAAAAAAAOABAAAAAAAAAAAAAAAQAkIUjdqqpeeJsBpUMNf7bp9FIm0B339KA25hMVIAWgBgAGiWhbYlcAA";
    }

    private Map<String, Object> extractContactData(Map<String, Object> eventData) {
        // Implement the logic to extract contact data from the eventData
        // This is a placeholder implementation and should be adjusted based on the actual structure of eventData
        Map<String, Object> contactData = new HashMap<>();
        contactData.put("email", eventData.get("email"));
        contactData.put("firstname", eventData.get("firstname"));
        contactData.put("lastname", eventData.get("lastname"));
        return contactData;
    }
}