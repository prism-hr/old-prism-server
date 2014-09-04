package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.validation.annotation.DatePast;

public class ApplicationFundingDTO {

    private Integer id;

    @NotNull
    private Integer fundingSource;

    @NotNull
    private FileDTO document;

    @NotEmpty
    private String description;

    @NotEmpty
    private String value;

    @NotNull
    @DatePast
    private LocalDate awardDate;

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

    public FileDTO getDocument() {
        return document;
    }

    public void setDocument(FileDTO document) {
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
