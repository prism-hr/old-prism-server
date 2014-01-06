package com.zuehlke.pgadmissions.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity(name = "REVIEW_COMMENT")
public class ReviewComment extends Comment {

    private static final long serialVersionUID = 9120577563568889651L;

    @OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.REMOVE })
    @org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
    @JoinColumn(name = "reviewer_id")
    private Reviewer reviewer;

    @Column(name = "willing_to_interview")
    private Boolean willingToInterview;

    @Column(name = "willing_to_work_with_applicant")
    private Boolean willingToWorkWithApplicant;

    @Column(name = "suitable_candidate")
    private Boolean suitableCandidateForUcl;

    @Column(name = "applicant_suitable_for_programme")
    private Boolean suitableCandidateForProgramme;

    @Column(name = "applicant_rating")
    private Integer applicantRating;

    @Column(name = "decline")
    private boolean decline;

    @Transient
    private String alert;

    public Reviewer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Reviewer reviewer) {
        this.reviewer = reviewer;
    }

    public boolean isWillingToInterviewSet() {
        return willingToInterview != null;
    }

    public Boolean getWillingToInterview() {
        return willingToInterview;
    }

    public void setWillingToInterview(Boolean willingToInterview) {
        this.willingToInterview = willingToInterview;
    }

    public Boolean getWillingToWorkWithApplicant() {
        return willingToWorkWithApplicant;
    }

    public void setWillingToWorkWithApplicant(final Boolean willingToWorkWithApplicant) {
        this.willingToWorkWithApplicant = willingToWorkWithApplicant;
    }

    public boolean isWillingToWorkWithApplicantSet() {
        return willingToWorkWithApplicant != null;
    }

    public boolean isSuitableCandidateSet() {
        return suitableCandidateForUcl != null;
    }

    public Boolean getSuitableCandidateForUcl() {
        return suitableCandidateForUcl;
    }

    public void setSuitableCandidateForUcl(Boolean suitableCandidate) {
        this.suitableCandidateForUcl = suitableCandidate;
    }

    public boolean isDecline() {
        return decline;
    }

    public void setDecline(boolean decline) {
        this.decline = decline;
    }

    public Boolean getSuitableCandidateForProgramme() {
        return suitableCandidateForProgramme;
    }

    public void setSuitableCandidateForProgramme(Boolean suitableCandidateForProgramme) {
        this.suitableCandidateForProgramme = suitableCandidateForProgramme;
    }

    public Integer getApplicantRating() {
        return applicantRating;
    }

    public void setApplicantRating(Integer applicantRating) {
        this.applicantRating = applicantRating;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }
    
    private void recomputeReviewRoundRating(BigDecimal rating) {
    	BigDecimal currentRating = this.getReviewer().getReviewRound().getAverageRating();
    }
    
    private void recomputeReviewRoundEndorsementTotals(Boolean endorsement) {
    	
    }
    
}