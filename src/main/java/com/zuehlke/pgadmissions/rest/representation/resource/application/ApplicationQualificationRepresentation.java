package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedProgramRepresentation;

public class ApplicationQualificationRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private ImportedProgramRepresentation programMapping;

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

    public Integer getProgram() {
        return programMapping.getId();
    }
    
    public void setProgram(Integer program) {
        this.programMapping = new ImportedProgramRepresentation().withId(program);
    }
    
    public ImportedProgramRepresentation getProgramMapping() {
        return programMapping;
    }

    public void setProgramMapping(ImportedProgramRepresentation programMapping) {
        this.programMapping = programMapping;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

}
