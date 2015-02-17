package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.resource.FileRepresentation;

public class FundingRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private Integer fundingSource;

    private String sponsor;

    private FileRepresentation document;

    private String description;

    private String value;

    private LocalDate awardDate;

    private String terms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(Integer fundingSource) {
        this.fundingSource = fundingSource;
    }

    public final String getSponsor() {
        return sponsor;
    }

    public final void setSponsor(String sponsor) {
        this.sponsor = sponsor;
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

    public final String getTerms() {
        return terms;
    }

    public final void setTerms(String terms) {
        this.terms = terms;
    }

}
