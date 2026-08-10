package com.codewithhitesh.talentmatchpro;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileStorageService {

    private final Tika tika;

    public FileStorageService() {
        this.tika = new Tika();
    }

    public String extractTextFromDocument(MultipartFile file) throws IOException, TikaException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Failed to process file: Uploaded file is empty.");
        }

        // Tika automatically detects the file type (PDF, DOCX, TXT) and parses raw text
        String extractedText = tika.parseToString(file.getInputStream());

        if (extractedText == null || extractedText.trim().isEmpty()) {
            throw new IllegalArgumentException("Could not extract any text from the provided file.");
        }

        return extractedText.trim();
    }
}