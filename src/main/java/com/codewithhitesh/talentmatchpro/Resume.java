package com.codewithhitesh.talentmatchpro;

import jakarta.persistence.*;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resumeId;


    //ENUMS
    public enum Industry {
        MANUFACTURING, SOFTWARE, FINANCE, MEDICAL
    }
    public enum Role {
        FRESHER, JUNIOR, EXECUTIVE, MANAGER, MANAGEMENT
    }


    private String candidateName;
    private String fileName; // Name of uploaded PDF/Docx file

    @Column(columnDefinition = "TEXT")
    private String rawTextContent; // Text parsed by Tika for Spring AI processing

    @Enumerated(EnumType.STRING)
    private Industry industry;

    private int experience;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "TEXT")
    private String extractedText;


    protected Resume() {
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public Resume(String extractedText, Role role, int experience, Industry industry, String rawTextContent, String fileName, String candidateName) {
        this.extractedText = extractedText;
        this.role = role;
        this.experience = experience;
        this.industry = industry;
        this.rawTextContent = rawTextContent;
        this.fileName = fileName;
        this.candidateName = candidateName;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRawTextContent() {
        return rawTextContent;
    }

    public void setRawTextContent(String rawTextContent) {
        this.rawTextContent = rawTextContent;
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