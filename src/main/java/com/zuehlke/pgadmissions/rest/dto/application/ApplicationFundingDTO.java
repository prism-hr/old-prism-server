package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.validation.annotation.DateNotFuture;

public class ApplicationFundingDTO {

    private Integer id;

    @NotNull
    private Integer fundingSource;

    @NotEmpty
    @Size(max = 255)
    private String sponsor;
    
    @NotEmpty
    @Size(max = 2000)
    private String description;

    @NotEmpty
    @Size(max = 100)
    private String value;
    
    @NotNull
    @DateNotFuture
    private LocalDate awardDate;
    
    @Size(max = 2000)
    private String terms;
    
    private FileDTO document;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public final String getSponsor() {
        return sponsor;
    }

    public final void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public Integer getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(Integer fundingSource) {
        this.fundingSource = fundingSource;
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
    
    public FileDTO getDocument() {
        return document;
    }

    public void setDocument(FileDTO document) {
        this.document = document;
    }
    
}
