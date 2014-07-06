package com.zuehlke.pgadmissions.rest.domain.application;

import org.joda.time.LocalDate;

public class FundingRepresentation {

    private ImportedEntityRepresentation fundingSource;

    private DocumentRepresentation document;

    private String description;

    private String value;

    private LocalDate awardDate;

    public ImportedEntityRepresentation getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(ImportedEntityRepresentation fundingSource) {
        this.fundingSource = fundingSource;
    }

    public DocumentRepresentation getDocument() {
        return document;
    }

    public void setDocument(DocumentRepresentation document) {
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