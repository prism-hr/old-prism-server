package com.zuehlke.pgadmissions.domain.application;

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
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.imported.ImportedLanguageQualificationType;

@Entity
@Table(name = "APPLICATION_LANGUAGE_QUALIFICATION")
public class ApplicationLanguageQualification extends ApplicationSection {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "languageQualification")
    private ApplicationPersonalDetail personalDetail;

    @ManyToOne
    @JoinColumn(name = "language_qualification_type_id")
    private ImportedLanguageQualificationType type;

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
    @JoinColumn(name = "document_id", unique = true)
    private Document document;

    @Column(name = "last_updated_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastUpdatedTimestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public final ApplicationPersonalDetail getPersonalDetail() {
        return personalDetail;
    }

    public final void setPersonalDetail(ApplicationPersonalDetail personalDetail) {
        this.personalDetail = personalDetail;
    }

    public ImportedLanguageQualificationType getType() {
        return type;
    }

    public void setType(ImportedLanguageQualificationType type) {
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

    @Override
    public DateTime getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    @Override
    public void setLastUpdatedTimestamp(DateTime lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public ApplicationLanguageQualification withType(ImportedLanguageQualificationType type) {
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

    public String getExamDateDisplay(String dateFormat) {
        return examDate == null ? null : examDate.toString(dateFormat);
    }

    public String getTypeDisplay() {
        return type == null ? null : type.getName();
    }

}
