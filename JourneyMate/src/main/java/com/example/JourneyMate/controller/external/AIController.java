package com.example.JourneyMate.controller.external;

import com.example.JourneyMate.service.external.AIRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIRecommendationService aiService;

    @GetMapping("/recommend")
    public ResponseEntity<String> getRecommendation(
            @RequestParam String pref,
            @RequestParam String budget) {
        return ResponseEntity.ok(aiService.getRecommendation(pref, budget));
    }
}
