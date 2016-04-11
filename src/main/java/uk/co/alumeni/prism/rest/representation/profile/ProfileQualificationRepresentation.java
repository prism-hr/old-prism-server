package uk.co.alumeni.prism.rest.representation.profile;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRelationInvitationRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationSectionRepresentation;

public class ProfileQualificationRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private ResourceRelationInvitationRepresentation resource;

    private LocalDate startDate;

    private LocalDate awardDate;

    private String grade;

    private Boolean completed;

    private DocumentRepresentation document;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ResourceRelationInvitationRepresentation getResource() {
        return resource;
    }

    public void setResource(ResourceRelationInvitationRepresentation resource) {
        this.resource = resource;
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

    public ProfileQualificationRepresentation withResource(ResourceRelationInvitationRepresentation resource) {
        this.resource = resource;
        return this;
    }

    public ProfileQualificationRepresentation withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ProfileQualificationRepresentation withAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
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
        return startDate != null ? "" + startDate.getMonthOfYear() + '/' + startDate.getYear() : null;
    }

    public String getAwardDateDisplay() {
        return awardDate != null ? "" + awardDate.getMonthOfYear() + '/' + awardDate.getYear() : null;
    }

}
