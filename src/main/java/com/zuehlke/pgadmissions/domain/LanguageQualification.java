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

@Entity(name = "APPLICATION_FORM_PERSONAL_DETAIL_LANGUAGE_QUALIFICATIONS")
public class LanguageQualification implements Serializable {

    private static final long serialVersionUID = -4188769453233574918L;

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "language_qualification_document_id")
    private Document languageQualificationDocument;

    @OneToOne(mappedBy = "languageQualification")
    private PersonalDetails personalDetails;

    @Column(name = "qualification_type")
    @Enumerated(EnumType.STRING)
    LanguageQualificationEnum qualificationType;

    @Column(name = "other_qualification_type_name")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 100)
    private String otherQualificationTypeName;

    @Column(name = "date_of_examination")
    private Date dateOfExamination;

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

    @Column(name = "exam_taken_online")
    private Boolean examTakenOnline;

    public LanguageQualification() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public LanguageQualificationEnum getQualificationType() {
        return qualificationType;
    }

    public void setQualificationType(LanguageQualificationEnum qualificationType) {
        this.qualificationType = qualificationType;
    }

    public String getOtherQualificationTypeName() {
        return otherQualificationTypeName;
    }

    public void setOtherQualificationTypeName(String otherQualificationTypeName) {
        this.otherQualificationTypeName = otherQualificationTypeName;
    }

    public Date getDateOfExamination() {
        return dateOfExamination;
    }

    public void setDateOfExamination(Date dateOfExamination) {
        this.dateOfExamination = dateOfExamination;
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

    public void setSpeakingScore(String speakingcore) {
        this.speakingScore = speakingcore;
    }

    public String getListeningScore() {
        return listeningScore;
    }

    public void setListeningScore(String listeningScore) {
        this.listeningScore = listeningScore;
    }

    // freemarker convinience method
    public boolean isExamTakenOnlineSet() {
        return (examTakenOnline != null);
    }

    public Boolean getExamTakenOnline() {
        return examTakenOnline;
    }

    public void setExamTakenOnline(Boolean examTakenOnline) {
        this.examTakenOnline = examTakenOnline;
    }

    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }

    public Document getLanguageQualificationDocument() {
        return languageQualificationDocument;
    }

    public void setLanguageQualificationDocument(Document languageQualificationDocument) {
        languageQualificationDocument.setIsReferenced(true);
        this.languageQualificationDocument = languageQualificationDocument;
    }

    @Override
    public String toString() {
        return String
                .format("LanguageQualification [personalDetails=%s, qualificationType=%s, otherQualificationTypeName=%s, dateOfExamination=%s, overallScore=%s, readingScore=%s, writingScore=%s, speakingcore=%s, listeningScore=%s, examTakenOnline=%s]",
                        personalDetails, qualificationType, otherQualificationTypeName, dateOfExamination, overallScore, readingScore, writingScore,
                        speakingScore, listeningScore, examTakenOnline);
    }

}
