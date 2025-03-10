package com.example.controller;

import com.example.service.GeminiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Add this for development
public class GeminiController {
    
    private final GeminiService geminiService;
    
    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }
    
    @PostMapping("/generate")
    public String generateResponse(
        @RequestParam String prompt, 
        @RequestParam(defaultValue = "100") int maxTokens
    ) {
        return geminiService.generateResponse(prompt, maxTokens);
    }
}