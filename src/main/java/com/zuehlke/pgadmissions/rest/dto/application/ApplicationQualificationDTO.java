package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.validation.annotation.DateNotAfterDate;
import com.zuehlke.pgadmissions.rest.validation.annotation.DatePast;
import org.joda.time.LocalDate;

@DateNotAfterDate(startDate = "startDate", endDate = "awardDate")
public class ApplicationQualificationDTO {

    private Integer id;

    @NotEmpty
    private String subject;

    private String title;

    @DatePast
    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate awardDate;

    @NotEmpty
    private String language;

    @NotNull
    private Integer type;

    @NotEmpty
    private String grade;

    private FileDTO document;

    @NotNull
    private ImportedInstitutionDTO institution;

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

    public FileDTO getDocument() {
        return document;
    }

    public void setDocument(FileDTO document) {
        this.document = document;
    }

    public ImportedInstitutionDTO getInstitution() {
        return institution;
    }

    public void setInstitution(ImportedInstitutionDTO institution) {
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
