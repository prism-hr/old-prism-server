package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_LANGUAGE_QUALIFICATION")
public class LanguageQualification implements Serializable {

    private static final long serialVersionUID = -2695332340505084549L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "qualification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguageQualificationEnum qualificationType;

    @Column(name = "qualification_type_other")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String qualificationTypeOther;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Column(name = "overall_score", nullable = false)
    private String overallScore;

    @Column(name = "reading_score", nullable = false)
    private String readingScore;

    @Column(name = "writing_score", nullable = false)
    private String writingScore;

    @Column(name = "speaking_score", nullable = false)
    private String speakingScore;

    @Column(name = "listening_score", nullable = false)
    private String listeningScore;

    @Column(name = "exam_online", nullable = false)
    private Boolean examOnline;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id", nullable = false)
    private Document proofOfAward;
    
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

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
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
    

    public LanguageQualification withQualificationType(LanguageQualificationEnum qualificationType) {
        this.qualificationType = qualificationType;
        return this;
    }

    public LanguageQualification withQualificationTypeOther(String qualificationTypeOther) {
        this.qualificationTypeOther = qualificationTypeOther;
        return this;
    }

    public LanguageQualification withExamDate(LocalDate examDate) {
        this.examDate = examDate;
        return this;
    }

    public LanguageQualification withOverallScore(String score) {
        this.overallScore = score;
        return this;
    }

    public LanguageQualification withReadingScore(String score) {
        this.readingScore = score;
        return this;
    }

    public LanguageQualification withWritingScore(String score) {
        this.writingScore = score;
        return this;
    }

    public LanguageQualification withSpeakingScore(String score) {
        this.speakingScore = score;
        return this;
    }

    public LanguageQualification withListeningScore(String score) {
        this.listeningScore = score;
        return this;
    }

    public LanguageQualification withExamOnline(Boolean online) {
        this.examOnline = online;
        return this;
    }
    
    public LanguageQualification withProofOfAward(Document proofOfAward) {
        this.proofOfAward = proofOfAward;
        return this;
    }

    
}
