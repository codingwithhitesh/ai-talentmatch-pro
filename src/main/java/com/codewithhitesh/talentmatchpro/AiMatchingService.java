package com.codewithhitesh.talentmatchpro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AiMatchingService {

    private final JobService jobService;
    private final ResumeService resumeService;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    // Inject dependencies provided by Spring
    public AiMatchingService(
            JobService jobService,
            ResumeService resumeService,
            ChatClient.Builder chatClientBuilder,
            ObjectMapper objectMapper) {

        this.jobService = jobService;
        this.resumeService = resumeService;
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    // FEATURE 1:
    // Match one resume with one job
    public String matchResumeToJob(Long jobId, Long resumeId) {

        // Fetch Job and Resume
        Job job = jobService.getJobById(jobId);
        Resume resume = resumeService.getResumeById(resumeId);

        if (job == null || resume == null) {
            return "Given descriptions not found";
        }

        String jobDescription = job.getDescription();

        if (jobDescription == null || jobDescription.isBlank()) {
            return "JOB API not working. Please try again.";
        }

        String resumeDescription = resume.getExtractedText();

        if (resumeDescription == null || resumeDescription.isBlank()) {
            return "Resume API not working. Please try again.";
        }

        // Build AI prompt
        String prompt = """
                You are an expert HR and ATS evaluation engine.

                Compare the following Resume with the Job Description.

                JOB DESCRIPTION:
                %s

                RESUME TEXT:
                %s

                Evaluate the candidate and return the match percentage,
                matched skills, missing skills, and feedback.
                """.formatted(jobDescription, resumeDescription);

        // Send prompt to GROQ
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }


    // FEATURE 2:
    // Rank all resumes for one job
    public List<CandidateMatch> rankCandidates(Long jobId) {

        // Get selected job
        Job job = jobService.getJobById(jobId);

        // Get all resumes from database
        List<Resume> resumes = resumeService.getAllResumes();

        // Store AI results
        List<CandidateMatch> results = new ArrayList<>();

        // Compare every resume with the selected job
        for (Resume resume : resumes) {

            String prompt = """
                    You are an HR candidate ranking system.

                    Compare this resume with this job description.

                    JOB:
                    %s

                    RESUME:
                    %s

                    Return ONLY JSON in this format:

                    {
                      "score": 85,
                      "matchedSkills": ["Java", "Spring Boot"],
                      "missingSkills": ["Docker"],
                      "recommendation": "SHORTLIST",
                      "explanation": "Good match for the role."
                    }
                    """.formatted(
                    job.getDescription(),
                    resume.getExtractedText()
            );

            // Send prompt to GROQ
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            try {

                // Convert AI JSON response into CandidateMatch object
                CandidateMatch aiResult =
                        objectMapper.readValue(response, CandidateMatch.class);

                // Add database information
                aiResult.setResumeId(resume.getResumeId());
                aiResult.setCandidateName(resume.getCandidateName());
                aiResult.setFileName(resume.getFileName());

                results.add(aiResult);

            } catch (Exception e) {

                System.out.println(
                        "Could not process resume: "
                                + resume.getResumeId()
                );
            }
        }

        // Sort candidates from highest score to lowest
        results.sort(
                Comparator.comparingInt(CandidateMatch::getScore)
                        .reversed()
        );

        return results;
    }
}