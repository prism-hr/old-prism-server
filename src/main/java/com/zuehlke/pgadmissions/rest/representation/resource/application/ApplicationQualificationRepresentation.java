package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedProgramRepresentation;

public class ApplicationQualificationRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private ImportedProgramRepresentation program;

    private LocalDate startDate;

    private LocalDate awardDate;

    private String language;

    private String grade;

    private DocumentRepresentation document;

    private Boolean completed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public ImportedProgramRepresentation getProgram() {
        return program;
    }

    public void setProgram(ImportedProgramRepresentation program) {
        this.program = program;
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

    public DocumentRepresentation getDocument() {
        return document;
    }

    public void setDocument(DocumentRepresentation document) {
        this.document = document;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
    
    public ApplicationQualificationRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }
    
    public ApplicationQualificationRepresentation withProgram(ImportedProgramRepresentation program) {
        this.program = program;
        return this;
    }
    
    public ApplicationQualificationRepresentation withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationQualificationRepresentation withAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
        return this;
    }
    
    public ApplicationQualificationRepresentation withLanguage(String language) {
        this.language = language;
        return this;
    }
    
    public ApplicationQualificationRepresentation withGrade(String grade) {
        this.grade = grade;
        return this;
    }
    
    public ApplicationQualificationRepresentation withDocumentRepresentation(DocumentRepresentation document) {
        this.document = document;
        return this;
    }
    
    public ApplicationQualificationRepresentation withCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }
    
}
