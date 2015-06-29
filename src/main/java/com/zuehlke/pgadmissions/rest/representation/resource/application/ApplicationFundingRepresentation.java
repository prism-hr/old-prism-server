package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;

public class ApplicationFundingRepresentation extends ApplicationSectionRepresentation {

    private Integer id;
    
    private ImportedEntitySimpleRepresentation fundingSourceMapping;

    private String sponsor;

    private DocumentRepresentation document;

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
        return fundingSourceMapping.getId();
    }

    public void setFundingSource(Integer fundingSource) {
        this.fundingSourceMapping = new ImportedEntitySimpleRepresentation().withId(fundingSource);
    }
    
    public ImportedEntitySimpleRepresentation getFundingSourceMapping() {
        return fundingSourceMapping;
    }

    public void setFundingSourceMapping(ImportedEntitySimpleRepresentation fundingSourceMapping) {
        this.fundingSourceMapping = fundingSourceMapping;
    }

    public final String getSponsor() {
        return sponsor;
    }

    public final void setSponsor(String sponsor) {
        this.sponsor = sponsor;
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

    public final String getTerms() {
        return terms;
    }

    public final void setTerms(String terms) {
        this.terms = terms;
    }

}
