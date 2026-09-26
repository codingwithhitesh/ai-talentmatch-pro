package com.codewithhitesh.talentmatchpro;

import java.util.List;

public class CandidateMatch {

    private Long resumeId;

    private String candidateName;

    private String fileName;

    private int score;

    private List<String> matchedSkills;

    private List<String> missingSkills;

    private String recommendation;

    private String explanation;


    public CandidateMatch() {
    }


    public CandidateMatch(
            Long resumeId,
            String candidateName,
            String fileName,
            int score,
            List<String> matchedSkills,
            List<String> missingSkills,
            String recommendation,
            String explanation
    ) {
        this.resumeId = resumeId;
        this.candidateName = candidateName;
        this.fileName = fileName;
        this.score = score;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.recommendation = recommendation;
        this.explanation = explanation;
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


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(
            List<String> matchedSkills
    ) {
        this.matchedSkills = matchedSkills;
    }


    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(
            List<String> missingSkills
    ) {
        this.missingSkills = missingSkills;
    }


    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(
            String recommendation
    ) {
        this.recommendation = recommendation;
    }


    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(
            String explanation
    ) {
        this.explanation = explanation;
    }
}