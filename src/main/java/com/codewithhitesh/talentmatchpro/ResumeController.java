package com.codewithhitesh.talentmatchpro;

import org.apache.tika.exception.TikaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Resume> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("candidateName") String candidateName) throws IOException, TikaException {

        Resume savedResume = resumeService.saveResumeInDB(file, candidateName);
        return new ResponseEntity<>(savedResume, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResumeById(@PathVariable Long id) {
        return ResponseEntity.ok(resumeService.getResumeById(id));
    }


    /*   UPDATE 2.0
    * * NEW: * Get all resumes. *
    * * GET /api/resumes */
    @GetMapping
    public ResponseEntity<List<Resume>> getAllResumes() {
        return ResponseEntity.ok( resumeService.getAllResumes() ); }
}