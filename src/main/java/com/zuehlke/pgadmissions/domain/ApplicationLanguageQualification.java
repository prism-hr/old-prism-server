package com.zuehlke.pgadmissions.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

@Entity
@Table(name = "APPLICATION_LANGUAGE_QUALIFICATION")
public class ApplicationLanguageQualification {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "imported_language_qualification_type_id", nullable = false)
    private LanguageQualificationType type;

    @Column(name = "exam_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
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
    private Document document;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LanguageQualificationType getType() {
        return type;
    }

    public void setType(LanguageQualificationType type) {
        this.type = type;
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

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public ApplicationLanguageQualification withType(LanguageQualificationType type) {
        this.type = type;
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
    
    public ApplicationLanguageQualification withDocument(Document document) {
        this.document = document;
        return this;
    }

}
