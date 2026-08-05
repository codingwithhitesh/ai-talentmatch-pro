package com.codewithhitesh.talentmatchpro;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    //ENUMS
    public enum Industry {
        MANUFACTURING, SOFTWARE, FINANCE, MEDICAL
    }
    public enum Role {
        FRESHER, JUNIOR, EXECUTIVE, MANAGER, MANAGEMENT
    }

    private String title;
    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String description; // Full Job Description text for AI prompt matching

    @Enumerated(EnumType.STRING)
    private Industry industry;

    private int experience;

    @Enumerated(EnumType.STRING)
    private Role role;

    protected Job() {
    }

    public Job(String title, String companyName, String description, Industry industry, Role role, int experience) {
        this.title = title;
        this.companyName = companyName;
        this.description = description;
        this.industry = industry;
        this.role = role;
        this.experience = experience;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Industry getIndustry() {
        return industry;
    }

    public void setIndustry(Industry industry) {
        this.industry = industry;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}