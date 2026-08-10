package com.codewithhitesh.talentmatchpro;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiMatchService {

    private final JobService jobService;
    private final ResumeService resumeService;
    private final ChatClient chatClient;

    // Inject ChatClient.Builder provided by Spring AI
    public AiMatchService(JobService jobService, ResumeService resumeService, ChatClient.Builder chatClientBuilder) {
        this.jobService = jobService;
        this.resumeService = resumeService;
        this.chatClient = chatClientBuilder.build();
    }

    public String matchResumeToJob(Long jobId, Long resumeId) {
        // 1. Fetch Job and Resume
        Job job = jobService.getJobById(jobId);
        Resume resume = resumeService.getResumeById(resumeId);

        // 2. Build the prompt
        String prompt = """
                You are an expert HR and ATS evaluation engine.
                Compare the following Resume with the Job Description.
                
                JOB DESCRIPTION:
                %s
                
                RESUME TEXT:
                %s
                
                Evaluate the candidate and return the match percentage, matched skills, missing skills, and feedback.
                """.formatted(job.getDescription(), resume.getExtractedText());

        // 3. Call Ollama via ChatClient and map the response automatically into MatchResultDto
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}