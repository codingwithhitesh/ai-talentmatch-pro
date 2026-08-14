package com.codewithhitesh.talentmatchpro;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/match")
public class AiMatchingController {

    private final AiMatchingService aiMatchService;

    public AiMatchingController(AiMatchingService aiMatchService) {
        this.aiMatchService = aiMatchService;
    }

    @PostMapping
    public ResponseEntity<String> calculateMatch(
            @RequestParam Long jobId,
            @RequestParam Long resumeId) {

        String result = aiMatchService.matchResumeToJob(jobId, resumeId);
        return ResponseEntity.ok(result);
    }
}