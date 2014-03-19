package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.enums.LanguageQualificationEnum;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Embeddable
public class LanguageQualification implements Serializable {

    private static final long serialVersionUID = -4188769453233574918L;

    @Column(name = "language_qualification_type")
    @Enumerated(EnumType.STRING)
    private LanguageQualificationEnum qualificationType;

    @Column(name = "language_qualification_type_name")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String qualificationTypeName;

    @Column(name = "language_exam_date")
    private Date examDate;

    @Column(name = "language_overall_score")
    private String overallScore;

    @Column(name = "language_reading_score")
    private String readingScore;

    @Column(name = "language_writing_score")
    private String writingScore;

    @Column(name = "language_speaking_score")
    private String speakingScore;

    @Column(name = "language_listening_score")
    private String listeningScore;

    @Column(name = "language_exam_online")
    private Boolean examOnline;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "language_qualification_document_id")
    private Document languageQualificationDocument;

    public LanguageQualificationEnum getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(LanguageQualificationEnum qualificationType) {
        this.qualificationType = qualificationType;
    }

    public String getQualificationTypeName() {
        return qualificationTypeName;
    }

    public void setQualificationTypeName(String qualificationTypeName) {
        this.qualificationTypeName = qualificationTypeName;
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

    public Document getLanguageQualificationDocument() {
        return languageQualificationDocument;
    }

    public void setLanguageQualificationDocument(Document languageQualificationDocument) {
        if (languageQualificationDocument != null) {
            languageQualificationDocument.setIsReferenced(true);
        }
        this.languageQualificationDocument = languageQualificationDocument;
    }

    @Override
    public String toString() {
        return String
                .format("LanguageQualification [qualificationType=%s, qualificationTypeName=%s, examDate=%s, overallScore=%s, readingScore=%s, writingScore=%s, speakingcore=%s, listeningScore=%s, examOnline=%s]",
                        qualificationType, qualificationTypeName, examDate, overallScore, readingScore, writingScore, speakingScore, listeningScore, examOnline);
    }

}
