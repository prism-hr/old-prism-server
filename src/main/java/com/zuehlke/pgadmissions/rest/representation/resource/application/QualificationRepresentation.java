package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.resource.FileRepresentation;

public class QualificationRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private String subject;

    private String title;

    private LocalDate startDate;

    private LocalDate awardDate;

    private String language;

    private Integer type;

    private String grade;

    private FileRepresentation document;

    private ImportedInstitutionRepresentation institution;

    private Boolean completed;

    private Boolean includeInExport;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public FileRepresentation getDocument() {
        return document;
    }

    public void setDocument(FileRepresentation document) {
        this.document = document;
    }

    public ImportedInstitutionRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(ImportedInstitutionRepresentation institution) {
        this.institution = institution;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Boolean getIncludeInExport() {
        return includeInExport;
    }

    public void setIncludeInExport(Boolean includeInExport) {
        this.includeInExport = includeInExport;
    }
}
