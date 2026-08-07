package com.codewithhitesh.talentmatchpro;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    public Job updateJob(Job updatedJob , Long id) {

        Job existingJob = jobRepository
                                       .findById(id)
                                       .orElseThrow((() -> new RuntimeException("Job not found with id: " + id)));

        existingJob.setTitle(updatedJob.getTitle());
        existingJob.setDescription(updatedJob.getDescription());
        existingJob.setRole(updatedJob.getRole());
        existingJob.setExperience(updatedJob.getExperience());

        return jobRepository.save(existingJob);
    }


    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new RuntimeException("Job" + id + " does not exist");
        }
        jobRepository.delete(id);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + id));
    }
}