package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.imported.ImportedProgramRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.FileRepresentation;

public class QualificationRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private ImportedProgramRepresentation program;

    private LocalDate startDate;

    private LocalDate awardDate;

    private String language;

    private String grade;

    private FileRepresentation document;

    private Boolean completed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public FileRepresentation getDocument() {
        return document;
    }

    public void setDocument(FileRepresentation document) {
        this.document = document;
    }

    public ImportedProgramRepresentation getProgram() {
        return program;
    }

    public void setProgram(ImportedProgramRepresentation program) {
        this.program = program;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

}
