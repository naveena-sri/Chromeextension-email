package com.example.demo.controller;

import com.example.demo.model.GeminiRequest;
import com.example.demo.model.GeminiResponse;
import com.example.demo.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailReplyController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate")
    public GeminiResponse generateReply(@RequestBody GeminiRequest request) {
        return geminiService.generateReply(request);
    }
}
