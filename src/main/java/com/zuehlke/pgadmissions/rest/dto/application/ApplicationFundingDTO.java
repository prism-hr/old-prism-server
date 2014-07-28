package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.rest.representation.application.FileRepresentation;
import org.joda.time.DateTime;

public class ApplicationFundingDTO {

    private Integer id;

    private Integer fundingSource;

    private FileRepresentation document;

    private String description;

    private String value;

    private DateTime awardDate;

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

    public DateTime getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(DateTime awardDate) {
        this.awardDate = awardDate;
    }
}
