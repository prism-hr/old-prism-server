package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

public class ApplicationPassportRepresentation extends ApplicationSectionRepresentation {

    private String number;

    private String name;

    private LocalDate issueDate;

    private LocalDate expiryDate;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public ApplicationPassportRepresentation withNumber(String number) {
        this.number = number;
        return this;
    }

    public ApplicationPassportRepresentation withName(String name) {
        this.name = name;
        return this;
    }
    
    public ApplicationPassportRepresentation withIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
        return this;
    }
    
    public ApplicationPassportRepresentation withExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }  
    
}
