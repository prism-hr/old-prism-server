package com.zuehlke.pgadmissions.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.joda.time.LocalDate;

@Entity
@Table(name = "APPLICATION_LANGUAGE_QUALIFICATION")
public class ApplicationLanguageQualification {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne
    @JoinColumn(name = "imported_language_qualification_type_id", nullable = false)
    private ImportedLanguageQualificationType languageQualificationType;

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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id", nullable = false)
    private Document proofOfAward;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ImportedLanguageQualificationType getLanguageQualificationType() {
        return languageQualificationType;
    }

    public void setLanguageQualificationType(ImportedLanguageQualificationType languageQualificationType) {
        this.languageQualificationType = languageQualificationType;
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

    public Document getProofOfAward() {
        return proofOfAward;
    }

    public void setProofOfAward(Document proofOfAward) {
        this.proofOfAward = proofOfAward;
    }

    public ApplicationLanguageQualification withLanguageQualificationType(ImportedLanguageQualificationType langaugeQualificationType) {
        this.languageQualificationType = langaugeQualificationType;
        return this;
    }

    public ApplicationLanguageQualification withExamDate(LocalDate examDate) {
        this.examDate = examDate;
        return this;
    }

    public ApplicationLanguageQualification withOverallScore(String score) {
        this.overallScore = score;
        return this;
    }

    public ApplicationLanguageQualification withReadingScore(String score) {
        this.readingScore = score;
        return this;
    }

    public ApplicationLanguageQualification withWritingScore(String score) {
        this.writingScore = score;
        return this;
    }

    public ApplicationLanguageQualification withSpeakingScore(String score) {
        this.speakingScore = score;
        return this;
    }

    public ApplicationLanguageQualification withListeningScore(String score) {
        this.listeningScore = score;
        return this;
    }
    
    public ApplicationLanguageQualification withProofOfAward(Document proofOfAward) {
        this.proofOfAward = proofOfAward;
        return this;
    }

}
