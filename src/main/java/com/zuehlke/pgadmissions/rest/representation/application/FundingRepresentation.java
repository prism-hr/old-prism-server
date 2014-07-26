package com.zuehlke.pgadmissions.rest.representation.application;

import org.joda.time.LocalDate;

public class FundingRepresentation {

    private String fundingSource;

    private FileRepresentation document;

    private String description;

    private String value;

    private LocalDate awardDate;

    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public FileRepresentation getDocument() {
        return document;
    }

    public void setDocument(FileRepresentation document) {
        this.document = document;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }
}
