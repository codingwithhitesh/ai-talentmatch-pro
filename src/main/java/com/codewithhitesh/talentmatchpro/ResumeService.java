package com.codewithhitesh.talentmatchpro;

import org.apache.james.mime4j.dom.Multipart;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

}
