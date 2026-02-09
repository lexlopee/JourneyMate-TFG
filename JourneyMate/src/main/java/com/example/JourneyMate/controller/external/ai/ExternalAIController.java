package com.example.JourneyMate.controller.external.ai;

import com.example.JourneyMate.service.external.AIRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExternalAIController {

    private final AIRecommendationService aiService;

    @GetMapping("/recommend")
    public ResponseEntity<String> getRecommendation(
            @RequestParam String pref,
            @RequestParam String budget) {
        return ResponseEntity.ok(aiService.getRecommendation(pref, budget));
    }
}
