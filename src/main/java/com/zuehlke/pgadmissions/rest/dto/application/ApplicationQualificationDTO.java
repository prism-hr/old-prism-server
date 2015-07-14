package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.utils.validation.DateNotAfterDate;
import uk.co.alumeni.prism.utils.validation.DateNotFuture;

import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedProgramDTO;

@DateNotAfterDate(startDate = "startDate", endDate = "awardDate")
public class ApplicationQualificationDTO {

    private Integer id;

    @NotEmpty
    @Size(max = 200)
    private String subject;

    @NotNull
    private ImportedProgramDTO program;

    @DateNotFuture
    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate awardDate;

    @NotEmpty
    @Size(max = 70)
    private String language;

    @NotEmpty
    @Size(max = 200)
    private String grade;

    private FileDTO document;

    private Boolean completed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ImportedProgramDTO getProgram() {
        return program;
    }

    public void setProgram(ImportedProgramDTO program) {
        this.program = program;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

}
