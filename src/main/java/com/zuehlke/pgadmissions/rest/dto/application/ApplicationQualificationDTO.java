package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.rest.representation.application.FileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.application.ImportedInstitutionRepresentation;
import org.joda.time.DateTime;

public class ApplicationQualificationDTO {

    private Integer id;

    private String subject;

    private String title;

    private DateTime startDate;

    private DateTime awardDate;

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

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(DateTime awardDate) {
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
