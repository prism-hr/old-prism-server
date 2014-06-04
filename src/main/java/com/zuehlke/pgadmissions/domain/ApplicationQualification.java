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

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_QUALIFICATION")
public class ApplicationQualification {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "subject", nullable = false)
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
    private String subject;

    @Column(name = "title", nullable = false)
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
    private String title;

    @Column(name = "start_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate startDate;

    @Column(name = "award_date", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate awardDate;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
    @Column(name = "qualification_language", nullable = false)
    private String language;

    @ManyToOne
    @JoinColumn(name = "qualification_type_id", nullable = false)
    private QualificationType type;

    @Column(name = "grade", nullable = false)
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
    private String grade;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private ImportedInstitution institution;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false, insertable = false, updatable = false)
    private Application application;

    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @Column(name = "include_in_export", nullable = false)
    private boolean includeInExport = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String qualificationSubject) {
        this.subject = qualificationSubject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ImportedInstitution getInstitution() {
        return institution;
    }

    public void setInstitution(ImportedInstitution institution) {
        this.institution = institution;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public QualificationType getType() {
        return type;
    }

    public void setType(QualificationType type) {
        this.type = type;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean isIncludeInExport() {
        return includeInExport;
    }

    public void setIncludeInExport(Boolean export) {
        this.includeInExport = export;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ApplicationQualification withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationQualification withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public ApplicationQualification withTitle(String title) {
        this.title = title;
        return this;
    }

    public ApplicationQualification withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationQualification withAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
        return this;
    }

    public ApplicationQualification withLanguage(String language) {
        this.language = language;
        return this;
    }

    public ApplicationQualification withType(QualificationType type) {
        this.type = type;
        return this;
    }

    public ApplicationQualification withGrade(String grade) {
        this.grade = grade;
        return this;
    }

    public ApplicationQualification withDocument(Document document) {
        this.document = document;
        return this;
    }

    public ApplicationQualification withInstitution(ImportedInstitution institution) {
        this.institution = institution;
        return this;
    }

    public ApplicationQualification withApplication(Application application) {
        this.application = application;
        return this;
    }

    public ApplicationQualification withCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public ApplicationQualification withIncludeInExport(Boolean export) {
        this.includeInExport = export;
        return this;
    }

}
