package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name = "APPLICATION_FORM_LANGUAGE_QUALIFICATION")
public class LanguageQualification implements Serializable {

    private static final long serialVersionUID = -2695332340505084549L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "qualification_type")
    @Enumerated(EnumType.STRING)
    private LanguageQualificationEnum qualificationType;

    @Column(name = "qualification_type_other")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String qualificationTypeOther;

    @Column(name = "exam_date")
    private Date examDate;

    @Column(name = "overall_score")
    private String overallScore;

    @Column(name = "reading_score")
    private String readingScore;

    @Column(name = "writing_score")
    private String writingScore;

    @Column(name = "speaking_score")
    private String speakingScore;

    @Column(name = "listening_score")
    private String listeningScore;

    @Column(name = "exam_online")
    private Boolean examOnline;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id")
    private Document proofOfAward = null;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LanguageQualificationEnum getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(LanguageQualificationEnum qualificationType) {
        this.qualificationType = qualificationType;
    }

    public String getQualificationTypeOther() {
        return qualificationTypeOther;
    }

    public void setQualificationTypeOther(String qualificationTypeOther) {
        this.qualificationTypeOther = qualificationTypeOther;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public String getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(String overallScore) {
        this.overallScore = overallScore;
    }

    public String getReadingScore() {
        return readingScore;
    }

    public void setReadingScore(String readingScore) {
        this.readingScore = readingScore;
    }

    public String getWritingScore() {
        return writingScore;
    }

    public void setWritingScore(String writingScore) {
        this.writingScore = writingScore;
    }

    public String getSpeakingScore() {
        return speakingScore;
    }

    public void setSpeakingScore(String speakingScore) {
        this.speakingScore = speakingScore;
    }

    public String getListeningScore() {
        return listeningScore;
    }

    public void setListeningScore(String listeningScore) {
        this.listeningScore = listeningScore;
    }

    public Boolean getExamOnline() {
        return examOnline;
    }

    public void setExamOnline(Boolean examOnline) {
        this.examOnline = examOnline;
    }

    public Document getProofOfAward() {
        return proofOfAward;
    }

    public void setProofOfAward(Document proofOfAward) {
        this.proofOfAward = proofOfAward;
    }
    
}
