package com.zuehlke.pgadmissions.rest.representation.profile;

import static com.zuehlke.pgadmissions.PrismConstants.BACK_SLASH;

import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationActivity;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSectionRepresentation;

public class ProfileQualificationRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private ResourceRepresentationActivity resource;

    private Integer startYear;

    private Integer startMonth;

    private Integer awardYear;

    private Integer awardMonth;

    private String grade;

    private Boolean completed;

    private DocumentRepresentation document;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ResourceRepresentationActivity getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationActivity resource) {
        this.resource = resource;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

    public Integer getAwardYear() {
        return awardYear;
    }

    public void setAwardYear(Integer awardYear) {
        this.awardYear = awardYear;
    }

    public Integer getAwardMonth() {
        return awardMonth;
    }

    public void setAwardMonth(Integer awardMonth) {
        this.awardMonth = awardMonth;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public DocumentRepresentation getDocument() {
        return document;
    }

    public void setDocument(DocumentRepresentation document) {
        this.document = document;
    }

    public ProfileQualificationRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public ProfileQualificationRepresentation withProgram(ResourceRepresentationActivity program) {
        this.resource = program;
        return this;
    }

    public ProfileQualificationRepresentation withStartYear(Integer startYear) {
        this.startYear = startYear;
        return this;
    }

    public ProfileQualificationRepresentation withStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
        return this;
    }

    public ProfileQualificationRepresentation withAwardYear(Integer awardYear) {
        this.awardYear = awardYear;
        return this;
    }

    public ProfileQualificationRepresentation withAwardMonth(Integer awardMonth) {
        this.awardMonth = awardMonth;
        return this;
    }

    public ProfileQualificationRepresentation withGrade(String grade) {
        this.grade = grade;
        return this;
    }

    public ProfileQualificationRepresentation withCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public ProfileQualificationRepresentation withDocumentRepresentation(DocumentRepresentation document) {
        this.document = document;
        return this;
    }

    public String getStartDateDisplay() {
        return startYear == null ? null : startMonth.toString() + BACK_SLASH + startYear.toString();
    }

    public String getAwardDateDisplay() {
        return awardYear == null ? null : awardMonth.toString() + BACK_SLASH + awardYear.toString();
    }

}