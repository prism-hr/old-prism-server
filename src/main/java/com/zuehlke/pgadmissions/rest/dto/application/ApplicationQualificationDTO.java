package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.validation.annotation.DateNotAfterDate;
import com.zuehlke.pgadmissions.rest.validation.annotation.DateNotFuture;

@DateNotAfterDate(startDate = "startDate", endDate = "awardDate")
public class ApplicationQualificationDTO {

    private Integer id;

    @NotEmpty
    @Size(max = 200)
    private String subject;

    @NotEmpty
    @Size(max = 200)
    private String title;

    @DateNotFuture
    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate awardDate;

    @NotEmpty
    @Size(max = 70)
    private String language;

    @NotNull
    private Integer type;

    @NotEmpty
    @Size(max = 200)
    private String grade;

    private FileDTO document;

    @NotNull
    private ImportedInstitutionDTO institution;

    private Boolean completed;

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

}
