package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class AdvertDTO {

    @NotEmpty
    @Size(max = 1000)
    private String summary;

    @Size(max = 20000)
    private String description;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
