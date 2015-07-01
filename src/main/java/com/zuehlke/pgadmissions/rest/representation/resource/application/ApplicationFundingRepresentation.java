package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;

public class ApplicationFundingRepresentation extends ApplicationSectionRepresentation {

    private Integer id;

    private ImportedEntitySimpleRepresentation fundingSource;

    private String sponsor;

    private String description;

    private String value;

    private LocalDate awardDate;

    private String terms;

    private DocumentRepresentation document;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ImportedEntitySimpleRepresentation getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(ImportedEntitySimpleRepresentation fundingSource) {
        this.fundingSource = fundingSource;
    }

    public final String getSponsor() {
        return sponsor;
    }

    public final void setSponsor(String sponsor) {
        this.sponsor = sponsor;
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

    public DocumentRepresentation getDocument() {
        return document;
    }

    public void setDocument(DocumentRepresentation document) {
        this.document = document;
    }

    public ApplicationFundingRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationFundingRepresentation withFundingSource(ImportedEntitySimpleRepresentation fundingSource) {
        this.fundingSource = fundingSource;
        return this;
    }

    public ApplicationFundingRepresentation withSponsor(String sponsor) {
        this.sponsor = sponsor;
        return this;
    }

    public ApplicationFundingRepresentation withDescription(String description) {
        this.description = description;
        return this;
    }

    public ApplicationFundingRepresentation withValue(String value) {
        this.value = value;
        return this;
    }

    public ApplicationFundingRepresentation withAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
        return this;
    }

    public ApplicationFundingRepresentation withTerms(String terms) {
        this.terms = terms;
        return this;
    }

    public ApplicationFundingRepresentation withDocument(DocumentRepresentation document) {
        this.document = document;
        return this;
    }

}
