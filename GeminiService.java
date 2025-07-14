package com.example.demo.service;

import com.example.demo.model.GeminiRequest;
import com.example.demo.model.GeminiResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.ArrayList;

@Service
public class GeminiService {

    private final String API_KEY = "Your_api_key";
    private final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public GeminiResponse generateReply(GeminiRequest incomingRequest) {
        // Extract user email content
        String originalEmailText = incomingRequest.getContents()
                .get(0).getParts().get(0).getText();

        // Build a better prompt with context
        List<GeminiRequest.Content> contents = new ArrayList<>();

        // Instruction: behave like you're writing the reply
        GeminiRequest.Content systemPrompt = new GeminiRequest.Content();
        systemPrompt.setRole("user");
        systemPrompt.setParts(List.of(
                new GeminiRequest.Part("You are an assistant that writes professional email replies. Reply to the following message as if you are the recipient, keeping the tone polite and professional.")
        ));

        // Actual email content
        GeminiRequest.Content userEmail = new GeminiRequest.Content();
        userEmail.setRole("user");
        userEmail.setParts(List.of(new GeminiRequest.Part(originalEmailText)));

        contents.add(systemPrompt);
        contents.add(userEmail);

        GeminiRequest preparedRequest = new GeminiRequest();
        preparedRequest.setContents(contents);

        // Call Gemini API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GeminiRequest> entity = new HttpEntity<>(preparedRequest, headers);

        ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                entity,
                GeminiResponse.class
        );

        return response.getBody();
    }
}
