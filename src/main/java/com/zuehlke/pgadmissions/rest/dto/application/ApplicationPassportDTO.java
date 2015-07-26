package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import uk.co.alumeni.prism.utils.validation.DateNotFuture;
import uk.co.alumeni.prism.utils.validation.DateNotPast;

public class ApplicationPassportDTO {

    @NotEmpty
    private String number;

    @NotEmpty
    private String name;

    @NotNull
    @DateNotFuture
    private LocalDate issueDate;

    @NotNull
    @DateNotPast
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
}
