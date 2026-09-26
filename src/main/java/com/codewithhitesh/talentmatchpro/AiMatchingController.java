package com.codewithhitesh.talentmatchpro;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // ============================================================ // FEATURE 2 // NEW CANDIDATE RANKING //
    // ============================================================ /* * New endpoint. * * POST: *
    // /api/match/rank?jobId=1 * *
    // This evaluates every resume against the job * and returns candidates sorted by score. */

    @PostMapping("/rank")

    public ResponseEntity<List<CandidateMatch>> rankCandidates(@RequestParam Long jobId )
    {
        List<CandidateMatch> results = aiMatchService.rankCandidates(jobId);
        return ResponseEntity.ok(results);
    }
}