package com.codewithhitesh.talentmatchpro;

import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service

public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final FileStorageService fileStorageService;

    public ResumeService(ResumeRepository resumeRepository, FileStorageService fileStorageService) {
        this.resumeRepository = resumeRepository;
        this.fileStorageService = fileStorageService;
    }

    public Resume saveResumeInDB (MultipartFile file , String candidateName) throws TikaException, IOException {

        String extractedText = fileStorageService.extractTextFromDocument(file);

        Resume resume = new Resume();
        resume.setCandidateName(candidateName);
        resume.setFileName(file.getOriginalFilename());
        resume.setExtractedText(extractedText);

        return resumeRepository.save(resume);   // Saved

    }
    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found with id: " + id));
    }

    /* * NEW:
    * Get every resume stored in the database
    * Used by the candidate-ranking feature. */


    public List<Resume> getAllResumes() {
        return resumeRepository.findAll(); }

}
