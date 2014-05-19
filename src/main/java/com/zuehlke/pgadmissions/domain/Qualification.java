package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity
@Table(name = "APPLICATION_QUALIFICATION")
public class Qualification implements Serializable, FormSectionObject {

    private static final long serialVersionUID = -8949535622435302565L;

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "subject")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
    private String subject;

    @Column(name = "title")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
    private String title;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "award_date")
    private Date awardDate;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
    @Column(name = "qualification_language")
    private String language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualification_type_id")
    private QualificationType type;

    @Column(name = "grade")
    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
    private String grade;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id")
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private ImportedInstitution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @Column(name = "completed")
    private Boolean completed;

    @Column(name = "export")
    private Boolean export;

    @Transient
    private boolean acceptedTerms;

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

    public Date getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(Date awardDate) {
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getExport() {
        return export;
    }

    public void setExport(Boolean export) {
        this.export = export;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public Qualification withId(Integer id) {
        this.id = id;
        return this;
    }

    public Qualification withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Qualification withTitle(String title) {
        this.title = title;
        return this;
    }

    public Qualification withStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Qualification withAwardDate(Date awardDate) {
        this.awardDate = awardDate;
        return this;
    }

    public Qualification withLanguage(String language) {
        this.language = language;
        return this;
    }

    public Qualification withType(QualificationType type) {
        this.type = type;
        return this;
    }

    public Qualification withGrade(String grade) {
        this.grade = grade;
        return this;
    }

    public Qualification withDocument(Document document) {
        this.document = document;
        return this;
    }

    public Qualification withInstitution(ImportedInstitution institution) {
        this.institution = institution;
        return this;
    }

    public Qualification withApplication(Application application) {
        this.application = application;
        return this;
    }

    public Qualification withCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public Qualification withExport(Boolean export) {
        this.export = export;
        return this;
    }

}
