package com.codewithhitesh.talentmatchpro;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DemoResumeLoader implements CommandLineRunner {

    private final ResumeRepository resumeRepository;
    private final FileStorageService fileStorageService;

    public DemoResumeLoader(
            ResumeRepository resumeRepository,
            FileStorageService fileStorageService) {

        this.resumeRepository = resumeRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void run(String... args) throws Exception {

        // Don't load demo resumes if database already contains resumes
        if (resumeRepository.count() > 0) {
            System.out.println(
                    "Resumes already exist. Skipping demo resume loading."
            );
            return;
        }

        System.out.println("Loading demo resumes...");

        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();

        Resource[] resources =
                resolver.getResources("classpath:/Demo_Resumes/*.pdf");

        if (resources.length == 0) {
            System.out.println("No demo resumes found.");
            return;
        }

        for (Resource resource : resources) {

            String fileName = resource.getFilename();

            if (fileName == null) {
                continue;
            }

            try (InputStream inputStream = resource.getInputStream()) {

                // Extract text from PDF
                String extractedText =
                        fileStorageService.extractTextFromInputStream(
                                inputStream,
                                fileName
                        );

                Resume resume = new Resume();

                resume.setCandidateName(
                        getCandidateName(fileName)
                );

                resume.setFileName(fileName);

                resume.setExtractedText(extractedText);

                resumeRepository.save(resume);

                System.out.println(
                        "Loaded demo resume: " + fileName
                );

            } catch (Exception e) {

                System.out.println(
                        "Failed to load resume: "
                                + fileName
                );

                e.printStackTrace();
            }
        }

        System.out.println("Demo resume loading completed.");
    }

    private String getCandidateName(String fileName) {

        // For now, use filename as candidate name
        // We can later extract the actual name from CV text.

        String name = fileName
                .replace(".pdf", "")
                .replace("_", " ")
                .replace("-", " ");

        return name;
    }
}